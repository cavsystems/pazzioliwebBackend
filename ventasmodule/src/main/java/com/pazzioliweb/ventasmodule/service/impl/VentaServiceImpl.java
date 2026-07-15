package com.pazzioliweb.ventasmodule.service.impl;

import com.pazzioliweb.commonbacken.events.VentaCompletadaEvent;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.service.AsientoContableService;
import com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService;
import com.pazzioliweb.comprobantesmodule.service.ConfiguracionContableService;
import com.pazzioliweb.movimientosinventariomodule.service.MovimientoInventarioAutoService;
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
import com.pazzioliweb.ventasmodule.exception.FacturaDevueltaException;
import com.pazzioliweb.ventasmodule.exception.FacturaPendienteException;
import com.pazzioliweb.ventasmodule.exception.VentaException;
import com.pazzioliweb.ventasmodule.mapper.VentaMapper;
import com.pazzioliweb.ventasmodule.repository.VentaRepository;
import com.pazzioliweb.ventasmodule.repository.VentaSpecification;
import com.pazzioliweb.ventasmodule.service.VentaService;
import com.pazzioliweb.ventasmodule.entity.CuentaPorCobrar;
import com.pazzioliweb.ventasmodule.repository.CuentaPorCobrarRepository;
import com.pazzioliweb.ventasmodule.service.CuentaPorCobrarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
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
    private final AsignacionComprobanteService asignacionComprobante;
    private final AsientoContableService asientoService;
    private final ConfiguracionContableService configContable;
    private final MovimientoInventarioAutoService movimientoInventarioAutoService;
    @Autowired
    private com.pazzioliweb.comprobantesmodule.service.AsientoFallidoService asientoFallidoService;
    @Autowired
    private com.pazzioliweb.comprobantesmodule.service.PeriodoContableService periodoContableService;

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
                            ApplicationEventPublisher eventPublisher,
                            AsignacionComprobanteService asignacionComprobante,
                            AsientoContableService asientoService,
                            ConfiguracionContableService configContable,
                            MovimientoInventarioAutoService movimientoInventarioAutoService) {
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
        this.asignacionComprobante = asignacionComprobante;
        this.asientoService = asientoService;
        this.configContable = configContable;
        this.movimientoInventarioAutoService = movimientoInventarioAutoService;
    }

    /**
     * Determina si la venta tiene método de pago a crédito (afecta el tipo de comprobante: FC vs VC).
     */
    private boolean tieneCredito(VentaDTO ventaDTO) {
        if (ventaDTO.getMetodosPago() == null) return false;
        return tieneCredito(ventaDTO.getMetodosPago());
    }

    private boolean tieneCredito(List<VentaMetodoPagoDTO> metodos) {
        if (metodos == null || metodos.isEmpty()) return false;
        return metodos.stream().anyMatch(mp -> {
            MetodosPago m = metodosPagoRepository.findById(mp.getMetodoPagoId().intValue()).orElse(null);
            return m != null && m.getTipoNegociacion() != null
                && "Credito".equalsIgnoreCase(m.getTipoNegociacion().name());
        });
    }

    /**
     * Asigna el comprobante (FC o VC) a la venta usando el prefijo del cajero.
     * Si la venta ya tiene un comprobante NO temporal asignado, no hace nada
     * (el consecutivo solo se reserva una vez).
     */
    private void asignarComprobanteAVenta(Venta venta, boolean tieneCredito) {
        if (venta.getComprobante() != null && venta.getNumeroVenta() != null
            && !venta.getNumeroVenta().startsWith("TMP-")) {
            return; // ya tiene comprobante real
        }
        if (venta.getCajero() == null) {
            throw new VentaException("Se requiere un cajero para generar el comprobante de la venta.");
        }
        TipoMovimientoComprobante tipo = tieneCredito
                ? TipoMovimientoComprobante.VC
                : TipoMovimientoComprobante.FC;
        try {
            AsignacionComprobanteService.Resultado r =
                    asignacionComprobante.asignar(venta.getCajero().getCajeroId(), tipo);
            venta.setComprobante(r.getComprobante());
            venta.setConsecutivoComprobante(r.getConsecutivo());
            venta.setNumeroVenta(r.getNumeroDocumento());
        } catch (AsignacionComprobanteService.ComprobanteNoConfiguradoException ex) {
            throw new VentaException(ex.getMessage());
        }
    }

    @Override
    @Transactional
    public VentaDTO crearVenta(VentaDTO ventaDTO) {

        // Validar que el periodo contable de la fecha de emisión esté abierto.
        // Bloquea aquí antes de cualquier escritura para evitar ventas huérfanas
        // sin asiento contable.
        if (ventaDTO.getFechaEmision() != null) {
            periodoContableService.validarPeriodoAbierto(ventaDTO.getFechaEmision());
        }

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
        venta.setTotalVenta(subtotal); // BRUTO (gravada + IVA). El neto a pagar = bruto − retenciones.

        // Normalizar retenciones sufridas (el cliente nos retiene). El mapper puede
        // dejarlas en null si el DTO no las trae → forzamos ZERO para no violar NOT NULL.
        venta.setRetefuente(venta.getRetefuente() != null ? venta.getRetefuente() : BigDecimal.ZERO);
        venta.setReteiva(venta.getReteiva() != null ? venta.getReteiva() : BigDecimal.ZERO);
        venta.setReteica(venta.getReteica() != null ? venta.getReteica() : BigDecimal.ZERO);

        // ─── Asignación de comprobante: solo si ya sabemos los métodos de pago.
        //     Si la venta se crea sin métodos (modo "abrir → agregar items → cobrar"),
        //     guardamos un número TMP temporal y la asignación real se hace en
        //     completarVenta() cuando ya se conoce si es contado o crédito.
        if (venta.getCajero() == null) {
            throw new VentaException("Se requiere un cajero para generar el comprobante de la venta.");
        }
        if (ventaDTO.getMetodosPago() != null && !ventaDTO.getMetodosPago().isEmpty()) {
            asignarComprobanteAVenta(venta, tieneCredito(ventaDTO));
        } else {
            // Placeholder único — se reemplaza con el número definitivo al completar
            venta.setNumeroVenta("TMP-" + System.currentTimeMillis());
        }

        Venta ven=ventaRepository.save(venta);
        final VentaDTO ventacreada= ventaMapper.toDto(ven);

        // ✅ Si la venta trae métodos de pago → completarla automáticamente
        //    (esto registra el movimiento en cajero y genera CxC si hay crédito)
        if (ventaDTO.getMetodosPago() != null && !ventaDTO.getMetodosPago().isEmpty()) {
            completarVenta(ven.getId(), ventaDTO.getMetodosPago());
        }

        // Actualizar ultimaFechaVenta del producto variante
        for (DetalleVentaDTO detalleDTO : ventaDTO.getItems()) {
            ProductoVariante variante = productoVarianteRepository.findBySku(detalleDTO.getCodigoProducto()).get();
            variante.setUltimaFechaVenta(LocalDateTime.now());
            productoVarianteRepository.save(variante);
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

        // Las retenciones que el cliente nos practica (sufridas) reducen lo que él
        // paga: el cliente paga el NETO (= bruto − retenciones) y la diferencia la
        // declara como retención. Por eso el pago neto + retenciones debe cubrir el
        // total BRUTO de la venta.
        BigDecimal reteF = venta.getRetefuente() != null ? venta.getRetefuente() : BigDecimal.ZERO;
        BigDecimal reteI = venta.getReteiva() != null ? venta.getReteiva() : BigDecimal.ZERO;
        BigDecimal reteC = venta.getReteica() != null ? venta.getReteica() : BigDecimal.ZERO;
        BigDecimal totalRetenciones = reteF.add(reteI).add(reteC);
        BigDecimal totalCubierto = totalPagado.add(totalRetenciones);

        if (totalCubierto.compareTo(venta.getTotalVenta()) < 0) {
            throw new VentaException("El total pagado (" + totalPagado + ") más retenciones ("
                    + totalRetenciones + ") no cubre el total de la venta (" + venta.getTotalVenta() + ")");
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

        // ─── Asignar el comprobante DEFINITIVO ahora que ya sabemos si es FC o VC ───
        // Si la venta tenía un número TMP (creada sin métodos), aquí se le asigna su prefijo
        // y consecutivo real. Si ya tenía un comprobante real, no se cambia.
        asignarComprobanteAVenta(venta, tieneCredito);

        // Blindaje fiscal: NO completar la venta si faltan las cuentas contables indispensables
        // (IVA generado, anticipos de retención sufrida, ingresos). Antes estas validaciones se
        // lanzaban DENTRO de generarAsientoVenta, cuyo try/catch las convertía en "asiento
        // fallido": la venta quedaba COMPLETADA y facturada pero sin asiento ni IVA registrado.
        // Validar aquí —antes de COMPLETAR y de emitir el evento de facturación— hace que la
        // operación se revierta de forma limpia con un mensaje claro para configurar el PUC.
        validarCuentasFiscalesVenta(venta);

        venta.setEstado("COMPLETADA");
        Venta ventaGuardada = ventaRepository.save(venta);

        // ✅ Descargue de inventario SALIDA + Kardex — PRIMERO y dentro de la transacción: si falla,
        // toda la venta se revierte (no queda COMPLETADA sin descontar stock). Debe ir antes del
        // asiento (REQUIRES_NEW) para no dejar asientos huérfanos si el inventario falla.
        generarMovimientoInventarioVenta(ventaGuardada);

        // Actualizar último movimiento del cliente
        try {
            if (ventaGuardada.getCliente() != null) {
                tercerosRepository.actualizarUltimoMovimiento(
                        ventaGuardada.getCliente().getTerceroId(), java.time.LocalDateTime.now());
            }
        } catch (Exception ex) {
            System.out.println("[UltimoMovimiento] Error actualizando cliente: " + ex.getMessage());
        }

        // ✅ Registrar movimiento en cajero y generar CxC si hay crédito
        DatosSesiones sesionComp = obtenerSesionActiva();
        if (sesionComp == null || sesionComp.getDetalleCajeroId() == null) {
            throw new VentaException("No se puede completar la venta: no hay sesión de cajero activa. Inicie sesión nuevamente.");
        }

        BigDecimal montoEfectivo = BigDecimal.ZERO;
        BigDecimal montoElectronico = BigDecimal.ZERO;
        BigDecimal montoCredito = BigDecimal.ZERO;
        int plazoDiasCredito = 30;
        // El vencimiento de la CxC toma por defecto el plazo comercial del CLIENTE (tercero).
        // Si el método de pago trae un plazo explícito (>0), ese tiene prioridad (ver abajo).
        Terceros clienteCxc = ventaGuardada.getCliente();
        if (clienteCxc != null && clienteCxc.getPlazo() != null && clienteCxc.getPlazo() > 0) {
            plazoDiasCredito = clienteCxc.getPlazo();
        }

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

        // Cuadre de caja de la VENTA:
        //  - montoTotal = recaudo de CONTADO real (efectivo + electrónico), NO el bruto de la
        //    venta. El bruto incluía la parte a crédito (que se registra aparte como
        //    CUENTA_POR_COBRAR) y las retenciones sufridas, inflando el totalRecaudo de la sesión.
        //  - montoCosto = COGS real (Σ cantidad × costo promedio), NO la base gravada; antes se
        //    pasaba la gravada, distorsionando por completo el margen/utilidad del arqueo.
        BigDecimal recaudoContado = montoEfectivo.add(montoElectronico);
        BigDecimal cogsVenta = calcularCostoTotalVenta(ventaGuardada);
        detalleCajeroService.registrarMovimiento(
                sesionComp.getDetalleCajeroId(),
                MovimientoCajero.TipoMovimiento.VENTA,
                ventaGuardada.getNumeroVenta(),
                ventaGuardada.getId(),
                recaudoContado,
                cogsVenta,
                montoEfectivo,
                montoElectronico,
                "Venta " + ventaGuardada.getNumeroVenta(),
                ventaGuardada.getComprobante()
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
                    "CxC Venta " + ventaGuardada.getNumeroVenta(),
                    ventaGuardada.getComprobante()
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

        // ✅ Generar asiento contable (partida doble) — soporta venta contado, crédito y mixta.
        // El inventario ya se descargó arriba (antes del asiento) de forma atómica.
        generarAsientoVenta(ventaGuardada);
    }

    /**
     * Registra un movimiento de inventario SALIDA + Kardex desde la venta.
     * Envuelto en try/catch para que si el inventario falla, la venta no se rompa
     * (el cuadre de existencias ya se actualiza directo desde el flujo principal).
     */
    private void generarMovimientoInventarioVenta(Venta venta) {
        try {
            java.util.List<MovimientoInventarioAutoService.ItemMovimiento> items =
                    MovimientoInventarioAutoService.nuevaLista();
            for (DetalleVenta d : venta.getItems()) {
                if (d.getCantidad() == null || d.getCantidad() <= 0) continue;
                // Costo unitario para Kardex: obtener costo del producto si no hay costo promedio anterior
                double cant = d.getCantidad().doubleValue();
                double precio = d.getPrecioUnitario() != null ? d.getPrecioUnitario().doubleValue() : 0.0;
                // OJO: DetalleVenta.iva es un MONTO (valor del IVA de la línea), NO un porcentaje.
                double ivaMonto = d.getIva() != null ? d.getIva().doubleValue() : 0.0;
                // El total de la línea con IVA = total del documento si está disponible; si no,
                // se calcula como base + IVA (monto). Antes el fallback trataba el IVA como
                // porcentaje (precio*cant*(1+iva/100)), lo que daba un total absurdo.
                double totalLineaConIva = d.getTotal() != null
                        ? d.getTotal().doubleValue()
                        : (precio * cant + ivaMonto);
                
                // Obtener costo del producto desde ProductoVariante -> Producto
                // Usar codigo_producto (SKU) en lugar de referencia_variantes
                double costoUnitario = 0.0;
                try {
                    System.out.println("[CostoProducto] Buscando variante con SKU: " + d.getCodigoProducto());
                    java.util.Optional<com.pazzioliweb.productosmodule.entity.ProductoVariante> varianteOpt =
                            productoVarianteRepository.findBySku(d.getCodigoProducto());
                    if (varianteOpt.isPresent()) {
                        System.out.println("[CostoProducto] Variante encontrada: " + varianteOpt.get().getProductoVarianteId());
                        if (varianteOpt.get().getProducto() != null) {
                            Double costoProducto = varianteOpt.get().getProducto().getCosto();
                            System.out.println("[CostoProducto] Costo del producto: " + costoProducto);
                            if (costoProducto != null && costoProducto > 0) {
                                costoUnitario = costoProducto;
                            } else {
                                System.out.println("[CostoProducto] Costo del producto es null o 0");
                            }
                        } else {
                            System.out.println("[CostoProducto] Producto es null");
                        }
                    } else {
                        System.out.println("[CostoProducto] Variante no encontrada con SKU: " + d.getCodigoProducto());
                    }
                } catch (Exception ex) {
                    System.out.println("[CostoProducto] Error obteniendo costo del producto: " + ex.getMessage());
                    ex.printStackTrace();
                }
                
                // Persistir el costo real de la venta en la línea (para reversar el COGS exacto en
                // una devolución posterior, aunque el costo del producto cambie luego).
                if (costoUnitario > 0) d.setCostoUnitario(java.math.BigDecimal.valueOf(costoUnitario));
                items.add(new MovimientoInventarioAutoService.ItemMovimiento(
                        d.getCodigoProducto(),
                        d.getReferenciaVariantes(),
                        cant,
                        costoUnitario,        // costo unitario del producto — kardex usa promedio existente si está disponible
                        totalLineaConIva       // total mostrado en historial
                ));
            }
            if (items.isEmpty()) return;
            // Tipo real del comprobante (FC contado / VC crédito) — si no hay comprobante, default FC.
            String tipoComprobante = "FC";
            Integer comprobanteId = null;
            Integer consecutivo = null;
            if (venta.getComprobante() != null) {
                if (venta.getComprobante().getTipoMovimiento() != null) {
                    tipoComprobante = venta.getComprobante().getTipoMovimiento().name();
                }
                comprobanteId = venta.getComprobante().getId() != null ? venta.getComprobante().getId().intValue() : null;
                consecutivo = venta.getConsecutivoComprobante();
            }
            movimientoInventarioAutoService.registrarSalidaPorVenta(
                    venta.getNumeroVenta(),
                    venta.getId(),
                    venta.getBodega() != null ? venta.getBodega().getCodigo() : null,
                    venta.getFechaEmision() != null ? venta.getFechaEmision() : LocalDate.now(),
                    items,
                    tipoComprobante,
                    comprobanteId,
                    consecutivo
            );
        } catch (VentaException ve) {
            throw ve;
        } catch (Exception ex) {
            // El descargue de inventario es CRÍTICO: si falla, la venta NO debe quedar COMPLETADA
            // sin descontar stock. Se propaga para revertir toda la operación (por eso este
            // movimiento corre ANTES del asiento/caja, dentro de la misma transacción).
            throw new VentaException("No se pudo registrar el movimiento de inventario de la venta "
                    + venta.getNumeroVenta() + ": " + ex.getMessage());
        }
    }

    /**
     * Asiento de venta:
     *   DÉBITO  Banco/Caja por cada método de pago contado
     *   DÉBITO  CxC (1305)             por la parte a crédito
     *   CRÉDITO Ingresos por ventas (4135) por subtotal sin IVA
     *   CRÉDITO IVA generado (240801)      por el IVA total
     */
    /**
     * Valida que existan en el PUC las cuentas contables indispensables para asentar la venta.
     * Se invoca ANTES de marcar la venta como COMPLETADA para que, si falta alguna, la operación
     * se revierta completa (no se factura ni descarga inventario) en lugar de quedar con un
     * "asiento fallido" silencioso. Solo exige la cuenta cuando el concepto respectivo aplica.
     */
    private void validarCuentasFiscalesVenta(Venta venta) {
        BigDecimal iva = venta.getIva() != null ? venta.getIva() : BigDecimal.ZERO;
        if (iva.compareTo(BigDecimal.ZERO) > 0 && configContable.ivaGenerado().isEmpty()) {
            throw new VentaException("No se puede completar la venta: tiene IVA $" + iva +
                    " pero la cuenta '240801 IVA generado' no está configurada en el PUC. " +
                    "Configúrela antes de facturar (no se puede acumular IVA en Ingresos).");
        }
        BigDecimal rf = venta.getRetefuente() != null ? venta.getRetefuente() : BigDecimal.ZERO;
        BigDecimal ri = venta.getReteiva()    != null ? venta.getReteiva()    : BigDecimal.ZERO;
        BigDecimal rc = venta.getReteica()    != null ? venta.getReteica()    : BigDecimal.ZERO;
        if (rf.compareTo(BigDecimal.ZERO) > 0 && configContable.anticipoRetefuente().isEmpty()) {
            throw new VentaException("No se puede completar la venta: tiene Retefuente sufrida pero " +
                    "la cuenta '135515' (anticipo retefuente) no está en el PUC.");
        }
        if (ri.compareTo(BigDecimal.ZERO) > 0 && configContable.anticipoReteiva().isEmpty()) {
            throw new VentaException("No se puede completar la venta: tiene ReteIVA sufrida pero " +
                    "la cuenta '135517' (anticipo reteIVA) no está en el PUC.");
        }
        if (rc.compareTo(BigDecimal.ZERO) > 0 && configContable.anticipoReteica().isEmpty()) {
            throw new VentaException("No se puede completar la venta: tiene ReteICA sufrida pero " +
                    "la cuenta '135518' (anticipo reteICA) no está en el PUC.");
        }
        if (configContable.ingresosVentas().isEmpty()) {
            throw new VentaException("No se puede completar la venta: la cuenta '4135 Ingresos por " +
                    "ventas' no está configurada en el PUC.");
        }
        // Cuentas de COSTO DE VENTAS (6135) e INVENTARIO (1435): si faltan, el asiento de ingreso
        // (REQUIRES_NEW) alcanzaba a confirmarse y el de COGS fallaba después → venta facturada con
        // ingreso pero SIN costo (utilidad inflada). Validarlas aquí hace fallar ANTES de contabilizar,
        // dejando la operación atómica (rollback total).
        if (configContable.costoVentas().isEmpty()) {
            throw new VentaException("No se puede completar la venta: la cuenta '6135 Costo de ventas' " +
                    "no está configurada en el PUC.");
        }
        if (configContable.inventarios().isEmpty()) {
            throw new VentaException("No se puede completar la venta: la cuenta '1435 Inventarios' " +
                    "no está configurada en el PUC.");
        }
        // Si la venta tiene parte a CRÉDITO, la cuenta 1305 (CxC clientes) es indispensable; sin
        // ella el asiento se omitía en silencio (venta COMPLETADA y facturada, pero sin asiento).
        boolean tieneCredito = venta.getMetodosPago() != null && venta.getMetodosPago().stream()
                .anyMatch(vmp -> vmp.getMetodoPago() != null
                        && vmp.getMetodoPago().getTipoNegociacion() == MetodosPago.TipoNegociacion.Credito);
        if (tieneCredito && configContable.cxcClientes().isEmpty()) {
            throw new VentaException("No se puede completar la venta a crédito: la cuenta '1305 " +
                    "Clientes' (CxC) no está configurada en el PUC.");
        }
    }

    private void generarAsientoVenta(Venta venta) {
        try {
            java.util.List<AsientoContableService.LineaDTO> lineas = new java.util.ArrayList<>();
            String nombreCliente = venta.getCliente() != null
                    ? (venta.getCliente().getRazonSocial() != null && !venta.getCliente().getRazonSocial().isBlank()
                        ? venta.getCliente().getRazonSocial()
                        : (venta.getCliente().getNombre1() != null ? venta.getCliente().getNombre1() : ""))
                    : "";

            // Blindaje: el cliente paga el NETO (= bruto − retenciones sufridas). Los métodos de
            // pago deben sumar ese neto (el POS ya los envía netos). Si por algún flujo vinieran
            // en BRUTO con retención > 0, se escalan proporcionalmente para que el asiento SIEMPRE
            // cuadre (retención = anticipo). Con retención = 0 el factor es 1 (no cambia nada).
            BigDecimal totalVentaBruto = venta.getTotalVenta() != null ? venta.getTotalVenta() : BigDecimal.ZERO;
            BigDecimal retTotalVenta = (venta.getRetefuente() != null ? venta.getRetefuente() : BigDecimal.ZERO)
                    .add(venta.getReteiva() != null ? venta.getReteiva() : BigDecimal.ZERO)
                    .add(venta.getReteica() != null ? venta.getReteica() : BigDecimal.ZERO);
            BigDecimal netoPagos = totalVentaBruto.subtract(retTotalVenta).max(BigDecimal.ZERO);
            BigDecimal sumaMetodos = venta.getMetodosPago().stream()
                    .map(m -> m.getMonto() != null ? m.getMonto() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // Se escala cuando la suma de métodos NO iguala el neto (por retención en bruto O por
            // SOBREPAGO): así el débito de los pagos = neto = créditos y el asiento cuadra aunque el
            // cajero haya digitado de más (el excedente es "cambio", no ingreso). Con suma = neto, factor = 1.
            final BigDecimal factorPago = (sumaMetodos.signum() > 0
                    && sumaMetodos.subtract(netoPagos).abs().compareTo(new BigDecimal("0.02")) > 0)
                    ? netoPagos.divide(sumaMetodos, 6, java.math.RoundingMode.HALF_UP)
                    : BigDecimal.ONE;

            // Débitos por métodos de pago
            for (VentaMetodoPago vmp : venta.getMetodosPago()) {
                MetodosPago metodo = vmp.getMetodoPago();
                BigDecimal montoLinea = (vmp.getMonto() != null ? vmp.getMonto() : BigDecimal.ZERO)
                        .multiply(factorPago).setScale(2, java.math.RoundingMode.HALF_UP);
                if (metodo.getTipoNegociacion() == MetodosPago.TipoNegociacion.Credito) {
                    // Crédito → débito a CxC
                    CuentaContable cxc = configContable.cxcClientes().orElse(null);
                    if (cxc == null) {
                        System.out.println("[AsientoVenta] Cuenta 1305 Clientes no configurada. Asiento omitido.");
                        return;
                    }
                    AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                            .debito(cxc.getId(), montoLinea, "CxC venta " + venta.getNumeroVenta());
                    if (venta.getCliente() != null) {
                        l.conTercero(venta.getCliente().getTerceroId(), nombreCliente);
                    }
                    lineas.add(l);
                } else {
                    // Contado → débito a la cuenta del método
                    CuentaContable cta = resolverCuentaMetodoPago(metodo);
                    if (cta == null) {
                        System.out.println("[AsientoVenta] Método '" + metodo.getDescripcion() + "' sin cuenta. Asiento omitido.");
                        return;
                    }
                    lineas.add(AsientoContableService.LineaDTO.debito(cta.getId(), montoLinea,
                            "Cobro contado por " + metodo.getDescripcion()));
                }
            }

            // ── Débitos por RETENCIONES SUFRIDAS (el cliente nos retuvo) ──
            // Son anticipos de impuestos (activo, grupo 1355). El cliente paga el
            // NETO y aquí cuadramos: débitos (caja/CxC neta + retenciones) = créditos
            // (ingresos + IVA generado), todo sobre el BRUTO de la venta.
            BigDecimal vReteF = venta.getRetefuente() != null ? venta.getRetefuente() : BigDecimal.ZERO;
            BigDecimal vReteI = venta.getReteiva()    != null ? venta.getReteiva()    : BigDecimal.ZERO;
            BigDecimal vReteC = venta.getReteica()    != null ? venta.getReteica()    : BigDecimal.ZERO;
            if (vReteF.compareTo(BigDecimal.ZERO) > 0) {
                CuentaContable cta = configContable.anticipoRetefuente().orElseThrow(() -> new IllegalStateException(
                        "La venta " + venta.getNumeroVenta() + " tiene Retefuente sufrida pero la cuenta '135515' no está en el PUC."));
                AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                        .debito(cta.getId(), vReteF, "Retefuente que nos practicaron venta " + venta.getNumeroVenta());
                if (venta.getCliente() != null) l.conTercero(venta.getCliente().getTerceroId(), nombreCliente);
                lineas.add(l);
            }
            if (vReteI.compareTo(BigDecimal.ZERO) > 0) {
                CuentaContable cta = configContable.anticipoReteiva().orElseThrow(() -> new IllegalStateException(
                        "La venta " + venta.getNumeroVenta() + " tiene ReteIVA sufrida pero la cuenta '135517' no está en el PUC."));
                AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                        .debito(cta.getId(), vReteI, "ReteIVA que nos practicaron venta " + venta.getNumeroVenta());
                if (venta.getCliente() != null) l.conTercero(venta.getCliente().getTerceroId(), nombreCliente);
                lineas.add(l);
            }
            if (vReteC.compareTo(BigDecimal.ZERO) > 0) {
                CuentaContable cta = configContable.anticipoReteica().orElseThrow(() -> new IllegalStateException(
                        "La venta " + venta.getNumeroVenta() + " tiene ReteICA sufrida pero la cuenta '135518' no está en el PUC."));
                AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                        .debito(cta.getId(), vReteC, "ReteICA que nos practicaron venta " + venta.getNumeroVenta());
                if (venta.getCliente() != null) l.conTercero(venta.getCliente().getTerceroId(), nombreCliente);
                lineas.add(l);
            }

            // Créditos: Ingresos y IVA
            // Cálculo robusto: tomamos como referencia el TOTAL VENTA (que ya
            // contiene los débitos de los métodos de pago) y le restamos el IVA.
            // Así garantizamos que débitos = créditos exactamente, sin importar
            // cómo esté definido el campo "gravada" en distintos flujos.
            BigDecimal totalIva = venta.getIva() != null ? venta.getIva() : BigDecimal.ZERO;
            BigDecimal totalVenta = venta.getTotalVenta() != null ? venta.getTotalVenta() : BigDecimal.ZERO;
            BigDecimal subtotalSinIva = totalVenta.subtract(totalIva).max(BigDecimal.ZERO);

            CuentaContable ingresos = configContable.ingresosVentas().orElse(null);
            if (ingresos == null) {
                System.out.println("[AsientoVenta] Cuenta 4135 Ingresos no configurada. Asiento omitido.");
                return;
            }

            // Crédito a Ingresos por el subtotal sin IVA
            lineas.add(AsientoContableService.LineaDTO.credito(ingresos.getId(),
                    subtotalSinIva,
                    "Ingreso venta " + venta.getNumeroVenta()));

            // Crédito al IVA generado. Si no hay cuenta IVA configurada, FALLAR
            // explícitamente: no se debe absorber el IVA en 4135 porque viola NIIF
            // (los ingresos van netos de IVA) y distorsiona el Estado de Resultados.
            if (totalIva.compareTo(BigDecimal.ZERO) > 0) {
                CuentaContable ivaGen = configContable.ivaGenerado().orElse(null);
                if (ivaGen == null) {
                    throw new IllegalStateException(
                        "La venta " + venta.getNumeroVenta() + " tiene IVA $" + totalIva +
                        " pero la cuenta '240801 IVA generado' NO está configurada en el PUC. " +
                        "Configúrela antes de continuar — no se puede acumular IVA en Ingresos."
                    );
                }
                lineas.add(AsientoContableService.LineaDTO.credito(ivaGen.getId(), totalIva,
                        "IVA generado venta " + venta.getNumeroVenta()));
            }

            // Tipo del documento: FC o VC según el comprobante
            String tipo = venta.getComprobante() != null
                    ? venta.getComprobante().getTipoMovimiento().name()
                    : "FC";

            asientoService.generarAsiento(
                    venta.getNumeroVenta() != null ? venta.getNumeroVenta() : ("V-" + venta.getId()),
                    venta.getFechaEmision() != null ? venta.getFechaEmision() : LocalDate.now(),
                    "Venta " + venta.getNumeroVenta() + " - " + nombreCliente,
                    tipo,
                    venta.getId(),
                    venta.getComprobante(),
                    lineas
            );

            // ─── Asiento del COSTO DE VENTAS (independiente del ingreso) ────────
            // DR 6135 (Costo de Ventas) / CR 1435 (Mercancías) por el costo total
            // de la mercancía vendida. Indispensable para que Estado de Resultados
            // muestre utilidad bruta correcta (ventas − costo).
            generarAsientoCostoVenta(venta, nombreCliente);
        } catch (Exception ex) {
            System.out.println("[AsientoVenta] Error generando asiento (no crítico): " + ex.getMessage());
            asientoFallidoService.registrar("VENTAS",
                    venta.getComprobante() != null && venta.getComprobante().getTipoMovimiento() != null
                        ? venta.getComprobante().getTipoMovimiento().name() : "FC",
                    venta.getId(), venta.getNumeroVenta(),
                    "Error generando asiento de venta: " + ex.getMessage(), ex);
        }
    }

    /**
     * Genera el asiento de Costo de Ventas: DR 6135 / CR 1435 por la suma de
     * (cantidad × costo) de cada detalle de la venta. Usa producto.costo como
     * referencia (el costo promedio actual del producto). Try/catch defensivo.
     */
    /**
     * Costo total de la mercancía vendida: Σ (cantidad × costo promedio) por detalle.
     * Reutilizado por el asiento de costo (6135/1435) y por el cuadre de caja (para que el
     * "totalCosto" de la sesión refleje el COGS real y no la base gravada de la venta).
     */
    private java.math.BigDecimal calcularCostoTotalVenta(Venta venta) {
        java.math.BigDecimal totalCosto = java.math.BigDecimal.ZERO;
        for (DetalleVenta d : venta.getItems()) {
            if (d.getCantidad() == null || d.getCantidad() <= 0) continue;
            java.math.BigDecimal costoUnit = obtenerCostoVarianteParaVenta(
                    d.getCodigoProducto(), d.getReferenciaVariantes(), venta.getBodega());
            if (costoUnit == null) continue;
            totalCosto = totalCosto.add(costoUnit.multiply(java.math.BigDecimal.valueOf(d.getCantidad())));
        }
        return totalCosto;
    }

    private void generarAsientoCostoVenta(Venta venta, String nombreCliente) {
        try {
            // Costo total = sum(cantidad × costo promedio) por detalle (ver calcularCostoTotalVenta).
            java.math.BigDecimal totalCosto = calcularCostoTotalVenta(venta);
            if (totalCosto.compareTo(java.math.BigDecimal.ZERO) <= 0) return; // nada que costear

            CuentaContable costoCta = configContable.costoVentas().orElse(null);
            CuentaContable invCta   = configContable.inventarios().orElse(null);
            if (costoCta == null || invCta == null) {
                System.out.println("[AsientoCosto] 6135 o 1435 no configuradas, se omite.");
                return;
            }

            java.util.List<AsientoContableService.LineaDTO> lineas = new java.util.ArrayList<>();
            lineas.add(AsientoContableService.LineaDTO.debito(costoCta.getId(), totalCosto,
                    "Costo de ventas " + venta.getNumeroVenta()));
            lineas.add(AsientoContableService.LineaDTO.credito(invCta.getId(), totalCosto,
                    "Salida de inventario por venta " + venta.getNumeroVenta()));

            String tipo = venta.getComprobante() != null
                    ? venta.getComprobante().getTipoMovimiento().name()
                    : "FC";

            // Diferenciamos el documentoOrigenTipo con sufijo "-COSTO" para que la
            // idempotencia de AsientoContableService no confunda este asiento con el
            // asiento principal de la venta (que usa el mismo ventaId).
            asientoService.generarAsiento(
                    "COSTO-" + (venta.getNumeroVenta() != null ? venta.getNumeroVenta() : ("V-" + venta.getId())),
                    venta.getFechaEmision() != null ? venta.getFechaEmision() : LocalDate.now(),
                    "Costo de venta " + venta.getNumeroVenta() + " - " + nombreCliente,
                    tipo + "-COSTO",
                    venta.getId(),
                    venta.getComprobante(),
                    lineas
            );
        } catch (Exception ex) {
            System.out.println("[AsientoCosto] Error generando asiento (no crítico): " + ex.getMessage());
            asientoFallidoService.registrar("VENTAS-COSTO",
                    venta.getComprobante() != null && venta.getComprobante().getTipoMovimiento() != null
                        ? venta.getComprobante().getTipoMovimiento().name() : "FC",
                    venta.getId(), venta.getNumeroVenta(),
                    "Error generando asiento de COGS: " + ex.getMessage(), ex);
        }
    }

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.pazzioliweb.movimientosinventariomodule.repository.KardexRepository kardexRepository;

    /**
     * Resuelve el costo unitario para el asiento de COGS.
     * Prioridad (NIIF Sec.13 / NIC 2):
     *   1. Costo promedio ponderado del Kardex de la variante+bodega.
     *   2. Costo histórico del producto (fallback).
     */
    private java.math.BigDecimal obtenerCostoVarianteParaVenta(String codigoProducto, String referenciaVariantes,
                                                                com.pazzioliweb.productosmodule.entity.Bodegas bodega) {
        try {
            java.util.Optional<com.pazzioliweb.productosmodule.entity.ProductoVariante> opt =
                    productoVarianteRepository.findBySku(codigoProducto);
            if (opt.isEmpty() && referenciaVariantes != null) {
                opt = productoVarianteRepository.findBySku(referenciaVariantes);
            }
            if (opt.isEmpty()) return null;
            com.pazzioliweb.productosmodule.entity.ProductoVariante variante = opt.get();

            // 1) Intentar costo promedio del Kardex (más fiel a NIIF Sec.13).
            if (kardexRepository != null && bodega != null) {
                java.util.Optional<com.pazzioliweb.movimientosinventariomodule.entity.Kardex> kx =
                        kardexRepository.findTopByProductoVarianteAndBodegaOrderByFechaCreacionDesc(variante, bodega);
                if (kx.isPresent() && kx.get().getCostoPromedio() != null && kx.get().getCostoPromedio() > 0) {
                    return java.math.BigDecimal.valueOf(kx.get().getCostoPromedio());
                }
            }

            // 2) Fallback al costo histórico del producto.
            if (variante.getProducto() != null) {
                Double c = variante.getProducto().getCosto();
                if (c != null && c > 0) return java.math.BigDecimal.valueOf(c);
            }
        } catch (Exception ex) {
            System.out.println("[AsientoCosto] No se pudo resolver costo de " + codigoProducto + ": " + ex.getMessage());
        }
        return null;
    }

    /** Overload retro-compatible. */
    private java.math.BigDecimal obtenerCostoVarianteParaVenta(String codigoProducto, String referenciaVariantes) {
        return obtenerCostoVarianteParaVenta(codigoProducto, referenciaVariantes, null);
    }

    private CuentaContable resolverCuentaMetodoPago(MetodosPago metodo) {
        if (metodo == null) return null;
        // Si tiene cuenta bancaria asociada, su cuenta contable es la verdad
        // (el dinero entra/sale del banco). Esto permite que el usuario cambie
        // la cuenta del banco sin tener que actualizar también el método de pago.
        if (metodo.getCuentaBancaria() != null && metodo.getCuentaBancaria().getCuentaContable() != null) {
            return metodo.getCuentaBancaria().getCuentaContable();
        }
        // Si no hay cuenta bancaria (típicamente Efectivo), usar la cuenta contable directa
        if (metodo.getCuentaContable() != null) return metodo.getCuentaContable();
        return configContable.cajaGeneral().orElse(null);
    }

    /**
     * @deprecated Método legacy que solo restaura inventario SIN generar asiento contable,
     *             reversar COGS ni emitir Nota Crédito DIAN. Usar {@code DevolucionService.registrarDevolucion}
     *             que sí maneja partida doble, Kardex y NC electrónica.
     *             Este método queda bloqueado para impedir corrupción contable.
     */
    @Override
    @Transactional
    @Deprecated
    public void devolverVenta(Long ventaId, List<DetalleVentaDTO> detallesDevueltos) {
        throw new UnsupportedOperationException(
            "VentaService.devolverVenta() está deprecado y bloqueado porque no genera asiento contable " +
            "ni Nota Crédito DIAN. Use DevolucionService.registrarDevolucion() — endpoint POST /api/ventas/devolucion."
        );
        // Código legacy preservado debajo como referencia (inalcanzable).
        /*
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
                        "Devolución venta " + venta.getNumeroVenta(),
                        venta.getComprobante()
                );
            } catch (Exception e) {
                System.out.println("Error al registrar devolución en cajero: " + e.getMessage());
            }
        }
        */
    }

    @Override
    @Transactional
    public void anularVenta(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new VentaException("Venta no encontrada"));

        if ("ANULADA".equals(venta.getEstado())) {
            throw new VentaException("La venta ya está anulada");
        }

        // No permitir anular una venta a CRÉDITO que ya recibió ABONOS: el asiento y la CxC se
        // reversarían, pero el efectivo del abono (recaudado vía Recibo de Caja) quedaría en caja sin
        // respaldo → descuadre. El usuario debe anular primero los recibos de caja del abono.
        if (venta.getNumeroVenta() != null) {
            boolean tieneAbonos = cuentaPorCobrarRepository.findByNumeroVenta(venta.getNumeroVenta()).stream()
                    .anyMatch(cxc -> !"ANULADA".equals(cxc.getEstado())
                            && cxc.getValorNeto() != null && cxc.getSaldo() != null
                            && cxc.getSaldo().compareTo(cxc.getValorNeto()) < 0);
            if (tieneAbonos) {
                throw new VentaException("No se puede anular: la venta a crédito ya tiene abonos recibidos. "
                        + "Anule primero los recibos de caja asociados y vuelva a intentarlo.");
            }
        }

        // Estado previo determina si hubo asiento/Kardex/CxC generados (COMPLETADA sí, PENDIENTE no).
        boolean teniaAsientos = "COMPLETADA".equals(venta.getEstado())
                || "DEVOLUCION_PARCIAL".equals(venta.getEstado());

        venta.setEstado("ANULADA");
        ventaRepository.save(venta);

        // ── Si tenía asiento contable, anularlo (ingreso + COGS) ──
        if (teniaAsientos) {
            try {
                String tipoDoc = venta.getComprobante() != null
                        ? venta.getComprobante().getTipoMovimiento().name() : "FC";
                asientoService.anularAsientoDeDocumento(tipoDoc, venta.getId());
                asientoService.anularAsientoDeDocumento(tipoDoc + "-COSTO", venta.getId());
            } catch (Exception ex) {
                System.out.println("[AnularVenta] Error anulando asiento: " + ex.getMessage());
            }

            // ── Restaurar existencias de cada item al saldo previo a la venta ──
            // DetalleVenta no referencia ProductoVariante directamente: buscamos por SKU/codigoProducto
            // y la bodega es a nivel de Venta.
            try {
                Integer bodegaCodigo = venta.getBodega() != null ? venta.getBodega().getCodigo() : null;
                if (bodegaCodigo != null) {
                    // La venta SACÓ stock (SALIDA); anularla lo REINGRESA con un movimiento REAL de
                    // kardex (documentoTipo "VENTA-ANUL") al costo de venta, para que existencias,
                    // kardex y el mayor 1435 queden consistentes. Antes se sumaba a existencias a
                    // mano y el kardex quedaba desincronizado.
                    java.util.List<MovimientoInventarioAutoService.ItemMovimiento> items =
                            MovimientoInventarioAutoService.nuevaLista();
                    for (DetalleVenta det : venta.getItems()) {
                        if (det.getCantidad() == null || det.getCantidad() <= 0) continue;
                        // Reingresar al MISMO costo con que salió la venta (persistido en el detalle),
                        // para que el kardex/existencias vuelvan al valor exacto que revierte el asiento
                        // (anularAsientoDeDocumento restaura 1435 al costo original). Antes se recalculaba
                        // con el costo promedio ACTUAL → si el costo cambió entre venta y anulación, el
                        // kardex divergía del mayor 1435.
                        java.math.BigDecimal costoUnit = (det.getCostoUnitario() != null
                                    && det.getCostoUnitario().compareTo(java.math.BigDecimal.ZERO) > 0)
                                ? det.getCostoUnitario()
                                : obtenerCostoVarianteParaVenta(det.getCodigoProducto(), det.getReferenciaVariantes(), venta.getBodega());
                        double costo = costoUnit != null ? costoUnit.doubleValue() : 0.0;
                        items.add(new MovimientoInventarioAutoService.ItemMovimiento(
                                det.getCodigoProducto(), det.getReferenciaVariantes(),
                                det.getCantidad().doubleValue(), costo));
                    }
                    movimientoInventarioAutoService.registrarAjusteAnulacion(
                            "VENTA-ANUL", venta.getId(), bodegaCodigo, LocalDate.now(), items, true);
                }
            } catch (Exception ex) {
                System.out.println("[AnularVenta] Error restaurando inventario: " + ex.getMessage());
            }

            // ── Anular CxC del cliente si era venta a crédito (busca por numeroVenta) ──
            try {
                if (venta.getNumeroVenta() != null) {
                    java.util.List<CuentaPorCobrar> cxcVenta = cuentaPorCobrarRepository
                            .findByNumeroVenta(venta.getNumeroVenta());
                    for (CuentaPorCobrar cxc : cxcVenta) {
                        if (!"ANULADA".equals(cxc.getEstado()) && !"PAGADA".equals(cxc.getEstado())) {
                            cxc.setEstado("ANULADA");
                            cxc.setSaldo(BigDecimal.ZERO);
                            cuentaPorCobrarRepository.save(cxc);
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("[AnularVenta] Error anulando CxC: " + ex.getMessage());
            }

            // ✅ Reversa de CAJA — DENTRO de teniaAsientos (una venta PENDIENTE nunca tocó la caja).
            // Espeja el movimiento VENTA: resta SOLO el recaudo de CONTADO real (efectivo/electrónico
            // que entraron, excluyendo crédito y retenciones) con tipo ANULACION (signo −1) y montos
            // POSITIVOS → neto −recaudoContado. Antes usaba el bruto (totalVenta) como efectivo →
            // descuadraba la caja en ventas a crédito/mixtas/con retención.
            //
            // NOTA (por diseño, no bug): la reversa se registra en la sesión de cajero ABIERTA actual,
            // no en la sesión original de la venta. registrarMovimiento() rechaza sesiones CERRADAS
            // ("La sesión de cajero está cerrada"), y una anulación normalmente ocurre cuando la sesión
            // original ya cerró; físicamente el reembolso sale del cajón abierto ahora. La parte a
            // CRÉDITO no se reversa en caja (el movimiento CUENTA_POR_COBRAR original tiene
            // afectaCaja=false/signo=0: nunca afectó el cuadre); la cartera se reversa arriba sobre la
            // entidad CuentaPorCobrar (fuente autoritativa), no sobre el arqueo.
            DatosSesiones sesionAnul = obtenerSesionActiva();
            if (sesionAnul != null && sesionAnul.getDetalleCajeroId() != null) {
                try {
                    BigDecimal montoEfectivo = BigDecimal.ZERO;
                    BigDecimal montoElectronico = BigDecimal.ZERO;
                    if (venta.getMetodosPago() != null) {
                        for (VentaMetodoPago vmp : venta.getMetodosPago()) {
                            MetodosPago mp = vmp.getMetodoPago();
                            if (mp != null && mp.getTipoNegociacion() == MetodosPago.TipoNegociacion.Credito) continue;
                            String sigla = (mp != null && mp.getSigla() != null) ? mp.getSigla().toUpperCase() : "";
                            if (sigla.startsWith("EF")) montoEfectivo = montoEfectivo.add(vmp.getMonto());
                            else montoElectronico = montoElectronico.add(vmp.getMonto());
                        }
                    }
                    BigDecimal recaudoContado = montoEfectivo.add(montoElectronico);
                    if (recaudoContado.compareTo(BigDecimal.ZERO) > 0) {
                        detalleCajeroService.registrarMovimiento(
                                sesionAnul.getDetalleCajeroId(),
                                MovimientoCajero.TipoMovimiento.ANULACION,
                                venta.getNumeroVenta(),
                                venta.getId(),
                                recaudoContado,
                                calcularCostoTotalVenta(venta),
                                montoEfectivo,
                                montoElectronico,
                                "Anulación venta " + venta.getNumeroVenta(),
                                venta.getComprobante()
                        );
                    }
                } catch (Exception e) {
                    System.out.println("Error al registrar anulación en cajero: " + e.getMessage());
                }
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
        // Si se busca por numeroVenta, aplicar lógica de fallback por estado
        if (numeroventa != null) {
            // 1. Buscar por estado COMPLETADA
            List<Venta> ventasCompletadas = ventaRepository.findByNumeroVentaAndEstado(numeroventa, "COMPLETADA");
            if (!ventasCompletadas.isEmpty()) {
                return ventasCompletadas.stream()
                        .map(ventaMapper::toDto)
                        .collect(Collectors.toList());
            }

            // 2. Buscar por estado DEVUELTA_PARCIAL
            List<Venta> ventasDevolucionParcial = ventaRepository.findByNumeroVentaAndEstado(numeroventa, "DEVOLUCION_PARCIAL");
            if (!ventasDevolucionParcial.isEmpty()) {
                return ventasDevolucionParcial.stream()
                        .map(ventaMapper::toDto)
                        .collect(Collectors.toList());
            }

            // 3. Buscar por estado PENDIENTE
            List<Venta> ventasPendientes = ventaRepository.findByNumeroVentaAndEstado(numeroventa, "PENDIENTE");
            if (!ventasPendientes.isEmpty()) {
                throw new FacturaPendienteException("Factura de venta en estado pendiente");
            }

            // 4. Buscar por estado DEVUELTA
            List<Venta> ventasDevueltas = ventaRepository.findByNumeroVentaAndEstado(numeroventa, "DEVUELTA");
            if (!ventasDevueltas.isEmpty()) {
                throw new FacturaDevueltaException("Factura ya devuelta");
            }

            // Si no se encontró en ningún estado, retornar lista vacía
            return List.of();
        }

        // Si no se busca por numeroVenta, usar la lógica normal de filtros
        return ventaRepository
                .findAll(VentaSpecification.conFiltros(numeroventa,terceroId, vendedorId, cajeroId, fechaInicio, fechaFin), Sort.by(Sort.Direction.DESC, "fechaCreacion"))
                .stream()
                .map(ventaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Long getUltimaVentaId() {
        return ventaRepository.getUltimaVentaId();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public VentaDTO getVentaById(Long ventaId) {
        com.pazzioliweb.ventasmodule.entity.Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada: " + ventaId));
        return ventaMapper.toDto(venta);
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
