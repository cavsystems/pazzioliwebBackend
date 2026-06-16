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
import com.pazzioliweb.movimientosinventariomodule.service.MovimientoInventarioAutoService;
import com.pazzioliweb.commonbacken.events.DevolucionRegistradaEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    private final com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService asignacionComprobante;
    @Autowired
    private com.pazzioliweb.comprobantesmodule.service.AsientoContableService asientoService;
    @Autowired
    private com.pazzioliweb.comprobantesmodule.service.ConfiguracionContableService configContable;
    @Autowired
    private com.pazzioliweb.comprobantesmodule.service.AsientoFallidoService asientoFallidoService;
    @Autowired
    private com.pazzioliweb.comprobantesmodule.service.PeriodoContableService periodoContableService;
  @Autowired
  private PedidoService pedidoService;
@Autowired
 private  PedidoRepository pedidoRepository;
    @Autowired
    private MovimientoInventarioAutoService movimientoInventarioAutoService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private com.pazzioliweb.tercerosmodule.repositori.TercerosRepository tercerosRepository;
    @Autowired
    public DevolucionServiceImpl(
            VentaRepository ventaRepository,
            DevolucionRepository devolucionRepository,
            ExistenciasRepository existenciasRepository,
            ProductoVarianteRepository productoVarianteRepository,
            DetalleCajeroService detalleCajeroService,
            CajeroRepository cajeroRepository,
            RedisTemplate<String, DatosSesiones> redisTemplate,
            com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService asignacionComprobante) {
        this.ventaRepository = ventaRepository;
        this.devolucionRepository = devolucionRepository;
        this.existenciasRepository = existenciasRepository;
        this.productoVarianteRepository = productoVarianteRepository;
        this.detalleCajeroService = detalleCajeroService;
        this.cajeroRepository = cajeroRepository;
        this.redisTemplate = redisTemplate;
        this.asignacionComprobante = asignacionComprobante;
    }

    @Override
    @Transactional
    public DevolucionDTO registrarDevolucion(DevolucionRequestDTO request) {

        // 0. Validar periodo contable abierto antes de cualquier movimiento.
        periodoContableService.validarPeriodoAbierto(LocalDate.now());

        // 1. Obtener la venta
        Venta venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> new VentaException("Venta no encontrada: " + request.getVentaId()));

        // 2. Solo se pueden devolver ventas COMPLETADAS o DEVOLUCION_PARCIAL
        if (!"COMPLETADA".equals(venta.getEstado()) && !"DEVOLUCION_PARCIAL".equals(venta.getEstado())) {
            throw new VentaException("Solo se pueden registrar devoluciones en ventas COMPLETADAS. Estado actual: " + venta.getEstado());
        }

        // 3. Resolver cajero (sesión activa)
        DatosSesiones sesionActual = obtenerSesionActiva();
        Integer cajeroId = null;
        if (sesionActual != null && sesionActual.getCajeroId() != null) {
            cajeroId = sesionActual.getCajeroId();
        }

        // 4. Crear encabezado de devolución
        Devolucion devolucion = new Devolucion();
        devolucion.setVenta(venta);
        devolucion.setMotivo(request.getMotivo());
        devolucion.setObservaciones(request.getObservaciones());
        devolucion.setEstado("REGISTRADA");
        devolucion.setUsuarioCreacion(request.getUsuarioCreacion());
        devolucion.setFechaCreacion(LocalDate.now());

        if (cajeroId != null) {
            cajeroRepository.findById(cajeroId).ifPresent(devolucion::setCajero);
        }

        // 5. Asignar comprobante (DV) usando el prefijo del cajero
        if (cajeroId == null) {
            throw new VentaException("Se requiere una sesión de cajero activa para registrar la devolución.");
        }
        try {
            com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService.Resultado r =
                    asignacionComprobante.asignar(cajeroId,
                            com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante.DV);
            devolucion.setComprobante(r.getComprobante());
            devolucion.setConsecutivoComprobante(r.getConsecutivo());
            devolucion.setNumeroDevolucion(r.getNumeroDocumento());
        } catch (com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService.ComprobanteNoConfiguradoException ex) {
            throw new VentaException(ex.getMessage());
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

        // Actualizar último movimiento del cliente de la venta
        try {
            if (venta.getCliente() != null) {
                tercerosRepository.actualizarUltimoMovimiento(
                        venta.getCliente().getTerceroId(), java.time.LocalDateTime.now());
            }
        } catch (Exception ex) {
            System.out.println("[UltimoMovimiento] Error actualizando cliente devolución: " + ex.getMessage());
        }

        // 8. Actualizar estado de la venta
        actualizarEstadoVenta(venta);

        // 9. Registrar movimiento en cajero para el cuadre de caja
        registrarMovimientoEnCajero(venta, guardada, totalNeto);

        // 10. Generar asiento contable de devolución (reversa de venta)
        generarAsientoDevolucion(guardada, venta, totalNeto);

        // 11. Registrar movimiento de inventario ENTRADA + Kardex (trazabilidad)
        generarMovimientoInventarioDevolucion(guardada, venta);

        // 12. Publicar evento → facturacionmodule generará la Nota Crédito Electrónica DIAN
        try {
            Integer cajeroEvtId = guardada.getCajero() != null ? guardada.getCajero().getCajeroId() : null;
            // Concepto DIAN: 1=Devolución, 2=Anulación, 3=Rebaja, 4=Descuento, 5=Otro
            Integer concepto = 1;
            eventPublisher.publishEvent(new DevolucionRegistradaEvent(
                    this, guardada.getId(), venta.getId(), cajeroEvtId, concepto));
        } catch (Exception ex) {
            System.out.println("[Devolucion] Error publicando evento NC (no crítico): " + ex.getMessage());
        }

        return toDTO(guardada);
    }

    /**
     * Registra un MovimientoInventario tipo ENTRADA por la devolución (la mercancía
     * vuelve a la bodega de origen). Genera Kardex para trazabilidad.
     * Try/catch defensivo: si falla, no rompe la devolución (las existencias ya
     * fueron restauradas vía restaurarInventario).
     */
    private void generarMovimientoInventarioDevolucion(Devolucion devolucion, Venta venta) {
        try {
            List<MovimientoInventarioAutoService.ItemMovimiento> items =
                    MovimientoInventarioAutoService.nuevaLista();
            for (DetalleDevolucion d : devolucion.getItems()) {
                if (d.getCantidadDevuelta() == null || d.getCantidadDevuelta() <= 0) continue;
                double costo = d.getPrecioUnitario() != null ? d.getPrecioUnitario().doubleValue() : 0.0;
                items.add(new MovimientoInventarioAutoService.ItemMovimiento(
                        d.getDetalleVenta().getCodigoProducto(),
                        d.getDetalleVenta().getReferenciaVariantes(),
                        d.getCantidadDevuelta().doubleValue(),
                        costo
                ));
            }
            if (items.isEmpty()) {
                System.out.println("[MovInv-Devolucion] Sin items con cantidad devuelta > 0 para devolución "
                        + devolucion.getNumeroDevolucion());
                return;
            }
            movimientoInventarioAutoService.registrarEntradaPorDevolucion(
                    devolucion.getNumeroDevolucion(),
                    devolucion.getId(),
                    venta.getBodega().getCodigo(),
                    devolucion.getFechaCreacion() != null ? devolucion.getFechaCreacion() : LocalDate.now(),
                    items
            );
        } catch (Exception ex) {
            System.out.println("[MovInv-Devolucion] Error generando movimiento (no crítico): " + ex.getMessage());
        }
    }

    /**
     * Asiento de devolución (DV) — reverso parcial/total de la venta original.
     * Lógica de contrapartida (CRÉDITO):
     *   - Venta CONTADO (FC) → CR Caja general (reembolso real al cliente)
     *   - Venta CRÉDITO (VC) → CR CxC Clientes (revierte la deuda del cliente)
     * Lógica del DÉBITO:
     *   DR Devolución en ventas (4175) por subtotal devuelto sin IVA
     *   DR IVA generado (240801) [reversa] por IVA devuelto
     */
    private void generarAsientoDevolucion(Devolucion devolucion, Venta venta, BigDecimal totalDevuelto) {
        try {
            java.util.List<com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO> lineas = new java.util.ArrayList<>();

            BigDecimal iva = devolucion.getIvaDevuelto() != null ? devolucion.getIvaDevuelto() : BigDecimal.ZERO;
            BigDecimal subtotal = totalDevuelto.subtract(iva).max(BigDecimal.ZERO);

            // DÉBITO Devolución en ventas
            com.pazzioliweb.comprobantesmodule.entity.CuentaContable devVent = configContable.devolucionVentas().orElse(null);
            if (devVent == null) {
                System.out.println("[AsientoDevolucion] Cuenta 4175 Devoluciones no configurada. Asiento omitido.");
                return;
            }
            lineas.add(com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO.debito(
                    devVent.getId(),
                    subtotal.compareTo(BigDecimal.ZERO) > 0 ? subtotal : totalDevuelto,
                    "Devolución venta " + venta.getNumeroVenta()));

            // DÉBITO IVA generado (revierte)
            if (iva.compareTo(BigDecimal.ZERO) > 0) {
                com.pazzioliweb.comprobantesmodule.entity.CuentaContable ivaGen = configContable.ivaGenerado().orElse(null);
                if (ivaGen != null) {
                    lineas.add(com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO.debito(
                            ivaGen.getId(), iva, "Reversa IVA por devolución"));
                }
            }

            // ─── Reversa de RETENCIONES sufridas (proporcional a lo devuelto) ───
            // En la venta, las retenciones que el cliente nos practicó se debitaron a
            // anticipos (135515 Retefuente / 135517 ReteIVA / 135518 ReteICA) y el
            // cliente pagó solo el NETO. Al devolver se reversan (crédito) en proporción
            // a lo devuelto; así la caja/CxC refleja únicamente el NETO realmente
            // reembolsado y el anticipo no queda colgado en los libros.
            BigDecimal totalVentaBruto = (venta.getTotalVenta() != null && venta.getTotalVenta().compareTo(BigDecimal.ZERO) > 0)
                    ? venta.getTotalVenta() : totalDevuelto;
            BigDecimal proporcion = totalDevuelto.divide(totalVentaBruto, 6, java.math.RoundingMode.HALF_UP);
            BigDecimal retefuenteRev = (venta.getRetefuente() != null ? venta.getRetefuente() : BigDecimal.ZERO)
                    .multiply(proporcion).setScale(2, java.math.RoundingMode.HALF_UP);
            BigDecimal reteivaRev = (venta.getReteiva() != null ? venta.getReteiva() : BigDecimal.ZERO)
                    .multiply(proporcion).setScale(2, java.math.RoundingMode.HALF_UP);
            BigDecimal reteicaRev = (venta.getReteica() != null ? venta.getReteica() : BigDecimal.ZERO)
                    .multiply(proporcion).setScale(2, java.math.RoundingMode.HALF_UP);
            String nombreTerceroRet = venta.getCliente() != null
                    ? (venta.getCliente().getRazonSocial() != null && !venta.getCliente().getRazonSocial().isBlank()
                        ? venta.getCliente().getRazonSocial() : venta.getCliente().getNombre1())
                    : null;
            if (retefuenteRev.compareTo(BigDecimal.ZERO) > 0) {
                com.pazzioliweb.comprobantesmodule.entity.CuentaContable cta = configContable.anticipoRetefuente().orElse(null);
                if (cta != null) {
                    com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO l =
                            com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO.credito(
                                    cta.getId(), retefuenteRev, "Reversa Retefuente por devolución " + devolucion.getNumeroDevolucion());
                    if (venta.getCliente() != null) l.conTercero(venta.getCliente().getTerceroId(), nombreTerceroRet);
                    lineas.add(l);
                }
            }
            if (reteivaRev.compareTo(BigDecimal.ZERO) > 0) {
                com.pazzioliweb.comprobantesmodule.entity.CuentaContable cta = configContable.anticipoReteiva().orElse(null);
                if (cta != null) {
                    com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO l =
                            com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO.credito(
                                    cta.getId(), reteivaRev, "Reversa ReteIVA por devolución " + devolucion.getNumeroDevolucion());
                    if (venta.getCliente() != null) l.conTercero(venta.getCliente().getTerceroId(), nombreTerceroRet);
                    lineas.add(l);
                }
            }
            if (reteicaRev.compareTo(BigDecimal.ZERO) > 0) {
                com.pazzioliweb.comprobantesmodule.entity.CuentaContable cta = configContable.anticipoReteica().orElse(null);
                if (cta != null) {
                    com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO l =
                            com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO.credito(
                                    cta.getId(), reteicaRev, "Reversa ReteICA por devolución " + devolucion.getNumeroDevolucion());
                    if (venta.getCliente() != null) l.conTercero(venta.getCliente().getTerceroId(), nombreTerceroRet);
                    lineas.add(l);
                }
            }
            // Lo que realmente se reembolsa (caja) o se reversa de CxC = bruto − retenciones reversadas
            BigDecimal montoContrapartida = totalDevuelto.subtract(retefuenteRev).subtract(reteivaRev).subtract(reteicaRev);

            // CRÉDITO: contrapartida según si la venta fue contado o crédito
            boolean ventaFueCredito = venta.getComprobante() != null
                    && venta.getComprobante().getTipoMovimiento() != null
                    && "VC".equals(venta.getComprobante().getTipoMovimiento().name());

            com.pazzioliweb.comprobantesmodule.entity.CuentaContable contrapartida;
            String descContrapartida;
            if (ventaFueCredito) {
                // Venta a crédito → revertir CxC del cliente (no toca caja)
                contrapartida = configContable.cxcClientes().orElse(null);
                descContrapartida = "Reversa CxC por devolución " + devolucion.getNumeroDevolucion();
                if (contrapartida == null) {
                    System.out.println("[AsientoDevolucion] Cuenta 1305 CxC no configurada. Asiento omitido.");
                    return;
                }
            } else {
                // Venta contado → reembolso real de caja
                contrapartida = configContable.cajaGeneral().orElse(null);
                descContrapartida = "Reembolso devolución " + devolucion.getNumeroDevolucion();
                if (contrapartida == null) {
                    System.out.println("[AsientoDevolucion] Cuenta 1105 Caja no configurada. Asiento omitido.");
                    return;
                }
            }
            com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO creditoLinea =
                    com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO.credito(
                            contrapartida.getId(), montoContrapartida, descContrapartida);
            if (venta.getCliente() != null) {
                creditoLinea.conTercero(venta.getCliente().getTerceroId(),
                        venta.getCliente().getRazonSocial() != null && !venta.getCliente().getRazonSocial().isBlank()
                            ? venta.getCliente().getRazonSocial()
                            : venta.getCliente().getNombre1());
            }
            lineas.add(creditoLinea);

            asientoService.generarAsiento(
                    devolucion.getNumeroDevolucion(),
                    devolucion.getFechaCreacion() != null ? devolucion.getFechaCreacion() : LocalDate.now(),
                    "Devolución " + devolucion.getNumeroDevolucion() + " de venta " + venta.getNumeroVenta(),
                    "DV",
                    devolucion.getId(),
                    devolucion.getComprobante(),
                    lineas
            );

            // ─── Reversa de Costo de Ventas: DR Inventarios / CR Costo Ventas ───
            // Los productos vuelven al stock, así que el COGS y el inventario
            // deben revertirse en proporción a la cantidad devuelta.
            generarAsientoReversaCostoDevolucion(devolucion, venta);
        } catch (Exception ex) {
            System.out.println("[AsientoDevolucion] Error generando asiento (no crítico): " + ex.getMessage());
            asientoFallidoService.registrar("DEVOLUCIONES", "DV",
                    devolucion.getId(), devolucion.getNumeroDevolucion(),
                    "Error generando asiento de devolución: " + ex.getMessage(), ex);
        }
    }

    /**
     * Reversa del Costo de Ventas para la devolución:
     *   DR Inventarios (1435)        — los productos vuelven al stock
     *   CR Costo de Ventas (6135)    — se revierte el COGS por la cantidad devuelta
     * El monto se calcula con el costo promedio actual del producto/variante
     * multiplicado por la cantidad devuelta.
     */
    private void generarAsientoReversaCostoDevolucion(Devolucion devolucion, Venta venta) {
        try {
            BigDecimal totalCosto = BigDecimal.ZERO;
            for (DetalleDevolucion d : devolucion.getItems()) {
                if (d.getCantidadDevuelta() == null || d.getCantidadDevuelta() <= 0) continue;
                if (d.getDetalleVenta() == null) continue;
                BigDecimal costoUnit = obtenerCostoVarianteParaDevolucion(
                        d.getDetalleVenta().getCodigoProducto(),
                        d.getDetalleVenta().getReferenciaVariantes());
                if (costoUnit == null) continue;
                totalCosto = totalCosto.add(costoUnit.multiply(BigDecimal.valueOf(d.getCantidadDevuelta())));
            }
            if (totalCosto.compareTo(BigDecimal.ZERO) <= 0) return;

            com.pazzioliweb.comprobantesmodule.entity.CuentaContable costo = configContable.costoVentas().orElse(null);
            com.pazzioliweb.comprobantesmodule.entity.CuentaContable inv   = configContable.inventarios().orElse(null);
            if (costo == null || inv == null) {
                System.out.println("[AsientoReversaCostoDevolucion] 6135 o 1435 no configuradas, se omite.");
                return;
            }

            java.util.List<com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO> lineas = new java.util.ArrayList<>();
            lineas.add(com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO.debito(
                    inv.getId(), totalCosto,
                    "Reingreso inventario por devolución " + devolucion.getNumeroDevolucion()));
            lineas.add(com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO.credito(
                    costo.getId(), totalCosto,
                    "Reversa costo de ventas por devolución " + devolucion.getNumeroDevolucion()));

            // Sufijo "-COSTO" en el tipo para no chocar con la idempotencia
            // del asiento principal de la devolución.
            asientoService.generarAsiento(
                    "COSTO-DV-" + devolucion.getNumeroDevolucion(),
                    devolucion.getFechaCreacion() != null ? devolucion.getFechaCreacion() : LocalDate.now(),
                    "Reversa costo devolución " + devolucion.getNumeroDevolucion(),
                    "DV-COSTO",
                    devolucion.getId(),
                    devolucion.getComprobante(),
                    lineas
            );
        } catch (Exception ex) {
            System.out.println("[AsientoReversaCostoDevolucion] Error generando asiento (no crítico): " + ex.getMessage());
            asientoFallidoService.registrar("DEVOLUCIONES-COSTO", "DV-COSTO",
                    devolucion.getId(), devolucion.getNumeroDevolucion(),
                    "Error generando reversa de COGS: " + ex.getMessage(), ex);
        }
    }

    /** Resuelve el costo promedio del producto/variante para la reversa de COGS. */
    private BigDecimal obtenerCostoVarianteParaDevolucion(String codigoProducto, String referenciaVariantes) {
        try {
            java.util.Optional<ProductoVariante> opt = productoVarianteRepository.findBySku(codigoProducto);
            if (opt.isEmpty() && referenciaVariantes != null) {
                opt = productoVarianteRepository.findBySku(referenciaVariantes);
            }
            if (opt.isPresent() && opt.get().getProducto() != null) {
                Double c = opt.get().getProducto().getCosto();
                if (c != null && c > 0) return BigDecimal.valueOf(c);
            }
        } catch (Exception ignored) { }
        return null;
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
                            " de venta " + venta.getNumeroVenta(),
                    devolucion.getComprobante()
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

        // Datos Nota Crédito Electrónica
        dto.setNumeroNc(dev.getNumeroNc());
        dto.setCufeNc(dev.getCufeNc());
        dto.setEstadoDianNc(dev.getEstadoDianNc());
        dto.setMensajeDianNc(dev.getMensajeDianNc());
        dto.setQrDataNc(dev.getQrDataNc());

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




