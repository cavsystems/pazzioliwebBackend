package com.pazzioliweb.ventasmodule.service.impl;

import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;
import com.pazzioliweb.cajerosmodule.repositori.CajeroRepository;
import com.pazzioliweb.cajerosmodule.service.DetalleCajeroService;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import com.pazzioliweb.vendedoresmodule.entity.Vendedores;
import com.pazzioliweb.vendedoresmodule.repositori.VendedoresRepository;
import com.pazzioliweb.ventasmodule.dtos.CotizacionDTO;
import com.pazzioliweb.ventasmodule.dtos.DetallePedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.DetalleVentaDTO;
import com.pazzioliweb.ventasmodule.dtos.PedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import com.pazzioliweb.ventasmodule.entity.Cotizacion;
import com.pazzioliweb.ventasmodule.entity.DetalleCotizacion;
import com.pazzioliweb.ventasmodule.exception.CotizacionException;
import com.pazzioliweb.ventasmodule.mapper.CotizacionMapper;
import com.pazzioliweb.ventasmodule.repository.CotizacionRepository;
import com.pazzioliweb.ventasmodule.repository.CotizacionSpecification;
import com.pazzioliweb.ventasmodule.repository.PedidoRepository;
import com.pazzioliweb.ventasmodule.repository.PedidoSpecification;
import com.pazzioliweb.ventasmodule.service.CotizacionService;
import com.pazzioliweb.ventasmodule.service.PedidoService;
import com.pazzioliweb.ventasmodule.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final CotizacionMapper cotizacionMapper;
    private final PedidoService pedidoService;

    private final VentaService ventaService;
    private final VendedoresRepository vendedoresRepository;
    private final CajeroRepository cajeroRepository;
    private final DetalleCajeroService detalleCajeroService;
    private final RedisTemplate<String, DatosSesiones> redisTemplate;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    BodegasRepository bodegaRepository;
    @Autowired
    TercerosRepository terceroRepository;
    @Autowired
    public CotizacionServiceImpl(CotizacionRepository cotizacionRepository,
                                 CotizacionMapper cotizacionMapper,
                                 @Lazy PedidoService pedidoService,
                                 @Lazy VentaService ventaService,
                                 VendedoresRepository vendedoresRepository,
                                 CajeroRepository cajeroRepository,
                                 DetalleCajeroService detalleCajeroService,
                                 RedisTemplate<String, DatosSesiones> redisTemplate,
                                 TercerosRepository terceroRepository
    ) {
        this.cotizacionRepository = cotizacionRepository;
        this.cotizacionMapper = cotizacionMapper;
        this.pedidoService = pedidoService;
        this.ventaService = ventaService;
        this.vendedoresRepository = vendedoresRepository;
        this.cajeroRepository = cajeroRepository;
        this.detalleCajeroService = detalleCajeroService;
        this.redisTemplate = redisTemplate;
        this.terceroRepository = terceroRepository;
    }

    // ─── Helper: sesión activa desde Redis ───────────────────────────────────
    private DatosSesiones obtenerSesionActiva() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getCredentials() != null) {
                String sessionId = auth.getCredentials().toString();
                return redisTemplate.opsForValue().get(sessionId);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener sesión activa en cotización: " + e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional
    public void crearCotizacion(CotizacionDTO cotizacionDTO) {
        Cotizacion cotizacion = cotizacionMapper.toEntity(cotizacionDTO);
        cotizacion.setEstado("BORRADOR");
        cotizacion.setFechaCreacion(LocalDate.now());

        // ── Asignar cajero: explícito en DTO o auto-detectar desde sesión Redis ──
        if (cotizacionDTO.getCajeroId() != null) {
            Cajero cajero = cajeroRepository.findById(cotizacionDTO.getCajeroId().intValue())
                    .orElseThrow(() -> new CotizacionException("Cajero no encontrado: " + cotizacionDTO.getCajeroId()));
            cotizacion.setCajero(cajero);
        } else {
            DatosSesiones sesion = obtenerSesionActiva();
            if (sesion != null && sesion.getCajeroId() != null) {
                Cajero cajero = cajeroRepository.findById(sesion.getCajeroId())
                        .orElseThrow(() -> new CotizacionException("Cajero de sesión no encontrado: " + sesion.getCajeroId()));
                cotizacion.setCajero(cajero);
            }
        }

        if (cotizacionDTO.getVendedorId() != null) {
            Vendedores vendedor = vendedoresRepository.findById(cotizacionDTO.getVendedorId())
                    .orElseThrow(() -> new CotizacionException("Vendedor no encontrado: " + cotizacionDTO.getVendedorId()));
            cotizacion.setVendedor(vendedor);
        }

        // ── Asignar vendedor si viene en el DTO ──────────────────────────────
        if (cotizacionDTO.getVendedorId() != null) {
            Vendedores vendedor = vendedoresRepository.findById(cotizacionDTO.getVendedorId())
                    .orElseThrow(() -> new CotizacionException("Vendedor no encontrado: " + cotizacionDTO.getVendedorId()));
            cotizacion.setVendedor(vendedor);
        }

        Bodegas bodega = bodegaRepository.getReferenceById(cotizacionDTO.getBodegaId());
        System.out.println("BODEGA ENCONTRADAS"+bodega.getNombre()+cotizacionDTO.getBodegaId());
        cotizacion.setBodega(bodega);
        Terceros tercero = terceroRepository.getReferenceById(cotizacionDTO.getClienteId().intValue());
        if(tercero==null){
            throw new CotizacionException("Tercero no encontrado: " + cotizacionDTO.getClienteId());
        }

        cotizacion.setCliente(tercero);
        // ── Calcular totales ─────────────────────────────────────────────────
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal ivaTotal = BigDecimal.ZERO;
        BigDecimal descuentosTotal = BigDecimal.ZERO;

        for (DetalleCotizacion detalle : cotizacion.getItems()) {
            detalle.setCotizacion(cotizacion);
            subtotal = subtotal.add(detalle.getTotal());
            ivaTotal = ivaTotal.add(detalle.getIva());
            descuentosTotal = descuentosTotal.add(detalle.getDescuento());
        }

        cotizacion.setGravada(subtotal.subtract(ivaTotal));
        cotizacion.setIva(ivaTotal);
        cotizacion.setDescuentos(descuentosTotal);
        cotizacion.setTotalCotizacion(subtotal);

        Cotizacion guardada = cotizacionRepository.save(cotizacion);

        // ── Registrar movimiento COTIZACION en el cajero (informativo, no afecta caja) ──
        DatosSesiones sesion = obtenerSesionActiva();
        if (sesion != null && sesion.getDetalleCajeroId() != null) {
            try {
                detalleCajeroService.registrarMovimiento(
                        sesion.getDetalleCajeroId(),
                        MovimientoCajero.TipoMovimiento.COTIZACION,
                        guardada.getNumeroCotizacion(),
                        guardada.getId(),
                        guardada.getTotalCotizacion(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        "Cotización " + guardada.getNumeroCotizacion()
                );
            } catch (Exception e) {
                System.out.println("Error al registrar cotización en cajero: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CotizacionDTO> getCotizacionesByCliente(Long clienteId) {
        return cotizacionRepository.findCotizacionesByClienteId(clienteId)
                .stream()
                .map(cotizacionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CotizacionDTO getCotizacionByNumero(String numeroCotizacion) {
        Cotizacion cotizacion = cotizacionRepository.findByNumeroCotizacionWithItems(numeroCotizacion)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));
        return cotizacionMapper.toDto(cotizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CotizacionDTO> getCotizacionesActivas() {
        return cotizacionRepository.findCotizacionesActivas()
                .stream()
                .map(cotizacionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void enviarCotizacion(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));

        if (!"BORRADOR".equals(cotizacion.getEstado())) {
            throw new CotizacionException("Solo se pueden enviar cotizaciones en estado BORRADOR");
        }

        cotizacion.setEstado("ENVIADA");
        cotizacionRepository.save(cotizacion);
    }

    @Override
    @Transactional
    public void aceptarCotizacion(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));

        if (!"ENVIADA".equals(cotizacion.getEstado())) {
            throw new CotizacionException("Solo se pueden aceptar cotizaciones en estado ENVIADA");
        }

        cotizacion.setEstado("ACEPTADA");
        cotizacionRepository.save(cotizacion);
    }

    @Override
    @Transactional
    public void rechazarCotizacion(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));

        if (!"ENVIADA".equals(cotizacion.getEstado())) {
            throw new CotizacionException("Solo se pueden rechazar cotizaciones en estado ENVIADA");
        }

        cotizacion.setEstado("RECHAZADA");
        cotizacionRepository.save(cotizacion);
    }

    @Override
    @Transactional
    public void anularCotizacion(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));

        if ("RECHAZADA".equals(cotizacion.getEstado()) || "VENCIDA".equals(cotizacion.getEstado())) {
            throw new CotizacionException("La cotización ya está cerrada");
        }

        cotizacion.setEstado("VENCIDA");
        cotizacionRepository.save(cotizacion);
    }

    @Override
    @Transactional
    public PedidoDTO convertirAPedido(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));

        if (!"ACEPTADA".equals(cotizacion.getEstado())) {
            throw new CotizacionException("Solo se pueden convertir cotizaciones ACEPTADAS a pedido");
        }

        // Crear PedidoDTO a partir de la cotización
       Long ultimopedido= pedidoRepository.getUltimopedidoId();
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setClienteId(cotizacion.getCliente().getTerceroId().longValue());
        pedidoDTO.setBodegaId(cotizacion.getBodega().getCodigo());
        pedidoDTO.setFechaEmision(LocalDate.now());
        pedidoDTO.setFechaEntregaEsperada(LocalDate.now().plusDays(7));
        pedidoDTO.setObservaciones("Generado desde cotización: " + cotizacion.getNumeroCotizacion());
        pedidoDTO.setSubtotal(cotizacion.getGravada());
        pedidoDTO.setNumeroPedido(ultimopedido.toString());
        pedidoDTO.setIva(cotizacion.getIva());
        pedidoDTO.setTotal(cotizacion.getTotalCotizacion());
        pedidoDTO.setUsuarioCreacion(cotizacion.getUsuarioCreacion());
        pedidoDTO.setCotizacionId(cotizacionId);

        // Propagar cajero de la cotización al pedido
        if (cotizacion.getCajero() != null) {
            pedidoDTO.setCajeroId(Long.valueOf(cotizacion.getCajero().getCajeroId()));
        }

        // Propagar vendedor de la cotización al pedido
        if (cotizacion.getVendedor() != null) {
            pedidoDTO.setVendedorId(cotizacion.getVendedor().getVendedor_id());
        }

        // Mapear items
        List<DetallePedidoDTO> items = cotizacion.getItems().stream().map(dc -> {
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
        cotizacion.setEstado("PEDIDO");
        cotizacionRepository.save(cotizacion);

        return pedidoCreado;
    }

    @Override
    @Transactional
    public VentaDTO convertirAVenta(Long cotizacionId, List<VentaMetodoPagoDTO> metodosPago) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));

        if (!"ACEPTADA".equals(cotizacion.getEstado())) {
            throw new CotizacionException("Solo se pueden facturar cotizaciones ACEPTADAS");
        }

        // Crear VentaDTO a partir de la cotización
        VentaDTO ventaDTO = new VentaDTO();
        ventaDTO.setClienteId(cotizacion.getCliente().getTerceroId().longValue());
        ventaDTO.setBodegaId(cotizacion.getBodega().getCodigo());
        ventaDTO.setFechaEmision(LocalDate.now());
        ventaDTO.setFechaEntregaEsperada(LocalDate.now());
        ventaDTO.setObservaciones("Facturado desde cotización: " + cotizacion.getNumeroCotizacion());
        ventaDTO.setSubtotal(cotizacion.getGravada());
        ventaDTO.setIva(cotizacion.getIva());
        ventaDTO.setTotal(cotizacion.getTotalCotizacion());
        ventaDTO.setUsuarioCreacion(cotizacion.getUsuarioCreacion());
        ventaDTO.setMetodosPago(metodosPago);

        // Propagar cajero de la cotización a la venta
        if (cotizacion.getCajero() != null) {
            ventaDTO.setCajeroId(Long.valueOf(cotizacion.getCajero().getCajeroId()));
        }

        // Propagar vendedor de la cotización a la venta
        if (cotizacion.getVendedor() != null) {
            ventaDTO.setVendedorId(cotizacion.getVendedor().getVendedor_id());
        }

        // Mapear items de cotización a detalles de venta
        List<DetalleVentaDTO> items = cotizacion.getItems().stream().map(dc -> {
            DetalleVentaDTO dv = new DetalleVentaDTO();
            dv.setCodigoProducto(dc.getCodigoProducto());
            dv.setCodigoBarras(dc.getCodigoBarras());
            dv.setDescripcionProducto(dc.getDescripcionProducto());
            dv.setReferenciaVariantes(dc.getReferenciaVariantes());
            dv.setCantidad(dc.getCantidad());
            dv.setPrecioUnitario(dc.getPrecioUnitario());
            dv.setDescuento(dc.getDescuento());
            dv.setIvaPorcentaje(dc.getIva());
            dv.setTotal(dc.getTotal());
            return dv;
        }).collect(Collectors.toList());
        ventaDTO.setItems(items);

        // Crear la venta (descuenta inventario, ajusta precios, etc.)
        ventaService.crearVenta(ventaDTO);

        // Marcar la cotización como facturada
        cotizacion.setEstado("FACTURADA");
        cotizacionRepository.save(cotizacion);

        return ventaDTO;
    }

    @Transactional
    @Override
    public Long getUltimacotizacion() {
        return  cotizacionRepository.getUltimacotizacionId();
    }


    /*
metodo para filtrar pedidos segun parametros
 */
    @Transactional
    @Override
    public List<CotizacionDTO> getCotizacionesByFiltros(Long terceroId, Integer vendedorId, Integer cajeroId,
                                                        LocalDate fechaInicio, LocalDate fechaFin) {
        return  cotizacionRepository
                .findAll(CotizacionSpecification.conFiltros(terceroId, vendedorId, cajeroId, fechaInicio, fechaFin))
                .stream()
                .map( cotizacionMapper::toDto)
                .collect(Collectors.toList());
    }

    // ─── Cambiar estado de una cotización con validación de transiciones ─────
    @Transactional
    @Override
    public void cambiarEstado(Long cotizacionId, String nuevoEstado) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada con ID: " + cotizacionId));

        String estadoActual = cotizacion.getEstado();
        String estadoNuevo  = nuevoEstado.toUpperCase().trim();

        validarTransicion(estadoActual, estadoNuevo, cotizacionId);

        cotizacion.setEstado(estadoNuevo);
        cotizacionRepository.save(cotizacion);
    }

    /**
     * Tabla de transiciones permitidas:
     *  BORRADOR   → ENVIADA, VENCIDA
     *  ENVIADA    → ACEPTADA, RECHAZADA, BORRADOR, VENCIDA
     *  ACEPTADA   → VENCIDA
     *  RECHAZADA  → (terminal)
     *  VENCIDA    → (terminal)
     *  PEDIDO     → (terminal – convertida a pedido)
     *  FACTURADA  → (terminal – convertida a venta)
     */
    private void validarTransicion(String actual, String nuevo, Long cotizacionId) {
        java.util.Map<String, java.util.Set<String>> transiciones = java.util.Map.of(
                "BORRADOR",  java.util.Set.of("ENVIADA", "VENCIDA"),
                "ENVIADA",   java.util.Set.of("ACEPTADA", "RECHAZADA", "BORRADOR", "VENCIDA"),
                "ACEPTADA",  java.util.Set.of("VENCIDA")
        );

        java.util.Set<String> permitidos = transiciones.getOrDefault(actual, java.util.Set.of());

        if (permitidos.isEmpty()) {
            throw new CotizacionException(
                    "La cotización " + cotizacionId + " está en estado " + actual + " y no admite cambios de estado");
        }

        if (!permitidos.contains(nuevo)) {
            throw new CotizacionException(
                    "Transición no permitida: " + actual + " → " + nuevo +
                            ". Estados permitidos desde " + actual + ": " + permitidos);
        }
    }
}

