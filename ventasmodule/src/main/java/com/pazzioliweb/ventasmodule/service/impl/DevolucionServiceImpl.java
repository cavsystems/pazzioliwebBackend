package com.pazzioliweb.ventasmodule.service.impl;

import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
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
            // Costo a reversar = COGS REAL con que salió la venta (persistido en DetalleVenta). Si la
            // venta es antigua y no lo tiene, se cae al costo promedio actual del producto. Se persiste
            // en el detalle de devolución y se reutiliza en kardex, reversa de COGS y anulación.
            BigDecimal costoDev = (detalleOriginal.getCostoUnitario() != null
                        && detalleOriginal.getCostoUnitario().compareTo(BigDecimal.ZERO) > 0)
                    ? detalleOriginal.getCostoUnitario()
                    : obtenerCostoVarianteParaDevolucion(detalleOriginal.getCodigoProducto(), detalleOriginal.getReferenciaVariantes());
            detalle.setCostoUnitario(costoDev != null ? costoDev : BigDecimal.ZERO);
            detalle.setMotivo(itemReq.getMotivo() != null ? itemReq.getMotivo() : request.getMotivo());
            detalles.add(detalle);

            totalDevuelto = totalDevuelto.add(totalLineaDevuelta);
            ivaDevuelto   = ivaDevuelto.add(ivaLineaDevuelta);

            // NOTA: las existencias NO se tocan aquí. El reingreso de stock lo hace de forma
            // ÚNICA el movimiento de inventario (registrarEntradaPorDevolucion), igual que en
            // compras/ventas. Antes se sumaba aquí (restaurarInventario) Y además el movimiento
            // sobrescribía existencias con setExistencia(saldoNuevo): en productos sin kardex el
            // saldo base era 0 y el SET borraba el stock previo. Un solo escritor evita esa doble
            // escritura y la pérdida de stock.
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

        // 9. Registrar movimiento en cajero para el cuadre de caja.
        //    Por caja/CxC solo se reembolsa el NETO (bruto − retenciones sufridas reversadas),
        //    exactamente lo que el asiento acredita en caja (montoContrapartida). Antes se pasaba
        //    el bruto (totalNeto con IVA, sin descontar retención) → el egreso de caja quedaba
        //    inflado respecto al asiento y descuadraba el arqueo en ventas con retención.
        BigDecimal montoReembolsoCaja = totalNeto.subtract(calcularRetencionesReversadas(venta, totalNeto));
        registrarMovimientoEnCajero(venta, guardada, montoReembolsoCaja);

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

    @Override
    @Transactional
    public DevolucionDTO anularDevolucion(Long devolucionId, String motivo, String usuario) {
        Devolucion dev = devolucionRepository.findById(devolucionId)
                .orElseThrow(() -> new VentaException("Devolución no encontrada: " + devolucionId));
        if ("ANULADA".equalsIgnoreCase(dev.getEstado()))
            throw new VentaException("La devolución ya está anulada.");
        if (motivo == null || motivo.isBlank())
            throw new VentaException("El motivo de anulación es obligatorio.");

        // Bloquear si el periodo contable del documento está cerrado.
        LocalDate fechaDoc = dev.getFechaCreacion() != null ? dev.getFechaCreacion() : LocalDate.now();
        periodoContableService.validarPeriodoAbierto(fechaDoc);

        Venta venta = dev.getVenta();

        // 1. Reversar los asientos contables de la devolución (principal + reversa de costo).
        //    Idempotente: si alguno no existe (p. ej. quedó fallido) no hace nada.
        asientoService.anularAsientoDeDocumento("DV", dev.getId());
        asientoService.anularAsientoDeDocumento("DV-COSTO", dev.getId());

        // 2. Revertir el reingreso de inventario: sacar de existencias lo que la devolución reingresó.
        revertirInventarioAnulacion(dev, venta);

        // 3. Revertir el movimiento de caja (solo la porción de contado que la devolución reembolsó).
        revertirCajaAnulacion(dev, venta);

        // 4. Marcar la devolución como ANULADA.
        dev.setEstado("ANULADA");
        dev.setMotivoAnulacion(motivo);
        dev.setFechaAnulacion(LocalDate.now());
        dev.setUsuarioAnulacion(usuario);
        devolucionRepository.save(dev);

        // 5. Recalcular el estado de la venta: al anular, sus ítems dejan de contar como devueltos
        //    (totalDevueltoPorDetalle solo cuenta devoluciones REGISTRADAS).
        if (venta != null) actualizarEstadoVenta(venta);

        return toDTO(dev);
    }

    /**
     * Revierte el reingreso de inventario que hizo la devolución: la devolución fue una ENTRADA
     * (DV), así que anularla registra una SALIDA real de kardex (documentoTipo "DV-ANUL"), para que
     * existencias, kardex y el mayor 1435 queden consistentes. Antes se restaba de existencias a
     * mano y el kardex quedaba con la entrada original (saldo inflado y divergente).
     */
    private void revertirInventarioAnulacion(Devolucion dev, Venta venta) {
        if (dev.getItems() == null || venta == null || venta.getBodega() == null) return;
        try {
            // Solo reversar si la ENTRADA original ("DV") realmente se registró; si falló en su
            // momento, no sacar stock (evitaría existencias negativas).
            if (!movimientoInventarioAutoService.existeMovimiento("DV", dev.getId())) return;
            List<MovimientoInventarioAutoService.ItemMovimiento> items = MovimientoInventarioAutoService.nuevaLista();
            for (DetalleDevolucion d : dev.getItems()) {
                if (d.getCantidadDevuelta() == null || d.getCantidadDevuelta() <= 0) continue;
                if (d.getDetalleVenta() == null) continue;
                String cod = d.getDetalleVenta().getCodigoProducto();
                // Mismo costo PERSISTIDO del registro (no recalcular con el costo actual del producto).
                double costo = d.getCostoUnitario() != null ? d.getCostoUnitario().doubleValue() : 0.0;
                items.add(new MovimientoInventarioAutoService.ItemMovimiento(
                        cod, d.getDetalleVenta().getReferenciaVariantes(), d.getCantidadDevuelta().doubleValue(), costo));
            }
            movimientoInventarioAutoService.registrarAjusteAnulacion(
                    "DV-ANUL", dev.getId(), venta.getBodega().getCodigo(), LocalDate.now(), items, false);
        } catch (Exception ex) {
            System.out.println("[AnularDevolucion] Reversa de inventario (no crítica): " + ex.getMessage());
        }
    }

    /**
     * Registra en la caja activa el movimiento inverso a la devolución (tipo ANULACION con montos
     * negados → suma de vuelta a la caja lo que la devolución había reembolsado). Solo la porción
     * de CONTADO (la parte a crédito nunca tocó la caja). Best-effort: si no hay sesión activa, se omite.
     */
    private void revertirCajaAnulacion(Devolucion dev, Venta venta) {
        DatosSesiones sesion = obtenerSesionActiva();
        if (sesion == null || sesion.getDetalleCajeroId() == null || venta == null) return;
        try {
            BigDecimal totalDevuelto = dev.getTotalNeto() != null ? dev.getTotalNeto() : BigDecimal.ZERO;
            BigDecimal montoEfectivo = BigDecimal.ZERO;
            BigDecimal montoElectronico = BigDecimal.ZERO;
            if (venta.getMetodosPago() != null && !venta.getMetodosPago().isEmpty()) {
                BigDecimal totalVenta = venta.getTotalVenta();
                BigDecimal totEf = BigDecimal.ZERO, totEl = BigDecimal.ZERO;
                for (VentaMetodoPago vmp : venta.getMetodosPago()) {
                    if (vmp.getMetodoPago().getTipoNegociacion() == MetodosPago.TipoNegociacion.Credito) continue;
                    String sigla = vmp.getMetodoPago().getSigla().toUpperCase();
                    if (sigla.startsWith("EF")) totEf = totEf.add(vmp.getMonto()); else totEl = totEl.add(vmp.getMonto());
                }
                if (totalVenta != null && totalVenta.compareTo(BigDecimal.ZERO) > 0) {
                    montoEfectivo   = totalDevuelto.multiply(totEf.divide(totalVenta, 10, java.math.RoundingMode.HALF_UP)).setScale(2, java.math.RoundingMode.HALF_UP);
                    montoElectronico = totalDevuelto.multiply(totEl.divide(totalVenta, 10, java.math.RoundingMode.HALF_UP)).setScale(2, java.math.RoundingMode.HALF_UP);
                } else if (totEf.add(totEl).compareTo(BigDecimal.ZERO) > 0) {
                    montoEfectivo = totalDevuelto;
                }
            } else {
                montoEfectivo = totalDevuelto;
            }
            BigDecimal cajaTotal = montoEfectivo.add(montoElectronico);
            if (cajaTotal.compareTo(BigDecimal.ZERO) <= 0) return; // venta a crédito: no hubo egreso de caja

            detalleCajeroService.registrarMovimiento(
                    sesion.getDetalleCajeroId(),
                    MovimientoCajero.TipoMovimiento.ANULACION,
                    dev.getNumeroDevolucion() + "-A",
                    dev.getId(),
                    cajaTotal.negate(),
                    BigDecimal.ZERO,
                    montoEfectivo.negate(),
                    montoElectronico.negate(),
                    "Anulación devolución " + dev.getNumeroDevolucion(),
                    dev.getComprobante()
            );
        } catch (Exception e) {
            System.out.println("[AnularDevolucion] Error revirtiendo caja: " + e.getMessage());
        }
    }

    /**
     * Registra un MovimientoInventario tipo ENTRADA por la devolución (la mercancía
     * vuelve a la bodega de origen), genera Kardex y actualiza existencias. Es el ÚNICO
     * punto que reingresa el stock de la devolución (fuente de verdad única, como en
     * compras/ventas). El try/catch registra el error sin abortar la devolución para no
     * dejar huérfano el asiento contable (REQUIRES_NEW ya confirmado); un fallo aquí queda
     * logueado para reproceso manual del movimiento de inventario.
     */
    private void generarMovimientoInventarioDevolucion(Devolucion devolucion, Venta venta) {
        try {
            List<MovimientoInventarioAutoService.ItemMovimiento> items =
                    MovimientoInventarioAutoService.nuevaLista();
            for (DetalleDevolucion d : devolucion.getItems()) {
                if (d.getCantidadDevuelta() == null || d.getCantidadDevuelta() <= 0) continue;
                // Costo PERSISTIDO en el registro (mismo valor para kardex, COGS y anulación).
                double costo = d.getCostoUnitario() != null ? d.getCostoUnitario().doubleValue() : 0.0;
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
            Integer comprobanteId = null;
            Integer consecutivo = null;
            if (devolucion.getComprobante() != null) {
                comprobanteId = devolucion.getComprobante().getId().intValue();
                consecutivo = devolucion.getConsecutivoComprobante();
            }
            movimientoInventarioAutoService.registrarEntradaPorDevolucion(
                    devolucion.getNumeroDevolucion(),
                    devolucion.getId(),
                    venta.getBodega().getCodigo(),
                    devolucion.getFechaCreacion() != null ? devolucion.getFechaCreacion() : LocalDate.now(),
                    items,
                    comprobanteId,
                    consecutivo
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
            // DR Devoluciones por la BASE sin IVA. Si la base es 0 (caso borde: línea 100% IVA),
            // no se agrega la línea: el IVA revertido y la contrapartida ya cuadran. Antes se usaba
            // totalDevuelto (que incluye IVA) y además se debitaba el IVA aparte → doble débito.
            if (subtotal.compareTo(BigDecimal.ZERO) > 0) {
                lineas.add(com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO.debito(
                        devVent.getId(),
                        subtotal,
                        "Devolución venta " + venta.getNumeroVenta()));
            }

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
            // Proporción de lo devuelto sobre el BRUTO de la venta. Si el bruto no es válido
            // (null/≤0), la proporción es 0: NO se reversan retenciones (antes el fallback usaba
            // totalDevuelto como base → proporción 1 → reversaba el 100% aun en devolución parcial).
            BigDecimal retefuenteRev = retencionReversada(venta.getTotalVenta(), venta.getRetefuente(), totalDevuelto);
            BigDecimal reteivaRev = retencionReversada(venta.getTotalVenta(), venta.getReteiva(), totalDevuelto);
            BigDecimal reteicaRev = retencionReversada(venta.getTotalVenta(), venta.getReteica(), totalDevuelto);
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
                // Mismo costo PERSISTIDO que usa el reingreso de kardex → 1435 = kardex.
                BigDecimal costoUnit = d.getCostoUnitario();
                if (costoUnit == null || costoUnit.compareTo(BigDecimal.ZERO) <= 0) continue;
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
     * Actualiza el estado de la venta según cuántos ítems han sido devueltos totalmente.
     * - Si todos los ítems de la venta fueron devueltos en su totalidad → DEVUELTA
     * - Si solo algunos ítems o parcialmente → DEVOLUCION_PARCIAL
     */
    private void actualizarEstadoVenta(Venta venta) {
        // totalDevueltoPorDetalle solo suma devoluciones VIGENTES (estado REGISTRADA), por lo que al
        // anular una devolución el conteo baja. Recalculamos el estado sobre lo vigente:
        //  - 0 devuelto  → COMPLETADA (se restaura; p.ej. tras anular la única devolución)
        //  - todo devuelto → DEVUELTA
        //  - algo devuelto → DEVOLUCION_PARCIAL
        int totalVigente = 0;
        boolean todosDevueltos = true;
        for (var detalle : venta.getItems()) {
            Integer devuelto = devolucionRepository.totalDevueltoPorDetalle(detalle.getId());
            int d = devuelto != null ? devuelto : 0;
            totalVigente += d;
            if (d < detalle.getCantidad()) {
                todosDevueltos = false;
            }
        }

        if (totalVigente == 0) {
            venta.setEstado("COMPLETADA");
        } else {
            venta.setEstado(todosDevueltos ? "DEVUELTA" : "DEVOLUCION_PARCIAL");
        }
        ventaRepository.save(venta);
    }

    /**
     * Retención sufrida reversada por una devolución: retencionTotal × (devuelto / brutoVenta),
     * redondeada a 2 decimales. Si el bruto de la venta no es válido (null/≤0) la proporción es 0
     * (no se reversa nada). Se usa tanto en el asiento como en el cálculo del reembolso de caja
     * para que ambos usen exactamente el mismo neto.
     */
    private BigDecimal retencionReversada(BigDecimal brutoVenta, BigDecimal retencionTotal, BigDecimal totalDevuelto) {
        if (brutoVenta == null || brutoVenta.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        BigDecimal ret = retencionTotal != null ? retencionTotal : BigDecimal.ZERO;
        BigDecimal proporcion = totalDevuelto.divide(brutoVenta, 6, java.math.RoundingMode.HALF_UP);
        return ret.multiply(proporcion).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /** Suma de las tres retenciones sufridas reversadas (retefuente + reteiva + reteica). */
    private BigDecimal calcularRetencionesReversadas(Venta venta, BigDecimal totalDevuelto) {
        return retencionReversada(venta.getTotalVenta(), venta.getRetefuente(), totalDevuelto)
                .add(retencionReversada(venta.getTotalVenta(), venta.getReteiva(), totalDevuelto))
                .add(retencionReversada(venta.getTotalVenta(), venta.getReteica(), totalDevuelto));
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
            // Solo la porción pagada de CONTADO (efectivo/electrónico) salió/entró por caja; la
            // parte a CRÉDITO nunca tocó la caja (se reversa vía CxC en el asiento). Antes se
            // repartía TODO el monto devuelto entre efectivo/electrónico —incluida la parte a
            // crédito, bucketeada como "electrónico"— generando un egreso de caja fantasma en las
            // devoluciones de ventas a crédito.
            BigDecimal montoEfectivoDevuelto   = BigDecimal.ZERO;
            BigDecimal montoElectronicoDevuelto = BigDecimal.ZERO;

            if (venta.getMetodosPago() != null && !venta.getMetodosPago().isEmpty()) {
                BigDecimal totalVenta = venta.getTotalVenta();
                BigDecimal totalEfectivo = BigDecimal.ZERO;
                BigDecimal totalElectronico = BigDecimal.ZERO;

                for (VentaMetodoPago vmp : venta.getMetodosPago()) {
                    // Excluir los métodos a crédito: no ingresaron a caja.
                    if (vmp.getMetodoPago().getTipoNegociacion() == MetodosPago.TipoNegociacion.Credito) continue;
                    String sigla = vmp.getMetodoPago().getSigla().toUpperCase();
                    if (sigla.startsWith("EF")) {
                        totalEfectivo = totalEfectivo.add(vmp.getMonto());
                    } else {
                        totalElectronico = totalElectronico.add(vmp.getMonto());
                    }
                }

                if (totalVenta != null && totalVenta.compareTo(BigDecimal.ZERO) > 0) {
                    // Proporción sobre el total de la venta: refunda por caja solo la fracción de
                    // contado (para ventas mixtas devuelve la parte contado; para 100% crédito, 0).
                    BigDecimal propEfec = totalEfectivo.divide(totalVenta, 10, java.math.RoundingMode.HALF_UP);
                    BigDecimal propElec = totalElectronico.divide(totalVenta, 10, java.math.RoundingMode.HALF_UP);
                    montoEfectivoDevuelto   = totalDevuelto.multiply(propEfec).setScale(2, java.math.RoundingMode.HALF_UP);
                    montoElectronicoDevuelto = totalDevuelto.multiply(propElec).setScale(2, java.math.RoundingMode.HALF_UP);
                } else if (totalEfectivo.add(totalElectronico).compareTo(BigDecimal.ZERO) > 0) {
                    // Sin total de venta válido pero con pagos de contado: todo a efectivo.
                    montoEfectivoDevuelto = totalDevuelto;
                }
                // Si no hubo pagos de contado (venta 100% crédito), ambos quedan en 0.
            } else {
                // Sin métodos de pago registrados: se asume contado (efectivo).
                montoEfectivoDevuelto = totalDevuelto;
            }

            BigDecimal cajaDevuelta = montoEfectivoDevuelto.add(montoElectronicoDevuelto);
            if (cajaDevuelta.compareTo(BigDecimal.ZERO) <= 0) {
                // Venta a crédito: no hay egreso de caja (la devolución se refleja en la CxC).
                return;
            }

            detalleCajeroService.registrarMovimiento(
                    sesion.getDetalleCajeroId(),
                    MovimientoCajero.TipoMovimiento.DEVOLUCION,
                    devolucion.getNumeroDevolucion(),
                    devolucion.getId(),
                    cajaDevuelta,
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
        Venta v = dev.getVenta();
        dto.setVentaId(v != null ? v.getId() : null);
        dto.setNumeroVenta(v != null ? v.getNumeroVenta() : null);
        // Null-safe: la venta puede no tener cliente (venta a consumidor final sin tercero).
        dto.setNombreCliente(v != null && v.getCliente() != null ? v.getCliente().getRazonSocial() : null);
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




