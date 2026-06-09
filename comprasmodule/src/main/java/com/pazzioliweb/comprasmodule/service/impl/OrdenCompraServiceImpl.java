package com.pazzioliweb.comprasmodule.service.impl;

import com.pazzioliweb.comprasmodule.client.ProductoClient;
import com.pazzioliweb.comprasmodule.dtos.*;
import com.pazzioliweb.comprasmodule.dtos.FinalizarCompraDTO;
import com.pazzioliweb.comprasmodule.entity.DetalleOrdenCompra;
import com.pazzioliweb.comprasmodule.entity.OrdenCompra;
import com.pazzioliweb.comprasmodule.entity.OrdenCompraMetodoPago;
import com.pazzioliweb.comprasmodule.exception.OrdenCompraException;
import com.pazzioliweb.comprasmodule.mapper.OrdenCompraMapper;
import com.pazzioliweb.comprasmodule.repository.OrdenCompraRepository;
import com.pazzioliweb.comprasmodule.service.ConfiguracionComprasService;
import com.pazzioliweb.comprasmodule.service.CuentaPorPagarService;
import com.pazzioliweb.comprasmodule.service.OrdenCompraService;
import com.pazzioliweb.comprasmodule.service.ProductoService;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import com.pazzioliweb.comprobantesmodule.service.AsientoContableService;
import com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService;
import com.pazzioliweb.comprobantesmodule.service.AsientoFallidoService;
import com.pazzioliweb.comprobantesmodule.service.ConfiguracionContableService;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdenCompraServiceImpl implements OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final OrdenCompraMapper ordenCompraMapper;
    private final ProductoService productoService;
    private final ProductoClient productoClient;
    private final CuentaPorPagarService cuentaPorPagarService;
    private final TercerosRepository tercerosRepository;
    private final BodegasRepository bodegasRepository;
    private final AsignacionComprobanteService asignacionComprobante;
    @org.springframework.beans.factory.annotation.Autowired
    private AsientoFallidoService asientoFallidoService;
    @org.springframework.beans.factory.annotation.Autowired
    private com.pazzioliweb.comprobantesmodule.service.PeriodoContableService periodoContableService;
    private final AsientoContableService asientoService;
    private final ConfiguracionContableService configContable;
    private final ConfiguracionComprasService configCompras;
    private final ComprobanteContableRepository comprobanteRepository;
    private final com.pazzioliweb.metodospagomodule.repositori.MetodosPagoRepository metodosPagoRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private com.pazzioliweb.movimientosinventariomodule.service.MovimientoInventarioAutoService movimientoInventarioAutoService;

    @Autowired
    public OrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository,
                                  OrdenCompraMapper ordenCompraMapper,
                                  ProductoService productoService,
                                  ProductoClient productoClient,
                                  CuentaPorPagarService cuentaPorPagarService,
                                  TercerosRepository tercerosRepository,
                                  BodegasRepository bodegasRepository,
                                  AsignacionComprobanteService asignacionComprobante,
                                  AsientoContableService asientoService,
                                  ConfiguracionContableService configContable,
                                  ConfiguracionComprasService configCompras,
                                  ComprobanteContableRepository comprobanteRepository,
                                  com.pazzioliweb.metodospagomodule.repositori.MetodosPagoRepository metodosPagoRepository) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.ordenCompraMapper = ordenCompraMapper;
        this.productoService = productoService;
        this.productoClient = productoClient;
        this.cuentaPorPagarService = cuentaPorPagarService;
        this.tercerosRepository = tercerosRepository;
        this.bodegasRepository = bodegasRepository;
        this.asignacionComprobante = asignacionComprobante;
        this.asientoService = asientoService;
        this.configContable = configContable;
        this.configCompras = configCompras;
        this.comprobanteRepository = comprobanteRepository;
        this.metodosPagoRepository = metodosPagoRepository;
    }

    /** Username autenticado actual; "SYSTEM" si no hay sesión válida. */
    private String obtenerUsuarioAutenticado() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) return "SYSTEM";
            Object principal = auth.getPrincipal();
            if (principal instanceof Usuario u && u.getUsuario() != null) {
                return u.getUsuario();
            }
            String name = auth.getName();
            if (name != null && !"anonymousUser".equals(name) && name.length() <= 250) {
                return name;
            }
        } catch (Exception ignored) {}
        return "SYSTEM";
    }

    /**
     * Determina qué cajero usar para asignar el comprobante de compra (CC/CR):
     *   1. Si el usuario es cajero y su cajero tiene comprobante activo del tipo → usar ese cajero.
     *   2. Si no → caer al cajero default configurado en ConfiguracionCompras.
     *   3. Si tampoco hay default → error claro pidiendo configurarlo.
     */
    /**
     * Devuelve true si ALGÚN método de pago del request tiene tipoNegociacion
     * = "Credito". Equivalente a VentaServiceImpl.tieneCredito(): el método
     * de pago decide el tipo de comprobante, independiente del plazo.
     */
    private boolean tieneMetodoPagoCredito(RealizarOrdenRequestDTO request) {
        if (request.getMetodosPago() == null || request.getMetodosPago().isEmpty()) return false;
        return request.getMetodosPago().stream().anyMatch(mp -> {
            if (mp == null || mp.getMetodoPagoId() == null) return false;
            try {
                com.pazzioliweb.metodospagomodule.entity.MetodosPago m =
                        metodosPagoRepository.findById(mp.getMetodoPagoId()).orElse(null);
                return m != null && m.getTipoNegociacion() != null
                        && "Credito".equalsIgnoreCase(m.getTipoNegociacion().name());
            } catch (Exception e) {
                return false;
            }
        });
    }

    private Integer resolverCajeroParaComprobante(Integer cajeroUsuario, TipoMovimientoComprobante tipo) {
        if (cajeroUsuario != null
                && comprobanteRepository.findActivoByCajeroAndTipo(cajeroUsuario, tipo).isPresent()) {
            return cajeroUsuario;
        }
        Integer cajeroDefault = configCompras.obtenerCajeroDefaultId();
        if (cajeroDefault != null
                && comprobanteRepository.findActivoByCajeroAndTipo(cajeroDefault, tipo).isPresent()) {
            return cajeroDefault;
        }
        throw new OrdenCompraException(
            "No hay cajero válido para asignar el comprobante de " + tipo.name() +
            ". El usuario actual " + (cajeroUsuario == null ? "no es cajero" : "no tiene comprobante " + tipo.name()) +
            " y no se ha configurado un cajero por defecto para compras. " +
            "Configúrelo en Admin → Configuración de compras."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdenCompraDTO> buscarConFiltros(String estado, LocalDate fechaDesde,
                                                 LocalDate fechaHasta, Integer proveedorId,
                                                 Pageable pageable) {
        return ordenCompraRepository.buscarConFiltros(estado, fechaDesde, fechaHasta, proveedorId, pageable)
                .map(ordenCompraMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdenCompraDTO> obtenerPorId(Long id) {
        return ordenCompraRepository.findById(id)
                .map(ordenCompraMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdenCompraDTO> obtenerPorNumeroOrden(String numeroOrden) {
        return ordenCompraRepository.findByNumeroOrden(numeroOrden)
                .map(ordenCompraMapper::toDto);

    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> obtenerPorNumeroOrdenlist(String numeroOrden, Pageable pageable) {
        return ordenCompraRepository.findByNumeroOrdenStartingWithIgnoreCase(numeroOrden,pageable)  .map(ordenCompraMapper::toDto).getContent();

    }


    @Override
    @Transactional
    public OrdenCompraDTO crear(OrdenCompraDTO ordenCompraDTO) {
        // This method is outdated, kept for compatibility
        throw new UnsupportedOperationException("Use realizarOrden for new orders");
    }

    @Override
    @Transactional
    public OrdenCompraDTO actualizar(OrdenCompraDTO ordenCompraDTO) {
        // Implementation for updating existing orders
        OrdenCompra ordenExistente = ordenCompraRepository.findById(ordenCompraDTO.getId())
                .orElseThrow(() -> new OrdenCompraException("La orden de compra no existe"));

        if (!"PENDIENTE".equals(ordenExistente.getEstado())) {
            throw new OrdenCompraException("Solo se pueden modificar órdenes en estado PENDIENTE");
        }

        ordenExistente.setObservaciones(ordenCompraDTO.getObservaciones());
        ordenExistente.setFechaEntregaEsperada(ordenCompraDTO.getFechaEntregaEsperada());
        ordenExistente.setGravada(ordenCompraDTO.getSubtotal());
        ordenExistente.setIva(ordenCompraDTO.getIva());
        ordenExistente.setTotalOrdenCompra(ordenCompraDTO.getTotal());

        // Retenciones (C4): si se editan, persistirlas.
        if (ordenCompraDTO.getRetefuente() != null) ordenExistente.setRetefuente(ordenCompraDTO.getRetefuente());
        if (ordenCompraDTO.getReteiva()    != null) ordenExistente.setReteiva(ordenCompraDTO.getReteiva());
        if (ordenCompraDTO.getReteica()    != null) ordenExistente.setReteica(ordenCompraDTO.getReteica());

        ordenExistente.getItems().clear();
        ordenCompraDTO.getItems().forEach(itemDto -> {
            DetalleOrdenCompra detalle = new DetalleOrdenCompra();
            detalle.setCodigoProducto(itemDto.getCodigoProducto());
            detalle.setDescripcionProducto(itemDto.getDescripcionProducto());
            detalle.setReferenciaVariantes(itemDto.getReferenciaVariantes());
            detalle.setCantidad(itemDto.getCantidad());
            detalle.setPrecioUnitario(itemDto.getPrecioUnitario());
            detalle.setDescuento(itemDto.getDescuento() != null ? itemDto.getDescuento() : BigDecimal.ZERO);
            detalle.setIva(itemDto.getIvaPorcentaje() != null ? itemDto.getIvaPorcentaje() : BigDecimal.ZERO);
            BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            BigDecimal subtotalConDescuento = subtotal.subtract(detalle.getDescuento());
            BigDecimal ivaAmount = subtotalConDescuento.multiply(detalle.getIva()).divide(BigDecimal.valueOf(100));
            detalle.setTotal(subtotalConDescuento.add(ivaAmount));
            detalle.setOrdenCompra(ordenExistente);
            ordenExistente.getItems().add(detalle);
        });

        OrdenCompra ordenActualizada = ordenCompraRepository.save(ordenExistente);
        return ordenCompraMapper.toDto(ordenActualizada);
    }

    @Override
    @Transactional
    public void anular(Long id, String motivo) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new OrdenCompraException("La orden de compra no existe"));

        if ("ANULADA".equals(orden.getEstado())) {
            throw new OrdenCompraException("La orden ya está anulada");
        }

        // Validar periodo contable abierto antes de mover saldos.
        periodoContableService.validarPeriodoAbierto(
                orden.getFechaEmision() != null ? orden.getFechaEmision() : orden.getFechaCreacion());

        // Revertir inventario de lo recibido
        for (DetalleOrdenCompra detalle : orden.getItems()) {
            if (detalle.getCantidadRecibida() != null && detalle.getCantidadRecibida() > 0) {
                productoService.actualizarInventario(
                        detalle.getCodigoProducto(),
                        null, // No tenemos lote específico para reversión
                        -detalle.getCantidadRecibida(),
                        orden.getBodega().getCodigo()
                );
            }
        }

        // ── Anular asiento contable (CC o CR) — NO se elimina, se marca ANULADO ──
        try {
            String tipoDoc = orden.getComprobante() != null
                    ? orden.getComprobante().getTipoMovimiento().name() : "CC";
            asientoService.anularAsientoDeDocumento(tipoDoc, orden.getId());
        } catch (Exception ex) {
            System.out.println("[AnularOC] Error anulando asiento: " + ex.getMessage());
        }

        // ── Anular CxP en lugar de eliminarla (preservar trazabilidad) ──
        try {
            cuentaPorPagarService.anularPorNumeroFactura(orden.getNumeroOrden(), motivo);
        } catch (Exception ex) {
            // Fallback al método legacy si el nuevo no existe
            try {
                cuentaPorPagarService.eliminarPorNumeroFactura(orden.getNumeroOrden());
            } catch (Exception ex2) {
                System.out.println("[AnularOC] Error gestionando CxP: " + ex2.getMessage());
            }
        }

        // NO se borran los items con orden.getItems().clear() — eso destruye trazabilidad.
        orden.setEstado("ANULADA");
        String prev = orden.getObservaciones() != null ? orden.getObservaciones() : "";
        orden.setObservaciones(prev + "\nAnulada: " + motivo);
        ordenCompraRepository.save(orden);
    }

    @Override
    @Transactional
    public void recibirOrden(Long id, List<ItemRecibidoDTO> itemsRecibidos) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new OrdenCompraException("La orden de compra no existe"));

        if ("ANULADA".equals(orden.getEstado()) || "RECIBIDA".equals(orden.getEstado())) {
            throw new OrdenCompraException("No se puede recibir una orden " + orden.getEstado());
        }

        for (ItemRecibidoDTO itemRecibido : itemsRecibidos) {
            DetalleOrdenCompra detalle = orden.getItems().stream()
                    .filter(d -> d.getId().equals(itemRecibido.getDetalleId()))
                    .findFirst()
                    .orElseThrow(() -> new OrdenCompraException("Detalle de orden no encontrado: " + itemRecibido.getDetalleId()));

            int cantidadPendiente = detalle.getCantidad() - detalle.getCantidadRecibida();
            if (itemRecibido.getCantidadRecibida() > cantidadPendiente) {
                throw new OrdenCompraException("La cantidad recibida no puede ser mayor a la pendiente");
            }

            int nuevaCantidadRecibida = detalle.getCantidadRecibida() + itemRecibido.getCantidadRecibida();
            detalle.setCantidadRecibida(nuevaCantidadRecibida);
            detalle.setRecibido(nuevaCantidadRecibida >= detalle.getCantidad());

            actualizarInventario(detalle, itemRecibido, orden.getBodega().getCodigo());
        }

        boolean todosRecibidos = orden.getItems().stream().allMatch(DetalleOrdenCompra::isRecibido);
        boolean algunosRecibidos = orden.getItems().stream().anyMatch(d -> d.getCantidadRecibida() > 0);

        if (todosRecibidos) {
            orden.setEstado("RECIBIDA");
            actualizarCostosYPrecios(orden);
        } else if (algunosRecibidos) {
            orden.setEstado("RECIBIDA_PARCIAL");
        }

        ordenCompraRepository.save(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> obtenerOrdenesPendientes() {
        return ordenCompraRepository.buscarConFiltros("PENDIENTE", null, null, null, Pageable.unpaged())
                .getContent()
                .stream()
                .map(ordenCompraMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> contarTotalOrdenes() {
        long total = ordenCompraRepository.count();
        return Collections.singletonMap("totalOrdenes", total);
    }

    @Override
    @Transactional
    public OrdenCompraDTO realizarOrden(RealizarOrdenRequestDTO request) {
        // Validar periodo contable antes de cualquier escritura.
        if (request.getFechainicial() != null && !request.getFechainicial().isEmpty()) {
            try {
                LocalDate fechaInicial = LocalDate.parse(request.getFechainicial(),
                        DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                periodoContableService.validarPeriodoAbierto(fechaInicial);
            } catch (java.time.format.DateTimeParseException ignore) {
                // Si la fecha no es parseable, se deja pasar — el flujo posterior usa LocalDate.now().
            }
        }

        // 1. Procesar productos: actualizar o crear productos con variantes
        procesarProductosDesdeRequest(request.getOrden_compra().getProducts());

        // 2. Crear la orden de compra
        OrdenCompra ordenCompra = crearOrdenDesdeRequest(request);

        // 3. Crear detalles de la orden
        List<DetalleOrdenCompra> detalles = crearDetallesDesdeRequest(ordenCompra, request.getOrden_compra().getProducts());
        ordenCompra.setItems(detalles);

        // 4. Guardar la orden
        OrdenCompra ordenGuardada = ordenCompraRepository.save(ordenCompra);

        // 5. Procesar métodos de pago (si vienen) — pueden ser parciales
        BigDecimal totalPagado = procesarMetodosPago(ordenGuardada, request);

        // 6. Crear cuenta por pagar (solo por el saldo no pagado)
        crearCuentaPorPagarDesdeRequest(ordenGuardada, request, totalPagado);

        // 7. Generar asiento contable (toma en cuenta los métodos de pago para los créditos)
        generarAsientoCompra(ordenGuardada, request);

        return ordenCompraMapper.toDto(ordenGuardada);
    }

    /**
     * Asiento de compra:
     *   DÉBITO Inventarios (1435)        por subtotal sin IVA
     *   DÉBITO IVA descontable (240810)  por IVA
     *   CRÉDITO CxP Proveedores (2205)   por el total (siempre que la compra registre CxP)
     *
     * Si la compra es CC (contado) y existiera flujo de pago inmediato, sería
     * CRÉDITO directo a la cuenta del medio de pago. Como el sistema actual
     * solo crea CxP, la salida de dinero se contabiliza después al pagar con CE.
     */
    private void generarAsientoCompra(OrdenCompra orden, RealizarOrdenRequestDTO request) {
        try {
            java.util.List<AsientoContableService.LineaDTO> lineas = new java.util.ArrayList<>();

            // ── Bases del asiento ──
            // IMPORTANTE: orden.getTotalOrdenCompra() es el total NETO (el frontend ya le restó
            // las retenciones = lo que el proveedor realmente recibe). Para el asiento necesitamos
            // el total BRUTO (base + IVA) porque el inventario y el IVA descontable NO se reducen
            // por las retenciones — solo se reduce lo que se le paga/debe al proveedor.
            BigDecimal totalNeto = orden.getTotalOrdenCompra() != null ? orden.getTotalOrdenCompra() : BigDecimal.ZERO;
            BigDecimal iva = orden.getIva() != null ? orden.getIva() : BigDecimal.ZERO;

            // Retenciones practicadas al proveedor (pasivo de la empresa con el fisco)
            BigDecimal retefuente = orden.getRetefuente() != null ? orden.getRetefuente() : BigDecimal.ZERO;
            BigDecimal reteivaProv = orden.getReteiva() != null ? orden.getReteiva() : BigDecimal.ZERO;
            BigDecimal reteicaProv = orden.getReteica() != null ? orden.getReteica() : BigDecimal.ZERO;
            BigDecimal totalRetenciones = retefuente.add(reteivaProv).add(reteicaProv);

            // Total bruto = neto + retenciones (revierte la resta hecha en el frontend).
            BigDecimal totalBruto = totalNeto.add(totalRetenciones);
            // Base del inventario = bruto - IVA (incluye gravada + exento, completa, SIN restar retenciones).
            BigDecimal baseInventario = totalBruto.subtract(iva).max(BigDecimal.ZERO);

            // DÉBITO Inventarios (por la base completa, no reducida por retenciones)
            CuentaContable inv = configContable.inventarios().orElse(null);
            if (inv == null) {
                System.out.println("[AsientoCompra] Cuenta 1435 Inventarios no configurada. Asiento omitido.");
                return;
            }
            lineas.add(AsientoContableService.LineaDTO.debito(inv.getId(),
                    baseInventario.compareTo(BigDecimal.ZERO) > 0 ? baseInventario : totalBruto,
                    "Compra " + orden.getNumeroOrden()));

            // DÉBITO IVA descontable
            if (iva.compareTo(BigDecimal.ZERO) > 0) {
                CuentaContable ivaDesc = configContable.ivaDescontable().orElse(null);
                if (ivaDesc != null) {
                    lineas.add(AsientoContableService.LineaDTO.debito(ivaDesc.getId(), iva,
                            "IVA descontable compra " + orden.getNumeroOrden()));
                }
            }

            // CRÉDITO — divide entre métodos de pago de CONTADO (lo realmente pagado) y CxP (saldo a crédito).
            // Un método de pago tipo "Credito" NO es un pago real: es deuda con el proveedor → NO se acredita
            // a caja/banco ni cuenta como pagado; su monto queda como saldo y va a Proveedores (CxP 2205).
            BigDecimal totalPagado = BigDecimal.ZERO;
            if (orden.getMetodosPago() != null) {
                for (OrdenCompraMetodoPago mp : orden.getMetodosPago()) {
                    BigDecimal monto = mp.getMonto() != null ? mp.getMonto() : BigDecimal.ZERO;
                    if (monto.compareTo(BigDecimal.ZERO) <= 0) continue;

                    // Saltar métodos de pago a CRÉDITO (no son egreso de dinero → van a CxP).
                    com.pazzioliweb.metodospagomodule.entity.MetodosPago metodo = mp.getMetodoPago();
                    boolean esMetodoCredito = metodo != null && metodo.getTipoNegociacion() != null
                            && "Credito".equalsIgnoreCase(metodo.getTipoNegociacion().name());
                    if (esMetodoCredito) continue;

                    // Resolver cuenta del método (banco si tiene cuenta bancaria, sino cuenta directa, sino caja)
                    CuentaContable ctaMetodo = resolverCuentaMetodoPagoCompra(metodo);
                    if (ctaMetodo == null) {
                        System.out.println("[AsientoCompra] Método '" + (metodo != null ? metodo.getDescripcion() : "?") + "' sin cuenta. Se asume caja.");
                        ctaMetodo = configContable.cajaGeneral().orElse(null);
                        if (ctaMetodo == null) continue;
                    }
                    lineas.add(AsientoContableService.LineaDTO.credito(ctaMetodo.getId(), monto,
                            "Pago " + (metodo != null ? metodo.getDescripcion() : "") + " compra " + orden.getNumeroOrden()));
                    totalPagado = totalPagado.add(monto);
                }
            }

            // CRÉDITO Retenciones por pagar (la empresa retiene al proveedor y le debe al fisco)
            if (retefuente.compareTo(BigDecimal.ZERO) > 0) {
                CuentaContable rf = configContable.retefuentePagar().orElse(null);
                if (rf != null) {
                    AsientoContableService.LineaDTO rfLinea = AsientoContableService.LineaDTO
                            .credito(rf.getId(), retefuente, "Retención fuente compra " + orden.getNumeroOrden());
                    if (request.getProvedor() != null && request.getProvedor().getTerceroId() != null) {
                        rfLinea.conTercero(request.getProvedor().getTerceroId(), request.getProvedor().getNombre());
                    }
                    lineas.add(rfLinea);
                } else {
                    System.out.println("[AsientoCompra] Cuenta 236505 Retefuente no configurada — retención no se contabiliza.");
                }
            }
            if (reteivaProv.compareTo(BigDecimal.ZERO) > 0) {
                CuentaContable ri = configContable.reteivaPagar().orElse(null);
                if (ri != null) {
                    AsientoContableService.LineaDTO riLinea = AsientoContableService.LineaDTO
                            .credito(ri.getId(), reteivaProv, "Retención IVA compra " + orden.getNumeroOrden());
                    if (request.getProvedor() != null && request.getProvedor().getTerceroId() != null) {
                        riLinea.conTercero(request.getProvedor().getTerceroId(), request.getProvedor().getNombre());
                    }
                    lineas.add(riLinea);
                } else {
                    System.out.println("[AsientoCompra] Cuenta 236540 ReteIVA no configurada — retención no se contabiliza.");
                }
            }
            if (reteicaProv.compareTo(BigDecimal.ZERO) > 0) {
                CuentaContable ric = configContable.reteicaPagar().orElse(null);
                if (ric != null) {
                    AsientoContableService.LineaDTO ricLinea = AsientoContableService.LineaDTO
                            .credito(ric.getId(), reteicaProv, "Retención ICA compra " + orden.getNumeroOrden());
                    if (request.getProvedor() != null && request.getProvedor().getTerceroId() != null) {
                        ricLinea.conTercero(request.getProvedor().getTerceroId(), request.getProvedor().getNombre());
                    }
                    lineas.add(ricLinea);
                } else {
                    System.out.println("[AsientoCompra] Cuenta 236570 ReteICA no configurada — retención no se contabiliza.");
                }
            }

            // CRÉDITO CxP solo por el saldo pendiente.
            // totalNeto YA tiene las retenciones restadas → NO restarlas de nuevo (eso era doble resta).
            BigDecimal saldoCxP = totalNeto.subtract(totalPagado);
            if (saldoCxP.compareTo(new BigDecimal("0.01")) > 0) {
                CuentaContable cxp = configContable.cxpProveedores().orElse(null);
                if (cxp == null) {
                    System.out.println("[AsientoCompra] Cuenta 2205 Proveedores no configurada. Asiento omitido.");
                    return;
                }
                AsientoContableService.LineaDTO creditoLinea = AsientoContableService.LineaDTO
                        .credito(cxp.getId(), saldoCxP, "CxP compra " + orden.getNumeroOrden());
                if (request.getProvedor() != null && request.getProvedor().getTerceroId() != null) {
                    creditoLinea.conTercero(request.getProvedor().getTerceroId(), request.getProvedor().getNombre());
                }
                lineas.add(creditoLinea);
            }

            String tipo = orden.getComprobante() != null
                    ? orden.getComprobante().getTipoMovimiento().name()
                    : "CC";

            asientoService.generarAsiento(
                    orden.getNumeroOrden() != null ? orden.getNumeroOrden() : ("OC-" + orden.getId()),
                    orden.getFechaCreacion() != null ? orden.getFechaCreacion() : LocalDate.now(),
                    "Compra " + orden.getNumeroOrden() + (request.getProvedor() != null ? " - " + request.getProvedor().getNombre() : ""),
                    tipo,
                    orden.getId(),
                    orden.getComprobante(),
                    lineas
            );
        } catch (Exception ex) {
            System.out.println("[AsientoCompra] Error generando asiento (no crítico): " + ex.getMessage());
            asientoFallidoService.registrar("COMPRAS",
                    orden.getComprobante() != null && orden.getComprobante().getTipoMovimiento() != null
                        ? orden.getComprobante().getTipoMovimiento().name() : "CC",
                    orden.getId(), orden.getNumeroOrden(),
                    "Error generando asiento de compra: " + ex.getMessage(), ex);
        }
    }

    /**
     * Resuelve la cuenta contable de un método de pago en una compra.
     * Prioridad: cuenta bancaria → cuenta del método → caja como fallback.
     */
    private CuentaContable resolverCuentaMetodoPagoCompra(com.pazzioliweb.metodospagomodule.entity.MetodosPago metodo) {
        if (metodo == null) return null;
        try {
            if (metodo.getCuentaBancaria() != null && metodo.getCuentaBancaria().getCuentaContable() != null) {
                return metodo.getCuentaBancaria().getCuentaContable();
            }
            if (metodo.getCuentaContable() != null) {
                return metodo.getCuentaContable();
            }
        } catch (Exception ex) {
            System.out.println("[Compra] Error resolviendo cuenta de método de pago: " + ex.getMessage());
        }
        return null;
    }

    private void procesarProductosDesdeRequest(List<RealizarOrdenRequestDTO.ProductoRequestPayloadDTO> products) {
        for (RealizarOrdenRequestDTO.ProductoRequestPayloadDTO product : products) {
            ProductoActualizarCrearDTO productoDTO = mapToProductoActualizarCrearDTO(product);
            productoService.actualizarOCrearProducto(productoDTO);
        }
    }

    private ProductoActualizarCrearDTO mapToProductoActualizarCrearDTO(RealizarOrdenRequestDTO.ProductoRequestPayloadDTO product) {
        ProductoActualizarCrearDTO dto = new ProductoActualizarCrearDTO();
        dto.setCodigo(product.getCodigo());
        dto.setTipoProducto(product.getTipo_producto());
        dto.setDescripcion(product.getDescripcion());
        dto.setReferencia(product.getReferencia());
        dto.setUnidadMedida(product.getUnidad_medida());
        dto.setImpuesto(product.getImpuesto());
        dto.setCosto(product.getCosto());
        dto.setLinea(product.getLinea());
        dto.setGrupo(product.getGrupo());
        dto.setCodigoBarras(product.getCodigobarras());
        dto.setUbicacion(product.getUbicacion());

        if (product.getVariantes() != null && !product.getVariantes().isEmpty()) {
            List<ProductoActualizarCrearDTO.VarianteDTO> variantes = new ArrayList<>();
            for (RealizarOrdenRequestDTO.VariantePayloadDTO variante : product.getVariantes()) {
                ProductoActualizarCrearDTO.VarianteDTO v = new ProductoActualizarCrearDTO.VarianteDTO();
                v.setCodigoBarraVariante(variante.getCodigobarravariante());
                v.setCantidad(variante.getCantidad());
                v.setNotas(variante.getNotas());
                v.setSku(variante.getSku());
                v.setReferenciaVariantes(variante.getReferenciaVariantes());

                if (variante.getExistencias() != null) {
                    List<ProductoActualizarCrearDTO.ExistenciaDTO> existencias = new ArrayList<>();
                    for (RealizarOrdenRequestDTO.ExistenciaDTO exis : variante.getExistencias()) {
                        ProductoActualizarCrearDTO.ExistenciaDTO e = new ProductoActualizarCrearDTO.ExistenciaDTO();
                        e.setExistenciaId(exis.getExistenciaId());
                        e.setBodegaId(exis.getBodegaId());
                        e.setBodega(exis.getBodega());
                        e.setCantidad(exis.getCantidad());
                        e.setMinimo(exis.getMinimo());
                        e.setMaximo(exis.getMaximo());
                        existencias.add(e);
                    }
                    v.setExistencias(existencias);
                }

                if (variante.getAtributos() != null) {
                    List<ProductoActualizarCrearDTO.AtributoDTO> atributos = new ArrayList<>();
                    for (RealizarOrdenRequestDTO.AtributoPayloadDTO attr : variante.getAtributos()) {
                        ProductoActualizarCrearDTO.AtributoDTO a = new ProductoActualizarCrearDTO.AtributoDTO();
                        a.setNombre(attr.getNombre());
                        a.setValor(attr.getValor());
                        atributos.add(a);
                    }
                    v.setAtributos(atributos);
                }

                if (variante.getPrecios() != null) {
                    List<ProductoActualizarCrearDTO.PrecioDTO> precios = new ArrayList<>();
                    for (RealizarOrdenRequestDTO.PrecioDTO prec : variante.getPrecios()) {
                        ProductoActualizarCrearDTO.PrecioDTO p = new ProductoActualizarCrearDTO.PrecioDTO();
                        p.setIdTipoPrecio(prec.getIdTipoPrecio());
                        p.setValor(prec.getValor());
                        precios.add(p);
                    }
                    v.setPrecios(precios);
                }

                v.setDescuento(variante.getDescuento());
                variantes.add(v);
            }
            dto.setVariantes(variantes);
        }

        return dto;
    }

    private OrdenCompra crearOrdenDesdeRequest(RealizarOrdenRequestDTO request) {
        OrdenCompra orden = new OrdenCompra();
        // ─── Comprobante contable (CC contado / CR crédito) ───
        // Estrategia: si el usuario es cajero y su cajero tiene comprobante CC/CR, se usa.
        // Si no, se cae al cajero default de compras configurado a nivel de empresa.
        //
        // El TIPO lo determina EL MÉTODO DE PAGO, no el plazo:
        //   - Si algún método de pago tiene tipoNegociacion = "Credito" → CR
        //     (y solo en ese caso se aplica el plazo para la fecha de vencimiento).
        //   - Cualquier otro método de pago (efectivo, transferencia, etc.) → CC (contado),
        //     sin importar el plazo que se haya escrito.
        //   - Fallback solo si NO hay métodos de pago: flag explícito esCredito.
        boolean esCredito;
        if (tieneMetodoPagoCredito(request)) {
            esCredito = true;
        } else if (request.getMetodosPago() != null && !request.getMetodosPago().isEmpty()) {
            // Hay métodos de pago y ninguno es de crédito → contado.
            esCredito = false;
        } else {
            // Sin métodos de pago seleccionados: respetar solo el flag explícito (NO el plazo).
            esCredito = Boolean.TRUE.equals(request.getEsCredito());
        }
        TipoMovimientoComprobante tipo = esCredito
                ? TipoMovimientoComprobante.CR
                : TipoMovimientoComprobante.CC;
        Integer cajeroEfectivo = resolverCajeroParaComprobante(request.getCajeroId(), tipo);
        try {
            AsignacionComprobanteService.Resultado r =
                    asignacionComprobante.asignar(cajeroEfectivo, tipo);
            orden.setComprobante(r.getComprobante());
            orden.setConsecutivoComprobante(r.getConsecutivo());
            orden.setNumeroOrden(r.getNumeroDocumento());
            orden.setCajeroId(cajeroEfectivo);
        } catch (AsignacionComprobanteService.ComprobanteNoConfiguradoException ex) {
            throw new OrdenCompraException(ex.getMessage());
        }
        orden.setEstado("PENDIENTE");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate fechaInicial;
        if (request.getFechainicial() != null && !request.getFechainicial().isEmpty()) {
            fechaInicial = LocalDate.parse(request.getFechainicial(), formatter);
        } else {
            fechaInicial = LocalDate.now();
        }
        orden.setFechaEmision(fechaInicial);

        LocalDate fechaEntrega;
        if (request.getFechafinal() != null && !request.getFechafinal().isEmpty()) {
            fechaEntrega = LocalDate.parse(request.getFechafinal(), formatter);
        } else if (esCredito) {
            // Solo en compras a CRÉDITO se aplica el plazo para la fecha de vencimiento.
            fechaEntrega = fechaInicial.plusDays(request.getPlazo() != null ? request.getPlazo() : 30);
        } else {
            // Contado: vence el mismo día (el plazo escrito no aplica).
            fechaEntrega = fechaInicial;
        }
        orden.setFechaEntregaEsperada(fechaEntrega);

        orden.setUsuarioCreacion(obtenerUsuarioAutenticado());
        orden.setFechaCreacion(LocalDate.now());

        orden.setGravada(request.getOrden_compra().getGravada());
        orden.setIva(request.getOrden_compra().getIva());
        orden.setDescuentos(request.getOrden_compra().getDescuentos());
        orden.setTotalOrdenCompra(request.getOrden_compra().getTotalOrdenCompra());

        // Retenciones aplicadas al proveedor (la empresa es agente retenedor).
        orden.setRetefuente(parseMoneyOrZero(request.getOrden_compra().getRetefuente()));
        orden.setReteiva(parseMoneyOrZero(request.getOrden_compra().getReteiva()));
        orden.setReteica(parseMoneyOrZero(request.getOrden_compra().getReteica()));

        // Set proveedor
        orden.setProveedor(tercerosRepository.findById(request.getProvedor().getTerceroId())
                .orElseThrow(() -> new OrdenCompraException("Proveedor no encontrado")));

        orden.setBodega(bodegasRepository.findByCodigo(request.getBodegaId())
                .orElseThrow(() -> new OrdenCompraException("Bodega no encontrada")));

        return orden;
    }

    private List<DetalleOrdenCompra> crearDetallesDesdeRequest(OrdenCompra orden, List<RealizarOrdenRequestDTO.ProductoRequestPayloadDTO> products) {
        List<DetalleOrdenCompra> detalles = new ArrayList<>();
        for (RealizarOrdenRequestDTO.ProductoRequestPayloadDTO product : products) {
            if (product.getVariantes() != null && !product.getVariantes().isEmpty()) {
                for (RealizarOrdenRequestDTO.VariantePayloadDTO variante : product.getVariantes()) {
                    DetalleOrdenCompra detalle = new DetalleOrdenCompra();
                    detalle.setOrdenCompra(orden);
                    detalle.setCodigoProducto(product.getCodigo());
                    detalle.setCodigoBarras(variante.getCodigobarravariante());
                    detalle.setDescripcionProducto(product.getDescripcion());
                    detalle.setObservacionProducto(variante.getNotas() != null ? String.join(", ", variante.getNotas()) : "");
                    detalle.setReferenciaVariantes(variante.getReferenciaVariantes());
                    detalle.setCantidad(variante.getCantidad());
                    detalle.setPrecioUnitario(product.getCosto());
                    detalle.setDescuento(variante.getDescuento() != null ? variante.getDescuento() : BigDecimal.ZERO);
                    detalle.setIva(product.getImpuesto() != null ? BigDecimal.valueOf(product.getImpuesto()) : BigDecimal.ZERO);
                    BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                    BigDecimal subtotalConDescuento = subtotal.subtract(detalle.getDescuento());
                    BigDecimal ivaAmount = subtotalConDescuento.multiply(detalle.getIva()).divide(BigDecimal.valueOf(100));
                    detalle.setTotal(subtotalConDescuento.add(ivaAmount));
                    detalle.setSku(variante.getSku());
                    detalle.setRecibido(false);
                    detalle.setCantidadRecibida(0);
                    detalle.setManifiesto(variante.getManifiesto());
                    detalles.add(detalle);
                }
            } else {
                DetalleOrdenCompra detalle = new DetalleOrdenCompra();
                detalle.setOrdenCompra(orden);
                detalle.setCodigoProducto(product.getCodigo());
                detalle.setCodigoBarras(product.getCodigobarras());
                detalle.setDescripcionProducto(product.getDescripcion());
                detalle.setObservacionProducto("");
             //  detalle.setSku(product.set);
                detalle.setReferenciaVariantes(product.getReferencia());
                detalle.setCantidad(0);
                detalle.setPrecioUnitario(product.getCosto());
                detalle.setDescuento(BigDecimal.ZERO);
                detalle.setIva(product.getImpuesto() != null ? BigDecimal.valueOf(product.getImpuesto()) : BigDecimal.ZERO);
                BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                BigDecimal subtotalConDescuento = subtotal.subtract(detalle.getDescuento());
                BigDecimal ivaAmount = subtotalConDescuento.multiply(detalle.getIva()).divide(BigDecimal.valueOf(100));
                detalle.setTotal(subtotalConDescuento.add(ivaAmount));
                detalle.setRecibido(false);
                detalle.setCantidadRecibida(0);
                detalles.add(detalle);
            }
        }
        return detalles;
    }

    /**
     * Persiste los métodos de pago de la orden (si vienen en el request).
     * Devuelve la suma total pagada, que se usa para calcular el saldo pendiente de CxP.
     */
    private BigDecimal procesarMetodosPago(OrdenCompra orden, RealizarOrdenRequestDTO request) {
        BigDecimal totalPagado = BigDecimal.ZERO;
        if (request.getMetodosPago() == null || request.getMetodosPago().isEmpty()) {
            return totalPagado;
        }
        java.util.List<OrdenCompraMetodoPago> persistidos = new java.util.ArrayList<>();
        for (RealizarOrdenRequestDTO.MetodoPagoCompraDTO mpDto : request.getMetodosPago()) {
            if (mpDto.getMetodoPagoId() == null || mpDto.getMonto() == null) continue;
            if (mpDto.getMonto().compareTo(BigDecimal.ZERO) <= 0) continue;
            try {
                com.pazzioliweb.metodospagomodule.entity.MetodosPago metodo =
                        metodosPagoRepository.findById(mpDto.getMetodoPagoId()).orElse(null);
                if (metodo == null) continue;
                OrdenCompraMetodoPago mp = new OrdenCompraMetodoPago();
                mp.setOrdenCompra(orden);
                mp.setMetodoPago(metodo);
                mp.setMonto(mpDto.getMonto());
                mp.setReferencia(mpDto.getReferencia());
                persistidos.add(mp);
                // Los métodos de pago a CRÉDITO NO son pago real → no suman a "pagado"
                // (su monto queda como saldo y genera la cuenta por pagar al proveedor).
                boolean esMetodoCredito = metodo.getTipoNegociacion() != null
                        && "Credito".equalsIgnoreCase(metodo.getTipoNegociacion().name());
                if (!esMetodoCredito) {
                    totalPagado = totalPagado.add(mpDto.getMonto());
                }
            } catch (Exception ex) {
                System.out.println("[OrdenCompra] Error procesando método de pago: " + ex.getMessage());
            }
        }
        orden.setMetodosPago(persistidos);
        ordenCompraRepository.save(orden);
        return totalPagado;
    }

    /**
     * Crea la CxP solo por el saldo NO pagado al proveedor.
     *
     * orden.getTotalOrdenCompra() ya es el total NETO (el frontend le restó las retenciones),
     * por eso aquí NO se vuelven a restar (antes había doble resta).
     *   Saldo CxP = totalNeto − totalPagado
     * Si saldoCxP &lt;= 0, NO se crea CxP.
     */
    private void crearCuentaPorPagarDesdeRequest(OrdenCompra orden, RealizarOrdenRequestDTO request,
                                                  BigDecimal totalPagado) {
        BigDecimal saldoPendiente = orden.getTotalOrdenCompra()
                .subtract(totalPagado != null ? totalPagado : BigDecimal.ZERO);
        if (saldoPendiente.compareTo(new BigDecimal("0.01")) <= 0) {
            // Pagado completo de contado, no se crea CxP
            return;
        }

        CuentaPorPagarDTO cuenta = new CuentaPorPagarDTO();
        cuenta.setNit(request.getProvedor().getIdentificacion());
        cuenta.setNombre(request.getProvedor().getNombre());
        cuenta.setNumeroFactura(orden.getNumeroOrden());
        cuenta.setFechaVencimiento(orden.getFechaEntregaEsperada());
        cuenta.setValorNeto(saldoPendiente);  // solo el saldo no pagado (total neto - pagado)
        cuenta.setEstado("PENDIENTE");
        cuenta.setProveedorId(request.getProvedor().getTerceroId());

        // Retenciones proporcionales al saldo pendiente vs total NETO de la orden
        BigDecimal totalNeto = orden.getTotalOrdenCompra();
        if (totalNeto != null && totalNeto.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal prop = saldoPendiente.divide(totalNeto, 10, java.math.RoundingMode.HALF_UP);
            BigDecimal rf = orden.getRetefuente() != null ? orden.getRetefuente() : BigDecimal.ZERO;
            BigDecimal rv = orden.getReteiva() != null ? orden.getReteiva() : BigDecimal.ZERO;
            BigDecimal rc = orden.getReteica() != null ? orden.getReteica() : BigDecimal.ZERO;
            cuenta.setRetefuente(rf.multiply(prop).setScale(2, java.math.RoundingMode.HALF_UP));
            cuenta.setReteiva(rv.multiply(prop).setScale(2, java.math.RoundingMode.HALF_UP));
            cuenta.setReteica(rc.multiply(prop).setScale(2, java.math.RoundingMode.HALF_UP));
        }

        cuentaPorPagarService.crear(cuenta);
    }

    /** Parsea un valor monetario string del DTO. Acepta null, vacío, comas/decimales. */
    private BigDecimal parseMoneyOrZero(String s) {
        if (s == null || s.isBlank()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(s.replace(",", "").trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    /** Suma de retenciones aplicadas en la orden (fuente + IVA + ICA). */
    private BigDecimal totalRetenciones(OrdenCompra orden) {
        BigDecimal rf = orden.getRetefuente() != null ? orden.getRetefuente() : BigDecimal.ZERO;
        BigDecimal ri = orden.getReteiva() != null ? orden.getReteiva() : BigDecimal.ZERO;
        BigDecimal ric = orden.getReteica() != null ? orden.getReteica() : BigDecimal.ZERO;
        return rf.add(ri).add(ric);
    }


    private void actualizarInventario(DetalleOrdenCompra detalle, ItemRecibidoDTO itemRecibido, int bodegaId) {
        productoService.actualizarInventario(
                detalle.getCodigoProducto(),
                itemRecibido.getLote(),
                itemRecibido.getCantidadRecibida(),
                bodegaId
        );
    }

    private void actualizarCostosYPrecios(OrdenCompra orden) {
        for (DetalleOrdenCompra detalle : orden.getItems()) {
            if (detalle.getCantidadRecibida() > 0) {
                ProductoActualizarCrearDTO productoDTO = new ProductoActualizarCrearDTO();
                productoDTO.setCodigo(detalle.getCodigoProducto());
                productoDTO.setCosto(detalle.getPrecioUnitario());

                // Add sale price, assuming 30% markup
                List<ProductoActualizarCrearDTO.PrecioDTO> precios = new ArrayList<>();
                ProductoActualizarCrearDTO.PrecioDTO precioVenta = new ProductoActualizarCrearDTO.PrecioDTO();
                precioVenta.setIdTipoPrecio(1); // Assuming 1 is the ID for sale price type
                precioVenta.setValor(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(1.3)));
                precios.add(precioVenta);
                productoDTO.setPrecios(precios);

                productoService.actualizarOCrearProducto(productoDTO);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> obtenerTodasLasOrdenes() {
        return ordenCompraRepository.findAll()
                .stream()
                .map(ordenCompraMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> obtenerOrdenesPorProveedorYEstado(Integer proveedorId, String estado) {
        if(proveedorId==0){
            return ordenCompraRepository.buscarConFiltrossinpro(estado, null, null,  Pageable.unpaged()) .getContent()
                    .stream()
                    .map(ordenCompraMapper::toDto)
                    .collect(Collectors.toList());
        }
        return ordenCompraRepository.buscarConFiltros(estado, null, null, proveedorId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(ordenCompraMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleOrdenCompraDTO> obtenerDetallesPorNumeroOrden(String numeroOrden) {
        return ordenCompraRepository.findByNumeroOrden(numeroOrden)
                .map(orden -> ordenCompraMapper.toDetalleDtoList(orden.getItems()))
                .orElse(Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long obtenerSiguienteId() {
        Optional<Long> maxId = ordenCompraRepository.findMaxId();
        return maxId.orElse(0L) + 1;
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // NUEVO FLUJO: Orden Simple → Ingreso con Finalización
    // ══════════════════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public OrdenCompraDTO realizarOrdenSimple(RealizarOrdenRequestDTO request) {
        if (request.getFechainicial() != null && !request.getFechainicial().isEmpty()) {
            try {
                LocalDate fecha = LocalDate.parse(request.getFechainicial(),
                        java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                periodoContableService.validarPeriodoAbierto(fecha);
            } catch (java.time.format.DateTimeParseException ignore) {}
        }

        // 1. Crear/actualizar productos en catálogo
        procesarProductosDesdeRequest(request.getOrden_compra().getProducts());

        // 2. Construir la orden SIN comprobante ni contabilidad
        OrdenCompra orden = new OrdenCompra();
        orden.setEstado("PENDIENTE");
        orden.setUsuarioCreacion(obtenerUsuarioAutenticado());
        orden.setFechaCreacion(LocalDate.now());

        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate fechaEmision = (request.getFechainicial() != null && !request.getFechainicial().isBlank())
                ? LocalDate.parse(request.getFechainicial(), fmt)
                : LocalDate.now();
        orden.setFechaEmision(fechaEmision);

        LocalDate fechaEntrega = (request.getFechafinal() != null && !request.getFechafinal().isBlank())
                ? LocalDate.parse(request.getFechafinal(), fmt)
                : fechaEmision.plusDays(request.getPlazo() != null ? request.getPlazo() : 0);
        orden.setFechaEntregaEsperada(fechaEntrega);

        orden.setGravada(request.getOrden_compra().getGravada());
        orden.setIva(request.getOrden_compra().getIva());
        orden.setDescuentos(request.getOrden_compra().getDescuentos());
        orden.setTotalOrdenCompra(request.getOrden_compra().getTotalOrdenCompra());
        orden.setRetefuente(parseMoneyOrZero(request.getOrden_compra().getRetefuente()));
        orden.setReteiva(parseMoneyOrZero(request.getOrden_compra().getReteiva()));
        orden.setReteica(parseMoneyOrZero(request.getOrden_compra().getReteica()));
        orden.setCajeroId(request.getCajeroId());

        orden.setProveedor(tercerosRepository.findById(request.getProvedor().getTerceroId())
                .orElseThrow(() -> new OrdenCompraException("Proveedor no encontrado")));
        orden.setBodega(bodegasRepository.findByCodigo(request.getBodegaId())
                .orElseThrow(() -> new OrdenCompraException("Bodega no encontrada")));

        // 3. Guardar para obtener el ID y generar número OC.
        // numero_orden es NOT NULL: se pone un placeholder antes del primer INSERT,
        // luego se asigna el número secuencial usando el ID generado.
        // numero_oc queda permanente; numero_orden se reemplazará con el comprobante al finalizar.
        orden.setNumeroOrden("OC-TEMP");
        OrdenCompra ordenGuardada = ordenCompraRepository.save(orden);
        String numeroOc = "OC-" + String.format("%06d", ordenGuardada.getId());
        ordenGuardada.setNumeroOrden(numeroOc);
        ordenGuardada.setNumeroOc(numeroOc);

        // 4. Crear detalles
        List<DetalleOrdenCompra> detalles = crearDetallesDesdeRequest(ordenGuardada, request.getOrden_compra().getProducts());
        ordenGuardada.setItems(detalles);
        ordenGuardada = ordenCompraRepository.save(ordenGuardada);

        // 5. Guardar métodos de pago — necesarios para determinar CC vs CR al hacer el ingreso
        procesarMetodosPago(ordenGuardada, request);

        return ordenCompraMapper.toDto(ordenGuardada);
    }

    @Override
    @Transactional
    public OrdenCompraDTO finalizarIngreso(Long ordenId, FinalizarCompraDTO dto) {
        OrdenCompra orden = ordenCompraRepository.findById(ordenId)
                .orElseThrow(() -> new OrdenCompraException("Orden de compra no encontrada: " + ordenId));

        if ("RECIBIDA".equals(orden.getEstado()) || "ANULADA".equals(orden.getEstado())) {
            throw new OrdenCompraException("La orden ya fue recibida o está anulada");
        }

        // Validar periodo contable
        LocalDate fechaRef = orden.getFechaEmision() != null ? orden.getFechaEmision() : LocalDate.now();
        if (dto.getFechaFactura() != null && !dto.getFechaFactura().isBlank()) {
            try {
                fechaRef = LocalDate.parse(dto.getFechaFactura(),
                        java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            } catch (Exception ignore) {}
        }
        periodoContableService.validarPeriodoAbierto(fechaRef);

        // 1. Actualizar items con precios y cantidades definitivas
        BigDecimal gravada = BigDecimal.ZERO;
        BigDecimal ivaTotal = BigDecimal.ZERO;
        BigDecimal descuentosTotal = BigDecimal.ZERO;

        if (dto.getItems() != null) {
            for (FinalizarCompraDTO.ItemFinalDTO itemDto : dto.getItems()) {
                if (itemDto.getDetalleId() == null) continue;
                DetalleOrdenCompra detalle = orden.getItems().stream()
                        .filter(d -> d.getId().equals(itemDto.getDetalleId()))
                        .findFirst()
                        .orElse(null);
                if (detalle == null) continue;

                if (itemDto.getPrecioUnitario() != null) detalle.setPrecioUnitario(itemDto.getPrecioUnitario());
                if (itemDto.getIvaPorcentaje() != null)  detalle.setIva(itemDto.getIvaPorcentaje());
                if (itemDto.getDescuento() != null)      detalle.setDescuento(itemDto.getDescuento());
                if (itemDto.getManifiesto() != null)     detalle.setManifiesto(itemDto.getManifiesto());

                Integer cant = itemDto.getCantidadRecibida() != null ? itemDto.getCantidadRecibida() : 0;
                detalle.setCantidadRecibida(cant);
                detalle.setRecibido(Boolean.TRUE.equals(itemDto.getRecibido()));

                // Recalcular total del item
                BigDecimal precio = detalle.getPrecioUnitario() != null ? detalle.getPrecioUnitario() : BigDecimal.ZERO;
                BigDecimal desc   = detalle.getDescuento() != null ? detalle.getDescuento() : BigDecimal.ZERO;
                BigDecimal ivaPct = detalle.getIva() != null ? detalle.getIva() : BigDecimal.ZERO;
                BigDecimal base   = precio.multiply(BigDecimal.valueOf(cant)).subtract(desc).max(BigDecimal.ZERO);
                BigDecimal ivaAmt = base.multiply(ivaPct).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                detalle.setTotal(base.add(ivaAmt));

                gravada = gravada.add(base);
                ivaTotal = ivaTotal.add(ivaAmt);
                descuentosTotal = descuentosTotal.add(desc);
            }
        }

        // Usar totales del DTO si vienen (el frontend ya los calculó), de lo contrario usar los calculados
        BigDecimal gravadaFinal = dto.getGravada() != null ? dto.getGravada() : gravada;
        BigDecimal ivaFinal     = dto.getIva()     != null ? dto.getIva()     : ivaTotal;
        BigDecimal rf = dto.getRetefuente() != null ? dto.getRetefuente() : BigDecimal.ZERO;
        BigDecimal rv = dto.getReteiva()    != null ? dto.getReteiva()    : BigDecimal.ZERO;
        BigDecimal ric = dto.getReteica()   != null ? dto.getReteica()    : BigDecimal.ZERO;
        BigDecimal descFinal = dto.getDescuentos() != null ? dto.getDescuentos() : BigDecimal.ZERO;

        BigDecimal totalBruto = gravadaFinal.add(ivaFinal);
        BigDecimal totalNeto  = dto.getTotalFinal() != null ? dto.getTotalFinal()
                : totalBruto.subtract(rf).subtract(rv).subtract(ric).subtract(descFinal).max(BigDecimal.ZERO);

        // Actualizar totales en la orden
        orden.setGravada(gravadaFinal);
        orden.setIva(ivaFinal);
        orden.setDescuentos(descFinal);
        orden.setRetefuente(rf);
        orden.setReteiva(rv);
        orden.setReteica(ric);
        orden.setTotalOrdenCompra(totalNeto);

        // 2. Determinar tipo de comprobante (CC contado / CR crédito).
        // Si el ingreso no trae métodos de pago (flujo normal: se definieron en la OC),
        // se leen los métodos ya guardados en la orden para determinar CC vs CR.
        boolean esCredito;
        if (dto.getMetodosPago() != null && !dto.getMetodosPago().isEmpty()) {
            esCredito = tienePagoCredito(dto.getMetodosPago());
        } else {
            esCredito = tieneMetodoPagoCreditoEnOrden(orden.getMetodosPago());
        }
        TipoMovimientoComprobante tipo = esCredito ? TipoMovimientoComprobante.CR : TipoMovimientoComprobante.CC;
        Integer cajeroId = dto.getCajeroId() != null ? dto.getCajeroId() : orden.getCajeroId();
        Integer cajeroEfectivo = resolverCajeroParaComprobante(cajeroId, tipo);

        try {
            AsignacionComprobanteService.Resultado r = asignacionComprobante.asignar(cajeroEfectivo, tipo);
            orden.setComprobante(r.getComprobante());
            orden.setConsecutivoComprobante(r.getConsecutivo());
            // Preservar el número OC original si aún no se había guardado
            if (orden.getNumeroOc() == null && orden.getNumeroOrden() != null
                    && orden.getNumeroOrden().startsWith("OC-")) {
                orden.setNumeroOc(orden.getNumeroOrden());
            }
            // numero_orden pasa a ser el número del comprobante contable (CC-X / CR-X)
            orden.setNumeroOrden(r.getNumeroDocumento());
            orden.setCajeroId(cajeroEfectivo);
        } catch (AsignacionComprobanteService.ComprobanteNoConfiguradoException ex) {
            throw new OrdenCompraException(ex.getMessage());
        }

        // 3. Procesar métodos de pago
        BigDecimal totalPagado = procesarMetodosPagoFinalizar(orden, dto.getMetodosPago());

        // 4. Estado de la orden
        boolean allReceived = orden.getItems().stream()
                .allMatch(d -> d.getCantidadRecibida() != null && d.getCantidadRecibida() >= d.getCantidad());
        orden.setEstado(allReceived ? "RECIBIDA" : "RECIBIDA_PARCIAL");

        // 5. Guardar orden con todos los cambios
        ordenCompraRepository.save(orden);

        // 6. Crear CxP solo si hay saldo pendiente (crédito no cubierto por pagos de contado)
        BigDecimal saldoCxP = totalNeto.subtract(totalPagado);
        if (saldoCxP.compareTo(new BigDecimal("0.01")) > 0) {
            CuentaPorPagarDTO cxp = new CuentaPorPagarDTO();
            cxp.setNit(orden.getProveedor().getIdentificacion());
            cxp.setNombre(orden.getProveedor().getRazonSocial() != null
                    ? orden.getProveedor().getRazonSocial()
                    : orden.getProveedor().getNombre1());
            cxp.setNumeroFactura(orden.getNumeroOrden());
            cxp.setNumeroFacturaProveedor(dto.getNumeroFacturaProveedor());
            cxp.setFechaVencimiento(orden.getFechaEntregaEsperada());
            cxp.setValorNeto(saldoCxP);
            cxp.setEstado("PENDIENTE");
            cxp.setProveedorId(dto.getProveedorId() != null ? dto.getProveedorId()
                    : orden.getProveedor().getTerceroId());
            // Retenciones proporcionales al saldo
            if (totalNeto.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal prop = saldoCxP.divide(totalNeto, 10, java.math.RoundingMode.HALF_UP);
                cxp.setRetefuente(rf.multiply(prop).setScale(2, java.math.RoundingMode.HALF_UP));
                cxp.setReteiva(rv.multiply(prop).setScale(2, java.math.RoundingMode.HALF_UP));
                cxp.setReteica(ric.multiply(prop).setScale(2, java.math.RoundingMode.HALF_UP));
            }
            cuentaPorPagarService.crear(cxp);
        }

        // 7. Generar asiento contable — igual que generarAsientoCompra pero con datos del DTO
        try {
            generarAsientoCompraFinalizar(orden, dto, totalPagado, totalNeto, totalBruto, gravadaFinal, ivaFinal, rf, rv, ric);
        } catch (Exception ex) {
            System.out.println("[FinalizarIngreso] Error asiento (no crítico): " + ex.getMessage());
            asientoFallidoService.registrar("COMPRAS",
                    orden.getComprobante() != null ? orden.getComprobante().getTipoMovimiento().name() : tipo.name(),
                    orden.getId(), orden.getNumeroOrden(),
                    "Error asiento finalización: " + ex.getMessage(), ex);
        }

        // 8. Actualizar inventario
        for (DetalleOrdenCompra detalle : orden.getItems()) {
            Integer cant = detalle.getCantidadRecibida();
            if (cant == null || cant <= 0) continue;
            try {
                productoClient.actualizarInventario(
                        detalle.getCodigoProducto(),
                        detalle.getReferenciaVariantes(),
                        cant,
                        orden.getBodega().getCodigo());
            } catch (Exception ex) {
                throw new OrdenCompraException("Error actualizando inventario de "
                        + detalle.getCodigoProducto() + ": " + ex.getMessage());
            }
        }

        // 9. Movimiento inventario (Kardex)
        try {
            List<com.pazzioliweb.movimientosinventariomodule.service.MovimientoInventarioAutoService.ItemMovimiento> kardexItems =
                    com.pazzioliweb.movimientosinventariomodule.service.MovimientoInventarioAutoService.nuevaLista();
            for (DetalleOrdenCompra d : orden.getItems()) {
                if (d.getCantidadRecibida() == null || d.getCantidadRecibida() <= 0) continue;
                double costoUnit = d.getPrecioUnitario() != null ? d.getPrecioUnitario().doubleValue() : 0.0;
                double ivaPct    = d.getIva() != null ? d.getIva().doubleValue() : 0.0;
                double base      = costoUnit * d.getCantidadRecibida();
                kardexItems.add(new com.pazzioliweb.movimientosinventariomodule.service.MovimientoInventarioAutoService.ItemMovimiento(
                        d.getCodigoProducto(), d.getReferenciaVariantes(),
                        d.getCantidadRecibida().doubleValue(), costoUnit,
                        base * (1.0 + ivaPct / 100.0)));
            }
            if (!kardexItems.isEmpty()) {
                String tipoComp = orden.getComprobante() != null
                        && orden.getComprobante().getTipoMovimiento() != null
                        ? orden.getComprobante().getTipoMovimiento().name() : "CC";
                movimientoInventarioAutoService.registrarEntradaPorCompra(
                        orden.getNumeroOrden(), orden.getId(),
                        orden.getBodega().getCodigo(),
                        orden.getFechaCreacion() != null ? orden.getFechaCreacion() : LocalDate.now(),
                        kardexItems, tipoComp);
            }
        } catch (Exception ex) {
            System.out.println("[FinalizarIngreso] Movimiento inventario (no crítico): " + ex.getMessage());
        }

        return ordenCompraMapper.toDto(orden);
    }

    /** Verifica si alguno de los métodos de pago YA GUARDADOS en la orden es de tipo Crédito. */
    private boolean tieneMetodoPagoCreditoEnOrden(List<OrdenCompraMetodoPago> metodosPago) {
        if (metodosPago == null || metodosPago.isEmpty()) return false;
        return metodosPago.stream().anyMatch(mp -> {
            com.pazzioliweb.metodospagomodule.entity.MetodosPago m = mp.getMetodoPago();
            return m != null && m.getTipoNegociacion() != null
                    && "Credito".equalsIgnoreCase(m.getTipoNegociacion().name());
        });
    }

    private boolean tienePagoCredito(List<FinalizarCompraDTO.MetodoPagoDTO> metodosPago) {
        if (metodosPago == null || metodosPago.isEmpty()) return false;
        return metodosPago.stream().anyMatch(mp -> {
            if (mp == null || mp.getMetodoPagoId() == null) return false;
            try {
                com.pazzioliweb.metodospagomodule.entity.MetodosPago m =
                        metodosPagoRepository.findById(mp.getMetodoPagoId()).orElse(null);
                return m != null && m.getTipoNegociacion() != null
                        && "Credito".equalsIgnoreCase(m.getTipoNegociacion().name());
            } catch (Exception e) { return false; }
        });
    }

    private BigDecimal procesarMetodosPagoFinalizar(OrdenCompra orden,
                                                     List<FinalizarCompraDTO.MetodoPagoDTO> metodosPago) {
        BigDecimal totalPagado = BigDecimal.ZERO;
        if (metodosPago == null || metodosPago.isEmpty()) return totalPagado;
        List<OrdenCompraMetodoPago> persistidos = new java.util.ArrayList<>();
        for (FinalizarCompraDTO.MetodoPagoDTO mpDto : metodosPago) {
            if (mpDto.getMetodoPagoId() == null || mpDto.getMonto() == null) continue;
            if (mpDto.getMonto().compareTo(BigDecimal.ZERO) <= 0) continue;
            try {
                com.pazzioliweb.metodospagomodule.entity.MetodosPago metodo =
                        metodosPagoRepository.findById(mpDto.getMetodoPagoId()).orElse(null);
                if (metodo == null) continue;
                OrdenCompraMetodoPago mp = new OrdenCompraMetodoPago();
                mp.setOrdenCompra(orden);
                mp.setMetodoPago(metodo);
                mp.setMonto(mpDto.getMonto());
                mp.setReferencia(mpDto.getReferencia());
                persistidos.add(mp);
                boolean esCredito = metodo.getTipoNegociacion() != null
                        && "Credito".equalsIgnoreCase(metodo.getTipoNegociacion().name());
                if (!esCredito) totalPagado = totalPagado.add(mpDto.getMonto());
            } catch (Exception ex) {
                System.out.println("[FinalizarIngreso] Error método de pago: " + ex.getMessage());
            }
        }
        orden.setMetodosPago(persistidos);
        ordenCompraRepository.save(orden);
        return totalPagado;
    }

    private void generarAsientoCompraFinalizar(OrdenCompra orden, FinalizarCompraDTO dto,
                                                BigDecimal totalPagado, BigDecimal totalNeto,
                                                BigDecimal totalBruto, BigDecimal gravada,
                                                BigDecimal iva, BigDecimal rf, BigDecimal rv, BigDecimal ric) {
        List<AsientoContableService.LineaDTO> lineas = new java.util.ArrayList<>();

        BigDecimal baseInventario = totalBruto.subtract(iva).max(BigDecimal.ZERO);
        CuentaContable inv = configContable.inventarios().orElse(null);
        if (inv == null) { System.out.println("[AsientoFinalizar] Cuenta inventarios no configurada"); return; }
        lineas.add(AsientoContableService.LineaDTO.debito(inv.getId(),
                baseInventario.compareTo(BigDecimal.ZERO) > 0 ? baseInventario : totalBruto,
                "Compra " + orden.getNumeroOrden()));

        if (iva.compareTo(BigDecimal.ZERO) > 0) {
            CuentaContable ivaDesc = configContable.ivaDescontable().orElse(null);
            if (ivaDesc != null)
                lineas.add(AsientoContableService.LineaDTO.debito(ivaDesc.getId(), iva,
                        "IVA descontable " + orden.getNumeroOrden()));
        }

        // Créditos por métodos de pago de contado
        if (orden.getMetodosPago() != null) {
            for (OrdenCompraMetodoPago mp : orden.getMetodosPago()) {
                BigDecimal monto = mp.getMonto() != null ? mp.getMonto() : BigDecimal.ZERO;
                if (monto.compareTo(BigDecimal.ZERO) <= 0) continue;
                com.pazzioliweb.metodospagomodule.entity.MetodosPago metodo = mp.getMetodoPago();
                boolean esCredito = metodo != null && metodo.getTipoNegociacion() != null
                        && "Credito".equalsIgnoreCase(metodo.getTipoNegociacion().name());
                if (esCredito) continue;
                CuentaContable ctaMetodo = resolverCuentaMetodoPagoCompra(metodo);
                if (ctaMetodo == null) ctaMetodo = configContable.cajaGeneral().orElse(null);
                if (ctaMetodo == null) continue;
                lineas.add(AsientoContableService.LineaDTO.credito(ctaMetodo.getId(), monto,
                        "Pago " + (metodo != null ? metodo.getDescripcion() : "") + " " + orden.getNumeroOrden()));
            }
        }

        // Créditos retenciones — resuelve el tercero desde la orden si el DTO trae null o 0
        Integer tercId = (dto.getProveedorId() != null && dto.getProveedorId() > 0)
                ? dto.getProveedorId()
                : (orden.getProveedor() != null ? orden.getProveedor().getTerceroId() : null);
        String tercNom = orden.getProveedor() != null ? orden.getProveedor().getRazonSocial() : null;

        if (rf.compareTo(BigDecimal.ZERO) > 0) {
            CuentaContable cta = configContable.retefuentePagar().orElse(null);
            if (cta != null) {
                AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                        .credito(cta.getId(), rf, "ReteFuente compra " + orden.getNumeroOrden());
                if (tercId != null) l.conTercero(tercId, tercNom);
                lineas.add(l);
            }
        }
        if (rv.compareTo(BigDecimal.ZERO) > 0) {
            CuentaContable cta = configContable.reteivaPagar().orElse(null);
            if (cta != null) {
                AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                        .credito(cta.getId(), rv, "ReteIVA compra " + orden.getNumeroOrden());
                if (tercId != null) l.conTercero(tercId, tercNom);
                lineas.add(l);
            }
        }
        if (ric.compareTo(BigDecimal.ZERO) > 0) {
            CuentaContable cta = configContable.reteicaPagar().orElse(null);
            if (cta != null) {
                AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                        .credito(cta.getId(), ric, "ReteICA compra " + orden.getNumeroOrden());
                if (tercId != null) l.conTercero(tercId, tercNom);
                lineas.add(l);
            }
        }

        // Crédito CxP por el saldo
        BigDecimal saldoCxP = totalNeto.subtract(totalPagado);
        if (saldoCxP.compareTo(new BigDecimal("0.01")) > 0) {
            CuentaContable cxp = configContable.cxpProveedores().orElse(null);
            if (cxp == null) { System.out.println("[AsientoFinalizar] Cuenta CxP no configurada"); return; }
            AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                    .credito(cxp.getId(), saldoCxP, "CxP compra " + orden.getNumeroOrden());
            if (tercId != null) l.conTercero(tercId, tercNom);
            lineas.add(l);
        }

        String tipoDoc = orden.getComprobante() != null ? orden.getComprobante().getTipoMovimiento().name() : "CC";
        asientoService.generarAsiento(
                orden.getNumeroOrden(),
                orden.getFechaCreacion() != null ? orden.getFechaCreacion() : LocalDate.now(),
                "Compra " + orden.getNumeroOrden() + (orden.getProveedor() != null ? " - " + orden.getProveedor().getRazonSocial() : ""),
                tipoDoc, orden.getId(), orden.getComprobante(), lineas);
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // Actualizar orden PENDIENTE en su totalidad (sin reasignar comprobante)
    // ══════════════════════════════════════════════════════════════════════════════
    @Override
    @Transactional
    public OrdenCompraDTO actualizarCompleto(Long id, RealizarOrdenRequestDTO request) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new OrdenCompraException("Orden no encontrada: " + id));

        if (!"PENDIENTE".equals(orden.getEstado())) {
            throw new OrdenCompraException("Solo se pueden editar órdenes en estado PENDIENTE. Estado actual: " + orden.getEstado());
        }

        // 1. Actualizar productos del catálogo
        if (request.getOrden_compra() != null && request.getOrden_compra().getProducts() != null) {
            procesarProductosDesdeRequest(request.getOrden_compra().getProducts());
        }

        // 2. Actualizar campos de la orden (sin tocar comprobante ni numeroOrden)
        DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy");

        if (request.getFechainicial() != null && !request.getFechainicial().isBlank()) {
            try { orden.setFechaEmision(LocalDate.parse(request.getFechainicial(), formatter)); } catch (Exception ignore) {}
        }

        if (request.getFechafinal() != null && !request.getFechafinal().isBlank()) {
            try { orden.setFechaEntregaEsperada(LocalDate.parse(request.getFechafinal(), formatter)); } catch (Exception ignore) {}
        }

        if (request.getBodegaId() != null) {
            bodegasRepository.findByCodigo(request.getBodegaId()).ifPresent(orden::setBodega);
        }

        if (request.getOrden_compra() != null) {
            RealizarOrdenRequestDTO.OrdenCompraDataDTO oc = request.getOrden_compra();
            if (oc.getGravada() != null)           orden.setGravada(oc.getGravada());
            if (oc.getIva() != null)               orden.setIva(oc.getIva());
            if (oc.getDescuentos() != null)        orden.setDescuentos(oc.getDescuentos());
            if (oc.getTotalOrdenCompra() != null)  orden.setTotalOrdenCompra(oc.getTotalOrdenCompra());
            orden.setRetefuente(parseMoneyOrZero(oc.getRetefuente()));
            orden.setReteiva(parseMoneyOrZero(oc.getReteiva()));
            orden.setReteica(parseMoneyOrZero(oc.getReteica()));
        }

        // 3. Reemplazar detalles (items)
        orden.getItems().clear();
        if (request.getOrden_compra() != null && request.getOrden_compra().getProducts() != null) {
            List<DetalleOrdenCompra> nuevosDetalles = crearDetallesDesdeRequest(orden, request.getOrden_compra().getProducts());
            orden.getItems().addAll(nuevosDetalles);
        }

        // 4. Reemplazar métodos de pago
        if (orden.getMetodosPago() != null) orden.getMetodosPago().clear();
        if (request.getMetodosPago() != null && !request.getMetodosPago().isEmpty()) {
            List<OrdenCompraMetodoPago> nuevosPagos = new java.util.ArrayList<>();
            for (RealizarOrdenRequestDTO.MetodoPagoCompraDTO mpDto : request.getMetodosPago()) {
                if (mpDto.getMetodoPagoId() == null || mpDto.getMonto() == null) continue;
                if (mpDto.getMonto().compareTo(BigDecimal.ZERO) <= 0) continue;
                metodosPagoRepository.findById(mpDto.getMetodoPagoId()).ifPresent(metodo -> {
                    OrdenCompraMetodoPago mp = new OrdenCompraMetodoPago();
                    mp.setOrdenCompra(orden);
                    mp.setMetodoPago(metodo);
                    mp.setMonto(mpDto.getMonto());
                    mp.setReferencia(mpDto.getReferencia());
                    nuevosPagos.add(mp);
                });
            }
            if (orden.getMetodosPago() == null) {
                orden.setMetodosPago(nuevosPagos);
            } else {
                orden.getMetodosPago().addAll(nuevosPagos);
            }
        }

        OrdenCompra guardada = ordenCompraRepository.save(orden);
        return ordenCompraMapper.toDto(guardada);
    }
}
