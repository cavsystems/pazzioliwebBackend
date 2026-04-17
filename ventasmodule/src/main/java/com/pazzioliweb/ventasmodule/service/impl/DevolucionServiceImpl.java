package com.pazzioliweb.ventasmodule.service.impl;

import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;
import com.pazzioliweb.cajerosmodule.repositori.CajeroRepository;
import com.pazzioliweb.cajerosmodule.service.DetalleCajeroService;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.productosmodule.entity.Existencias;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.repositori.ExistenciasRepository;
import com.pazzioliweb.productosmodule.repositori.ProductoVarianteRepository;
import com.pazzioliweb.ventasmodule.dtos.*;
import com.pazzioliweb.ventasmodule.entity.*;
import com.pazzioliweb.ventasmodule.exception.CotizacionException;
import com.pazzioliweb.ventasmodule.exception.DevolucionException;
import com.pazzioliweb.ventasmodule.exception.PedidoException;
import com.pazzioliweb.ventasmodule.exception.VentaException;
import com.pazzioliweb.ventasmodule.repository.DevolucionRepository;
import com.pazzioliweb.ventasmodule.repository.PedidoRepository;
import com.pazzioliweb.ventasmodule.repository.VentaRepository;
import com.pazzioliweb.ventasmodule.service.DevolucionService;
import com.pazzioliweb.ventasmodule.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DevolucionServiceImpl implements DevolucionService {

    private final VentaRepository ventaRepository;
    private final DevolucionRepository devolucionRepository;
    private final ExistenciasRepository existenciasRepository;
    private final ProductoVarianteRepository productoVarianteRepository;
    private final DetalleCajeroService detalleCajeroService;
    private final CajeroRepository cajeroRepository;
    private final RedisTemplate<String, DatosSesiones> redisTemplate;
  @Autowired
  private PedidoService pedidoService;
@Autowired
 private  PedidoRepository pedidoRepository;
    @Autowired
    public DevolucionServiceImpl(
            VentaRepository ventaRepository,
            DevolucionRepository devolucionRepository,
            ExistenciasRepository existenciasRepository,
            ProductoVarianteRepository productoVarianteRepository,
            DetalleCajeroService detalleCajeroService,
            CajeroRepository cajeroRepository,
            RedisTemplate<String, DatosSesiones> redisTemplate) {
        this.ventaRepository = ventaRepository;
        this.devolucionRepository = devolucionRepository;
        this.existenciasRepository = existenciasRepository;
        this.productoVarianteRepository = productoVarianteRepository;
        this.detalleCajeroService = detalleCajeroService;
        this.cajeroRepository = cajeroRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public DevolucionDTO registrarDevolucion(DevolucionRequestDTO request) {

        // 1. Obtener la venta
        Venta venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> new VentaException("Venta no encontrada: " + request.getVentaId()));

        // 2. Solo se pueden devolver ventas COMPLETADAS o DEVOLUCION_PARCIAL
        if (!"COMPLETADA".equals(venta.getEstado()) && !"DEVOLUCION_PARCIAL".equals(venta.getEstado())) {
            throw new VentaException("Solo se pueden registrar devoluciones en ventas COMPLETADAS. Estado actual: " + venta.getEstado());
        }

        // 3. Generar número de devolución
        String numeroDevolucion = generarNumeroDevolucion(venta);

        // 4. Crear encabezado de devolución
        Devolucion devolucion = new Devolucion();
        devolucion.setVenta(venta);
        devolucion.setNumeroDevolucion(numeroDevolucion);
        devolucion.setMotivo(request.getMotivo());
        devolucion.setObservaciones(request.getObservaciones());
        devolucion.setEstado("REGISTRADA");
        devolucion.setUsuarioCreacion(request.getUsuarioCreacion());
        devolucion.setFechaCreacion(LocalDate.now());

        // Asociar cajero desde la sesión activa
        DatosSesiones sesionActual = obtenerSesionActiva();
        if (sesionActual != null && sesionActual.getCajeroId() != null) {
            cajeroRepository.findById(sesionActual.getCajeroId()).ifPresent(devolucion::setCajero);
        }

        // 5. Procesar cada ítem
        List<DetalleDevolucion> detalles = new ArrayList<>();
        BigDecimal totalDevuelto = BigDecimal.ZERO;
        BigDecimal ivaDevuelto = BigDecimal.ZERO;

        for (DevolucionItemRequestDTO itemReq : request.getItems()) {
            if (itemReq.getCantidadDevolver() == null || itemReq.getCantidadDevolver() <= 0) {
                continue; // Ignorar ítems con cantidad 0 o nula
            }

            // Buscar el DetalleVenta original dentro de la venta
            DetalleVenta detalleOriginal = venta.getItems().stream()
                    .filter(d -> d.getId().equals(itemReq.getItemVentaId()))
                    .findFirst()
                    .orElseThrow(() -> new VentaException(
                            "Ítem de venta no encontrado: " + itemReq.getItemVentaId()));

            // Calcular cuánto ya se ha devuelto de este ítem
            Integer yaDevuelto = devolucionRepository.totalDevueltoPorDetalle(detalleOriginal.getId());
            if (yaDevuelto == null) yaDevuelto = 0;

            int disponibleParaDevolver = detalleOriginal.getCantidad() - yaDevuelto;
            if (itemReq.getCantidadDevolver() > disponibleParaDevolver) {
                throw new VentaException(
                        "La cantidad a devolver (" + itemReq.getCantidadDevolver() +
                                ") supera la cantidad disponible para devolución (" + disponibleParaDevolver +
                                ") del ítem: " + detalleOriginal.getDescripcionProducto());
            }

            // Calcular totales proporcionales de la línea
            BigDecimal cantDevolver = BigDecimal.valueOf(itemReq.getCantidadDevolver());
            BigDecimal cantVendida  = BigDecimal.valueOf(detalleOriginal.getCantidad());

            // Total de la línea original sin IVA (proporcional a cantidad devuelta)
            BigDecimal totalOriginalLinea = detalleOriginal.getTotal();
            BigDecimal ivaOriginalLinea   = detalleOriginal.getIva();

            // Calcular proporcionales según cantidad devuelta
            BigDecimal factorProporcional = cantDevolver.divide(cantVendida, 10, java.math.RoundingMode.HALF_UP);
            BigDecimal totalLineaDevuelta = totalOriginalLinea.multiply(factorProporcional)
                    .setScale(2, java.math.RoundingMode.HALF_UP);
            BigDecimal ivaLineaDevuelta   = ivaOriginalLinea.multiply(factorProporcional)
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            // Crear detalle de devolución
            DetalleDevolucion detalle = new DetalleDevolucion();
            detalle.setDevolucion(devolucion);
            detalle.setDetalleVenta(detalleOriginal);
            detalle.setCantidadDevuelta(itemReq.getCantidadDevolver());
            detalle.setPrecioUnitario(detalleOriginal.getPrecioUnitario());
            detalle.setIvaLinea(ivaLineaDevuelta);
            detalle.setTotalLinea(totalLineaDevuelta);
            detalle.setMotivo(itemReq.getMotivo() != null ? itemReq.getMotivo() : request.getMotivo());
            detalles.add(detalle);

            totalDevuelto = totalDevuelto.add(totalLineaDevuelta);
            ivaDevuelto   = ivaDevuelto.add(ivaLineaDevuelta);

            // 6. Restaurar inventario (existencias)
            restaurarInventario(detalleOriginal.getCodigoProducto(),
                    venta.getBodega().getCodigo(),
                    itemReq.getCantidadDevolver());
        }

        BigDecimal totalNeto = totalDevuelto; // totalDevuelto ya incluye IVA (es el total de línea)
        devolucion.setTotalDevuelto(totalDevuelto.subtract(ivaDevuelto)); // base gravada
        devolucion.setIvaDevuelto(ivaDevuelto);
        devolucion.setTotalNeto(totalNeto);
        devolucion.setItems(detalles);

        // 7. Guardar la devolución
        Devolucion guardada = devolucionRepository.save(devolucion);

        // 8. Actualizar estado de la venta
        actualizarEstadoVenta(venta);

        // 9. Registrar movimiento en cajero para el cuadre de caja
        registrarMovimientoEnCajero(venta, guardada, totalNeto);

        return toDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionDTO> getAllDevoluciones() {
        return devolucionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionDTO> getDevolucionesByVenta(Long ventaId) {
        return devolucionRepository.findByVenta_Id(ventaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DevolucionDTO getDevolucionById(Long devolucionId) {
        Devolucion dev = devolucionRepository.findById(devolucionId)
                .orElseThrow(() -> new VentaException("Devolución no encontrada: " + devolucionId));
        return toDTO(dev);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionDTO> getDevolucionesByCajero(Integer cajeroId) {
        return devolucionRepository.findByCajero_CajeroId(cajeroId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }



    @Override
    @Transactional
    public PedidoDTO convertirAPedido(Long Ventaid,Long DevolucionId) {
        Venta venta= ventaRepository.findById(Ventaid)
                .orElseThrow(() -> new DevolucionException("Cotización no encontrada"));



        // Crear PedidoDTO a partir de la cotización
        Long ultimopedido= pedidoRepository.getUltimopedidoId();
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setClienteId(venta.getCliente().getTerceroId().longValue());
        pedidoDTO.setBodegaId(venta.getBodega().getCodigo());
        pedidoDTO.setFechaEmision(LocalDate.now());
        pedidoDTO.setFechaEntregaEsperada(LocalDate.now().plusDays(7));
        pedidoDTO.setObservaciones("Generado desde devolución: " + DevolucionId);
        pedidoDTO.setSubtotal(venta.getGravada());
        pedidoDTO.setNumeroPedido(ultimopedido.toString());
        pedidoDTO.setIva(venta.getIva());
        pedidoDTO.setTotal(venta.getTotalVenta());
        pedidoDTO.setUsuarioCreacion(venta.getUsuarioCreacion());


        // Propagar cajero de la cotización al pedido
        if (venta.getCajero() != null) {
            pedidoDTO.setCajeroId(Long.valueOf(venta.getCajero().getCajeroId()));
        }

        // Propagar vendedor de la cotización al pedido
        if (venta.getVendedor() != null) {
            pedidoDTO.setVendedorId(venta.getVendedor().getVendedor_id());
        }

        // Mapear items
        List<DetallePedidoDTO> items = venta.getItems().stream().map(dc -> {
            DetallePedidoDTO dp = new DetallePedidoDTO();
            dp.setCodigoProducto(dc.getCodigoProducto());
            dp.setCodigoBarras(dc.getCodigoBarras());
            dp.setDescripcionProducto(dc.getDescripcionProducto());
            dp.setObservacionProducto(dc.getObservacionProducto() != null ? dc.getObservacionProducto() : "");
            dp.setReferenciaVariantes(dc.getReferenciaVariantes());
            dp.setCantidad(dc.getCantidad());
            dp.setPrecioUnitario(dc.getPrecioUnitario());
            dp.setDescuento(dc.getDescuento());
            dp.setIvaPorcentaje(dc.getIva());
            dp.setTotal(dc.getTotal());
            return dp;
        }).collect(Collectors.toList());
        pedidoDTO.setItems(items);

        // ── Bug 2 fix: retornar el pedido creado (con numeroPedido e id generados) ──
        PedidoDTO pedidoCreado = pedidoService.crearPedido(pedidoDTO);

        // ── Bug 1 fix: marcar la cotización como convertida a pedido ──────────


        return pedidoCreado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionDTO> getDevolucionesByFiltros(Integer cajeroId, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Devolucion> resultado;

        boolean tieneCajero = cajeroId != null;
        boolean tieneFechas = fechaInicio != null && fechaFin != null;

        if (tieneCajero && tieneFechas) {
            resultado = devolucionRepository.findByCajeroAndFechas(cajeroId, fechaInicio, fechaFin);
        } else if (tieneCajero) {
            resultado = devolucionRepository.findByCajero_CajeroId(cajeroId);
        } else if (tieneFechas) {
            resultado = devolucionRepository.findByFechas(fechaInicio, fechaFin);
        } else {
            resultado = devolucionRepository.findAll();

        }

        return resultado.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // =========================================================================
    // Métodos privados de apoyo
    // =========================================================================

    /**
     * Restaura las existencias en bodega para un producto devuelto.
     */
    private void restaurarInventario(String sku, int codigoBodega, int cantidad) {
        ProductoVariante variante = productoVarianteRepository.findBySku(sku)
                .orElseThrow(() -> new VentaException("Producto variante no encontrado: " + sku));

        Existencias existencia = existenciasRepository
                .findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(
                        variante.getProductoVarianteId(), codigoBodega)
                .orElseThrow(() -> new VentaException(
                        "Existencias no encontradas para SKU " + sku + " en bodega " + codigoBodega));

        existencia.setExistencia(existencia.getExistencia().add(BigDecimal.valueOf(cantidad)));
        existenciasRepository.save(existencia);
    }

    /**
     * Actualiza el estado de la venta según cuántos ítems han sido devueltos totalmente.
     * - Si todos los ítems de la venta fueron devueltos en su totalidad → DEVUELTA
     * - Si solo algunos ítems o parcialmente → DEVOLUCION_PARCIAL
     */
    private void actualizarEstadoVenta(Venta venta) {
        boolean todosDevueltos = venta.getItems().stream().allMatch(detalle -> {
            Integer totalDevuelto = devolucionRepository.totalDevueltoPorDetalle(detalle.getId());
            return (totalDevuelto != null) && totalDevuelto >= detalle.getCantidad();
        });

        venta.setEstado(todosDevueltos ? "DEVUELTA" : "DEVOLUCION_PARCIAL");
        ventaRepository.save(venta);
    }

    /**
     * Registra el movimiento DEVOLUCION en el cajero activo para afectar el cuadre de caja.
     * El monto se descarga proporcionalmente entre efectivo y electrónico según los
     * métodos de pago originales de la venta.
     */
    private void registrarMovimientoEnCajero(Venta venta, Devolucion devolucion, BigDecimal totalDevuelto) {
        DatosSesiones sesion = obtenerSesionActiva();
        if (sesion == null || sesion.getDetalleCajeroId() == null) {
            return; // Sin sesión activa, no se registra en caja
        }

        try {
            // Desglosar el monto devuelto en efectivo/electrónico
            // proporcional a cómo se pagó la venta original
            BigDecimal montoEfectivoDevuelto   = BigDecimal.ZERO;
            BigDecimal montoElectronicoDevuelto = BigDecimal.ZERO;

            if (venta.getMetodosPago() != null && !venta.getMetodosPago().isEmpty()) {
                BigDecimal totalVenta = venta.getTotalVenta();
                BigDecimal totalEfectivo = BigDecimal.ZERO;
                BigDecimal totalElectronico = BigDecimal.ZERO;

                for (VentaMetodoPago vmp : venta.getMetodosPago()) {
                    String sigla = vmp.getMetodoPago().getSigla().toUpperCase();
                    if (sigla.startsWith("EF")) {
                        totalEfectivo = totalEfectivo.add(vmp.getMonto());
                    } else {
                        totalElectronico = totalElectronico.add(vmp.getMonto());
                    }
                }

                if (totalVenta.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal propEfec = totalEfectivo.divide(totalVenta, 10, java.math.RoundingMode.HALF_UP);
                    BigDecimal propElec = totalElectronico.divide(totalVenta, 10, java.math.RoundingMode.HALF_UP);
                    montoEfectivoDevuelto   = totalDevuelto.multiply(propEfec).setScale(2, java.math.RoundingMode.HALF_UP);
                    montoElectronicoDevuelto = totalDevuelto.multiply(propElec).setScale(2, java.math.RoundingMode.HALF_UP);
                } else {
                    montoEfectivoDevuelto = totalDevuelto;
                }
            } else {
                // Sin métodos de pago registrados: todo efectivo
                montoEfectivoDevuelto = totalDevuelto;
            }

            detalleCajeroService.registrarMovimiento(
                    sesion.getDetalleCajeroId(),
                    MovimientoCajero.TipoMovimiento.DEVOLUCION,
                    devolucion.getNumeroDevolucion(),
                    devolucion.getId(),
                    totalDevuelto,
                    BigDecimal.ZERO, // costo
                    montoEfectivoDevuelto,
                    montoElectronicoDevuelto,
                    "Devolución " + devolucion.getNumeroDevolucion() +
                            " de venta " + venta.getNumeroVenta()
            );
        } catch (Exception e) {
            System.out.println("[DevolucionService] Error al registrar movimiento en cajero: " + e.getMessage());
        }
    }

    /**
     * Genera el número de devolución con el formato: DEV-{numeroVenta}-{secuencial}
     * Ejemplo: DEV-V-00001-1, DEV-V-00001-2
     */
    private String generarNumeroDevolucion(Venta venta) {
        long secuencial = devolucionRepository.countByVenta_Id(venta.getId()) + 1;
        return "DEV-" + venta.getNumeroVenta() + "-" + secuencial;
    }

    /**
     * Obtiene la sesión activa del usuario desde Redis.
     */
    private DatosSesiones obtenerSesionActiva() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getCredentials() != null) {
                String sessionId = auth.getCredentials().toString();
                return redisTemplate.opsForValue().get(sessionId);
            }
        } catch (Exception e) {
            System.out.println("[DevolucionService] Error al obtener sesión activa: " + e.getMessage());
        }
        return null;
    }

    /**
     * Mapea la entidad Devolucion a su DTO de respuesta.
     */
    private DevolucionDTO toDTO(Devolucion dev) {
        DevolucionDTO dto = new DevolucionDTO();
        dto.setId(dev.getId());
        dto.setVentaId(dev.getVenta().getId());
        dto.setNumeroVenta(dev.getVenta().getNumeroVenta());
        dto.setNombreCliente(dev.getVenta().getCliente().getRazonSocial());
        dto.setNumeroDevolucion(dev.getNumeroDevolucion());
        dto.setMotivo(dev.getMotivo());
        dto.setObservaciones(dev.getObservaciones());
        dto.setEstado(dev.getEstado());
        dto.setUsuarioCreacion(dev.getUsuarioCreacion());
        dto.setFechaCreacion(dev.getFechaCreacion());
        if (dev.getCajero() != null) {
            dto.setCajeroId(dev.getCajero().getCajeroId());
            dto.setCajeroNombre(dev.getCajero().getNombre());
        }
        dto.setTotalDevuelto(dev.getTotalDevuelto());
        dto.setIvaDevuelto(dev.getIvaDevuelto());
        dto.setTotalNeto(dev.getTotalNeto());

        if (dev.getItems() != null) {
            List<DetalleDevolucionDTO> itemDTOs = dev.getItems().stream().map(d -> {
                DetalleDevolucionDTO dDTO = new DetalleDevolucionDTO();
                dDTO.setId(d.getId());
                dDTO.setItemVentaId(d.getDetalleVenta().getId());
                dDTO.setCodigoProducto(d.getDetalleVenta().getCodigoProducto());
                dDTO.setDescripcionProducto(d.getDetalleVenta().getDescripcionProducto());
                dDTO.setCantidadDevuelta(d.getCantidadDevuelta());
                dDTO.setPrecioUnitario(d.getPrecioUnitario());
                dDTO.setIvaLinea(d.getIvaLinea());
                dDTO.setTotalLinea(d.getTotalLinea());
                dDTO.setMotivo(d.getMotivo());
                return dDTO;
            }).collect(Collectors.toList());
            dto.setItems(itemDTOs);
        }

        return dto;
    }
}




