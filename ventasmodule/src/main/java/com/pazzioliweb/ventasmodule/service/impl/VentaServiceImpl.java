package com.pazzioliweb.ventasmodule.service.impl;

import com.pazzioliweb.commonbacken.events.VentaCompletadaEvent;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import com.pazzioliweb.metodospagomodule.repositori.MetodosPagoRepository;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.entity.Existencias;
import com.pazzioliweb.productosmodule.entity.Precios;
import com.pazzioliweb.productosmodule.entity.PreciosProductoVariante;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.productosmodule.repositori.ExistenciasRepository;
import com.pazzioliweb.productosmodule.repositori.PreciosProductoVarianteRepository;
import com.pazzioliweb.productosmodule.repositori.ProductoVarianteRepository;
import com.pazzioliweb.productosmodule.repositori.PreciosRepository;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;
import com.pazzioliweb.cajerosmodule.repositori.CajeroRepository;
import com.pazzioliweb.cajerosmodule.service.DetalleCajeroService;
import com.pazzioliweb.vendedoresmodule.entity.Vendedores;
import com.pazzioliweb.vendedoresmodule.repositori.VendedoresRepository;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.ventasmodule.dtos.DetalleVentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import com.pazzioliweb.ventasmodule.entity.DetalleVenta;
import com.pazzioliweb.ventasmodule.entity.Venta;
import com.pazzioliweb.ventasmodule.entity.VentaMetodoPago;
import com.pazzioliweb.ventasmodule.exception.VentaException;
import com.pazzioliweb.ventasmodule.mapper.VentaMapper;
import com.pazzioliweb.ventasmodule.repository.VentaRepository;
import com.pazzioliweb.ventasmodule.repository.VentaSpecification;
import com.pazzioliweb.ventasmodule.service.VentaService;
import com.pazzioliweb.ventasmodule.repository.CuentaPorCobrarRepository;
import com.pazzioliweb.ventasmodule.service.CuentaPorCobrarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final VentaMapper ventaMapper;
    private final ProductoVarianteRepository productoVarianteRepository;
    private final PreciosProductoVarianteRepository preciosProductoVarianteRepository;
    private final PreciosRepository preciosRepository;
    private final ExistenciasRepository existenciasRepository;
    private final MetodosPagoRepository metodosPagoRepository;
    private final TercerosRepository tercerosRepository;
    private final BodegasRepository bodegasRepository;
    private final CajeroRepository cajeroRepository;
    private final VendedoresRepository vendedoresRepository;
    private final DetalleCajeroService detalleCajeroService;
    private final CuentaPorCobrarService cuentaPorCobrarService;
    private final CuentaPorCobrarRepository cuentaPorCobrarRepository;
    private final RedisTemplate<String, DatosSesiones> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public VentaServiceImpl(VentaRepository ventaRepository, VentaMapper ventaMapper,
                            ProductoVarianteRepository productoVarianteRepository,
                            PreciosProductoVarianteRepository preciosProductoVarianteRepository,
                            PreciosRepository preciosRepository,
                            ExistenciasRepository existenciasRepository,
                            MetodosPagoRepository metodosPagoRepository,
                            TercerosRepository tercerosRepository,
                            BodegasRepository bodegasRepository,
                            CajeroRepository cajeroRepository,
                            VendedoresRepository vendedoresRepository,
                            DetalleCajeroService detalleCajeroService,
                            CuentaPorCobrarService cuentaPorCobrarService,
                            CuentaPorCobrarRepository cuentaPorCobrarRepository,
                            RedisTemplate<String, DatosSesiones> redisTemplate,
                            ApplicationEventPublisher eventPublisher) {
        this.ventaRepository = ventaRepository;
        this.ventaMapper = ventaMapper;
        this.productoVarianteRepository = productoVarianteRepository;
        this.preciosProductoVarianteRepository = preciosProductoVarianteRepository;
        this.preciosRepository = preciosRepository;
        this.existenciasRepository = existenciasRepository;
        this.metodosPagoRepository = metodosPagoRepository;
        this.tercerosRepository = tercerosRepository;
        this.bodegasRepository = bodegasRepository;
        this.cajeroRepository = cajeroRepository;
        this.vendedoresRepository = vendedoresRepository;
        this.detalleCajeroService = detalleCajeroService;
        this.cuentaPorCobrarService = cuentaPorCobrarService;
        this.cuentaPorCobrarRepository = cuentaPorCobrarRepository;
        this.redisTemplate = redisTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public VentaDTO crearVenta(VentaDTO ventaDTO) {

        // Verificar stock disponible antes de crear la venta
        for (DetalleVentaDTO detalleDTO : ventaDTO.getItems()) {
            ProductoVariante variante = productoVarianteRepository.findBySku(detalleDTO.getCodigoProducto())
                    .orElseThrow(() -> new VentaException("Producto variante no encontrado: " + detalleDTO.getCodigoProducto()));

            Optional<Existencias> existenciaOpt = existenciasRepository
                    .findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(variante.getProductoVarianteId(), ventaDTO.getBodegaId());

            if (existenciaOpt.isEmpty()) {
                throw new VentaException("No hay existencias registradas para el producto " + detalleDTO.getCodigoProducto() + " en la bodega " + ventaDTO.getBodegaId());
            }

            Existencias existencia = existenciaOpt.get();
            BigDecimal cantidadVendida = BigDecimal.valueOf(detalleDTO.getCantidad());
            if (existencia.getExistencia().compareTo(cantidadVendida) < 0) {
                throw new VentaException("Stock insuficiente para " + detalleDTO.getCodigoProducto() + ". Disponible: " + existencia.getExistencia() + ", Solicitado: " + cantidadVendida);
            }
        }

        Venta venta = ventaMapper.toEntity(ventaDTO);
        venta.setEstado("PENDIENTE");
        venta.setFechaCreacion(LocalDate.now());

        // Resolver relaciones desde IDs
        Terceros cliente = tercerosRepository.findById(ventaDTO.getClienteId().intValue())
                .orElseThrow(() -> new VentaException("Cliente no encontrado: " + ventaDTO.getClienteId()));
        venta.setCliente(cliente);

        Bodegas bodega = bodegasRepository.findByCodigo(ventaDTO.getBodegaId())
                .orElseThrow(() -> new VentaException("Bodega no encontrada: " + ventaDTO.getBodegaId()));
        venta.setBodega(bodega);

        if (ventaDTO.getCajeroId() != null) {
            // Si viene cajeroId explícito en el DTO, usarlo
            Cajero cajero = cajeroRepository.findById(ventaDTO.getCajeroId().intValue())
                    .orElseThrow(() -> new VentaException("Cajero no encontrado: " + ventaDTO.getCajeroId()));
            venta.setCajero(cajero);
        } else {
            // Auto-detectar cajero desde la sesión activa (Redis)
            DatosSesiones sesionActiva = obtenerSesionActiva();
            if (sesionActiva != null && sesionActiva.getCajeroId() != null) {
                Cajero cajero = cajeroRepository.findById(sesionActiva.getCajeroId())
                        .orElseThrow(() -> new VentaException("Cajero de sesión no encontrado: " + sesionActiva.getCajeroId()));
                venta.setCajero(cajero);
            }
        }

        // Asignar vendedor si viene en el DTO
        if (ventaDTO.getVendedorId() != null) {
            Vendedores vendedor = vendedoresRepository.findById(ventaDTO.getVendedorId())
                    .orElseThrow(() -> new VentaException("Vendedor no encontrado: " + ventaDTO.getVendedorId()));
            venta.setVendedor(vendedor);
        }

        // Resolver métodos de pago desde la lista
        if (ventaDTO.getMetodosPago() != null && !ventaDTO.getMetodosPago().isEmpty()) {
            List<VentaMetodoPago> metodosPagoEntities = new ArrayList<>();
            for (VentaMetodoPagoDTO mpDTO : ventaDTO.getMetodosPago()) {
                MetodosPago mp = metodosPagoRepository.findById(mpDTO.getMetodoPagoId().intValue())
                        .orElseThrow(() -> new VentaException("Método de pago no encontrado: " + mpDTO.getMetodoPagoId()));
                VentaMetodoPago vmp = new VentaMetodoPago();
                vmp.setVenta(venta);
                vmp.setMetodoPago(mp);
                vmp.setMonto(mpDTO.getMonto());
                vmp.setReferencia(mpDTO.getReferencia());
                vmp.setPlazoEnDias(mpDTO.getPlazoEnDias());
                metodosPagoEntities.add(vmp);
            }
            venta.setMetodosPago(metodosPagoEntities);
        }

        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal ivaTotal = BigDecimal.ZERO;
        BigDecimal descuentosTotal = BigDecimal.ZERO;
        for (DetalleVenta detalle : venta.getItems()) {
            detalle.setVenta(venta);
            subtotal = subtotal.add(detalle.getTotal());
            ivaTotal = ivaTotal.add(detalle.getIva());
            descuentosTotal = descuentosTotal.add(detalle.getDescuento());
        }
        venta.setGravada(subtotal.subtract(ivaTotal));
        venta.setIva(ivaTotal);
        venta.setDescuentos(descuentosTotal);
        venta.setTotalVenta(subtotal);
        Venta ven=ventaRepository.save(venta);
        final VentaDTO ventacreada= ventaMapper.toDto(ven);

        // ✅ Si la venta trae métodos de pago → completarla automáticamente
        //    (esto registra el movimiento en cajero y genera CxC si hay crédito)
        if (ventaDTO.getMetodosPago() != null && !ventaDTO.getMetodosPago().isEmpty()) {
            completarVenta(ven.getId(), ventaDTO.getMetodosPago());
        }

        // Reducir stock después de guardar la venta
        for (DetalleVentaDTO detalleDTO : ventaDTO.getItems()) {
            ProductoVariante variante = productoVarianteRepository.findBySku(detalleDTO.getCodigoProducto()).get();
            Existencias existencia = existenciasRepository
                    .findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(variante.getProductoVarianteId(), ventaDTO.getBodegaId()).get();
            BigDecimal cantidadVendida = BigDecimal.valueOf(detalleDTO.getCantidad());
            existencia.setExistencia(existencia.getExistencia().subtract(cantidadVendida));
            existenciasRepository.save(existencia);
        }

        // Adjust prices if needed
        ajustarPreciosVenta(ventaDTO.getItems());

        return ventacreada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaDTO> getVentasByCliente(Long clienteId) {
        return ventaRepository.findVentasByClienteId(clienteId)
                .stream()
                .map(ventaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDTO getVentaByNumero(String numeroVenta) {
        Venta venta = ventaRepository.findByNumeroVentaWithItems(numeroVenta)
                .orElseThrow(() -> new VentaException("Venta no encontrada"));
        return ventaMapper.toDto(venta);
    }

    @Override
    @Transactional
    public void ajustarPreciosVenta(List<DetalleVentaDTO> detalles) {
        for (DetalleVentaDTO dto : detalles) {
            if (dto.isPrecioAjustado()) {
                // Find variant by codigoProducto (assume it's sku)
                ProductoVariante variante = productoVarianteRepository.findBySku(dto.getCodigoProducto())
                        .orElseThrow(() -> new VentaException("Producto variante no encontrado: " + dto.getCodigoProducto()));

                // Assume precioId for "venta" is 2 or something, need to find by descripcion
                Precios precioVenta = preciosRepository.findByDescripcion("VENTA")
                        .orElseThrow(() -> new VentaException("Tipo de precio VENTA no encontrado"));

                Optional<PreciosProductoVariante> existente = preciosProductoVarianteRepository
                        .findByProductoVariante_ProductoVarianteIdAndPrecio_PrecioId(variante.getProductoVarianteId(), precioVenta.getPrecioId());

                PreciosProductoVariante precioEntity;
                if (existente.isPresent()) {
                    precioEntity = existente.get();
                    precioEntity.setValor(dto.getPrecioUnitario().doubleValue());
                    precioEntity.setFechaModificacion(LocalDateTime.now());
                } else {
                    precioEntity = new PreciosProductoVariante();
                    precioEntity.setProductoVariante(variante);
                    precioEntity.setPrecio(precioVenta);
                    precioEntity.setValor(dto.getPrecioUnitario().doubleValue());
                    precioEntity.setFechaCreacion(LocalDateTime.now());
                    precioEntity.setFechaModificacion(LocalDateTime.now());
                }
                preciosProductoVarianteRepository.save(precioEntity);
            }
        }
    }

    @Override
    @Transactional
    public void completarVenta(Long ventaId, List<VentaMetodoPagoDTO> metodosPagoDTO) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new VentaException("Venta no encontrada"));

        if (!"PENDIENTE".equals(venta.getEstado())) {
            throw new VentaException("La venta ya ha sido completada o anulada");
        }

        if (metodosPagoDTO == null || metodosPagoDTO.isEmpty()) {
            throw new VentaException("Debe especificar al menos un método de pago");
        }

        // Validar que la suma de montos cubra el total de la venta
        BigDecimal totalPagado = metodosPagoDTO.stream()
                .map(VentaMetodoPagoDTO::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPagado.compareTo(venta.getTotalVenta()) < 0) {
            throw new VentaException("El total pagado (" + totalPagado + ") no cubre el total de la venta (" + venta.getTotalVenta() + ")");
        }

        // ✅ Validar permisos y cupo si hay método de pago a Crédito
        boolean tieneCredito = metodosPagoDTO.stream().anyMatch(mp -> {
            MetodosPago metodo = metodosPagoRepository.findById(mp.getMetodoPagoId().intValue()).orElse(null);
            return metodo != null && metodo.getTipoNegociacion() == MetodosPago.TipoNegociacion.Credito;
        });

        if (tieneCredito) {
            // 1. Verificar que el cliente tenga cupo habilitado (cupo >= 1 = permitido, cupo = 0 = no permitido)
            Terceros cliente = venta.getCliente();
            if (cliente.getCupo() == null || cliente.getCupo() <= 0) {
                throw new VentaException("El cliente " + cliente.getNombre1() + " no tiene habilitado crédito (cupo en 0)");
            }

            // 2. Calcular monto de crédito solicitado
            BigDecimal montoCredito = metodosPagoDTO.stream()
                    .filter(mp -> {
                        MetodosPago metodo = metodosPagoRepository.findById(mp.getMetodoPagoId().intValue()).orElse(null);
                        return metodo != null && metodo.getTipoNegociacion() == MetodosPago.TipoNegociacion.Credito;
                    })
                    .map(VentaMetodoPagoDTO::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 3. Verificar cupo disponible = cupo - saldo pendiente CxC
            BigDecimal saldoPendiente = cuentaPorCobrarRepository.sumSaldoPendienteByClienteId(cliente.getTerceroId());
            BigDecimal cupoTotal = BigDecimal.valueOf(cliente.getCupo());
            BigDecimal cupoDisponible = cupoTotal.subtract(saldoPendiente);

            if (montoCredito.compareTo(cupoDisponible) > 0) {
                throw new VentaException("Cupo de crédito insuficiente para " + cliente.getNombre1()
                        + ". Cupo total: " + cupoTotal
                        + ", Saldo pendiente: " + saldoPendiente
                        + ", Disponible: " + cupoDisponible
                        + ", Solicitado: " + montoCredito);
            }
        }

        // Limpiar métodos de pago anteriores si los hay
        if (venta.getMetodosPago() != null) {
            venta.getMetodosPago().clear();
        } else {
            venta.setMetodosPago(new ArrayList<>());
        }

        // Crear y asociar cada método de pago
        for (VentaMetodoPagoDTO mpDTO : metodosPagoDTO) {
            MetodosPago mp = metodosPagoRepository.findById(mpDTO.getMetodoPagoId().intValue())
                    .orElseThrow(() -> new VentaException("Método de pago no encontrado: " + mpDTO.getMetodoPagoId()));
            VentaMetodoPago vmp = new VentaMetodoPago();
            vmp.setVenta(venta);
            vmp.setMetodoPago(mp);
            vmp.setMonto(mpDTO.getMonto());
            vmp.setReferencia(mpDTO.getReferencia());
            vmp.setPlazoEnDias(mpDTO.getPlazoEnDias());
            venta.getMetodosPago().add(vmp);
        }

        venta.setEstado("COMPLETADA");
        Venta ventaGuardada = ventaRepository.save(venta);

        // ✅ Registrar movimiento en cajero y generar CxC si hay crédito
        DatosSesiones sesionComp = obtenerSesionActiva();
        if (sesionComp == null || sesionComp.getDetalleCajeroId() == null) {
            throw new VentaException("No se puede completar la venta: no hay sesión de cajero activa. Inicie sesión nuevamente.");
        }

        BigDecimal montoEfectivo = BigDecimal.ZERO;
        BigDecimal montoElectronico = BigDecimal.ZERO;
        BigDecimal montoCredito = BigDecimal.ZERO;
        int plazoDiasCredito = 30;

        for (VentaMetodoPago vmp : ventaGuardada.getMetodosPago()) {
            MetodosPago mp = vmp.getMetodoPago();
            if (mp.getTipoNegociacion() == MetodosPago.TipoNegociacion.Credito) {
                montoCredito = montoCredito.add(vmp.getMonto());
                if (vmp.getPlazoEnDias() != null && vmp.getPlazoEnDias() > 0) {
                    plazoDiasCredito = vmp.getPlazoEnDias();
                }
            } else {
                String sigla = mp.getSigla().toUpperCase();
                if (sigla.startsWith("EF")) {
                    montoEfectivo = montoEfectivo.add(vmp.getMonto());
                } else {
                    montoElectronico = montoElectronico.add(vmp.getMonto());
                }
            }
        }

        detalleCajeroService.registrarMovimiento(
                sesionComp.getDetalleCajeroId(),
                MovimientoCajero.TipoMovimiento.VENTA,
                ventaGuardada.getNumeroVenta(),
                ventaGuardada.getId(),
                ventaGuardada.getTotalVenta(),
                ventaGuardada.getGravada(),
                montoEfectivo,
                montoElectronico,
                "Venta " + ventaGuardada.getNumeroVenta()
        );

        if (montoCredito.compareTo(BigDecimal.ZERO) > 0) {
            detalleCajeroService.registrarMovimiento(
                    sesionComp.getDetalleCajeroId(),
                    MovimientoCajero.TipoMovimiento.CUENTA_POR_COBRAR,
                    ventaGuardada.getNumeroVenta(),
                    ventaGuardada.getId(),
                    montoCredito,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    "CxC Venta " + ventaGuardada.getNumeroVenta()
            );

            Terceros cliente = ventaGuardada.getCliente();
            cuentaPorCobrarService.crearDesdeVenta(ventaGuardada, cliente, montoCredito, plazoDiasCredito);
        }

        // ✅ Publicar evento para que facturación electrónica genere la factura automáticamente
        try {
            Integer cajeroId = ventaGuardada.getCajero() != null ? ventaGuardada.getCajero().getCajeroId() : null;
            eventPublisher.publishEvent(new VentaCompletadaEvent(this, ventaGuardada.getId(), cajeroId));
        } catch (Exception e) {
            System.out.println("Error al publicar evento VentaCompletada (facturación): " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void devolverVenta(Long ventaId, List<DetalleVentaDTO> detallesDevueltos) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new VentaException("Venta no encontrada"));

        if (!"COMPLETADA".equals(venta.getEstado())) {
            throw new VentaException("Solo se pueden devolver ventas completadas");
        }

        // Restaurar inventario por cada detalle devuelto
        for (DetalleVentaDTO dto : detallesDevueltos) {
            // Find the original detalle in venta
            DetalleVenta detalleOriginal = venta.getItems().stream()
                    .filter(d -> d.getId().equals(dto.getId()))
                    .findFirst()
                    .orElseThrow(() -> new VentaException("Detalle de venta no encontrado: " + dto.getId()));

            if (dto.getCantidad() > detalleOriginal.getCantidad()) {
                throw new VentaException("Cantidad a devolver no puede exceder la cantidad vendida");
            }

            // Find variante and existencia
            ProductoVariante variante = productoVarianteRepository.findBySku(detalleOriginal.getCodigoProducto()).get();
            Existencias existencia = existenciasRepository
                    .findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(variante.getProductoVarianteId(), venta.getBodega().getCodigo()).get();

            // Restaurar stock
            BigDecimal cantidadDevuelta = BigDecimal.valueOf(dto.getCantidad());
            existencia.setExistencia(existencia.getExistencia().add(cantidadDevuelta));
            existenciasRepository.save(existencia);

            // Aquí se podría ajustar totales o crear registro de devolución, pero por simplicidad, solo restaurar stock
        }

        // Cambiar estado a DEVUELTA si todos los items son devueltos, o parcial
        // Por ahora, marcar como DEVUELTA
        venta.setEstado("DEVUELTA");
        ventaRepository.save(venta);

        // ✅ Registrar movimiento DEVOLUCION en cajero
        DatosSesiones sesionDev = obtenerSesionActiva();
        if (sesionDev != null && sesionDev.getDetalleCajeroId() != null) {
            try {
                detalleCajeroService.registrarMovimiento(
                        sesionDev.getDetalleCajeroId(),
                        MovimientoCajero.TipoMovimiento.DEVOLUCION,
                        venta.getNumeroVenta(),
                        venta.getId(),
                        venta.getTotalVenta(),
                        venta.getGravada(),
                        venta.getTotalVenta(), // devolución = todo efectivo
                        BigDecimal.ZERO,
                        "Devolución venta " + venta.getNumeroVenta()
                );
            } catch (Exception e) {
                System.out.println("Error al registrar devolución en cajero: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void anularVenta(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new VentaException("Venta no encontrada"));

        if (!"PENDIENTE".equals(venta.getEstado())) {
            throw new VentaException("Solo se pueden anular ventas pendientes");
        }

        venta.setEstado("ANULADA");
        ventaRepository.save(venta);

        // ✅ Registrar movimiento ANULACION en cajero
        DatosSesiones sesionAnul = obtenerSesionActiva();
        if (sesionAnul != null && sesionAnul.getDetalleCajeroId() != null) {
            try {
                detalleCajeroService.registrarMovimiento(
                        sesionAnul.getDetalleCajeroId(),
                        MovimientoCajero.TipoMovimiento.ANULACION,
                        venta.getNumeroVenta(),
                        venta.getId(),
                        venta.getTotalVenta(),
                        venta.getGravada(),
                        venta.getTotalVenta(),
                        BigDecimal.ZERO,
                        "Anulación venta " + venta.getNumeroVenta()
                );
            } catch (Exception e) {
                System.out.println("Error al registrar anulación en cajero: " + e.getMessage());
            }
        }
    }

    @Override
    public Double getTotalVentasByFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        return ventaRepository.getTotalVentasByFecha(fechaInicio, fechaFin).orElse(0.0);
    }

    @Override
    public Long getCantidadVendidaByProducto(String codigoProducto, LocalDate fechaInicio, LocalDate fechaFin) {
        return ventaRepository.getCantidadVendidaByProducto(codigoProducto, fechaInicio, fechaFin).orElse(0L);
    }

    @Override
    public Double getTotalVentasByCajero(Integer cajeroId, LocalDate fechaInicio, LocalDate fechaFin) {
        return ventaRepository.getTotalVentasByCajero(cajeroId, fechaInicio, fechaFin).orElse(0.0);
    }
    @Transactional
    @Override
    public List<VentaDTO> getVentasByFiltros(String numeroventa,Long terceroId, Integer vendedorId, Integer cajeroId,
                                             LocalDate fechaInicio, LocalDate fechaFin) {
        return ventaRepository
                .findAll(VentaSpecification.conFiltros(numeroventa,terceroId, vendedorId, cajeroId, fechaInicio, fechaFin))
                .stream()
                .map(ventaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Long getUltimaVentaId() {
        return ventaRepository.getUltimaVentaId();
    }

    /**
     * Obtiene la sesión activa del usuario desde Redis a través del SecurityContextHolder.
     * Retorna null si no hay sesión o si no se puede obtener.
     */
    private DatosSesiones obtenerSesionActiva() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getCredentials() != null) {
                String sessionId = auth.getCredentials().toString();
                return redisTemplate.opsForValue().get(sessionId);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener sesión activa: " + e.getMessage());
        }
        return null;
    }
}
