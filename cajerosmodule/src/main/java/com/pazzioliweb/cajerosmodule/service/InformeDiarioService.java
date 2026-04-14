package com.pazzioliweb.cajerosmodule.service;

import com.pazzioliweb.cajerosmodule.dtos.InformeDiarioVentasDTO;
import com.pazzioliweb.cajerosmodule.dtos.InformeDiarioVentasDTO.*;
import com.pazzioliweb.cajerosmodule.entity.DetalleCajero;
import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;
import com.pazzioliweb.cajerosmodule.repositori.DetalleCajeroRepository;
import com.pazzioliweb.cajerosmodule.repositori.InformeDiarioRepository;
import com.pazzioliweb.cajerosmodule.repositori.MovimientoCajeroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que construye el Informe Diario de Ventas (Reporte Z / Cuadre de Caja).
 *
 * Sin dependencia circular: usa SQL nativo (InformeDiarioRepository)
 * para consultar tablas de ventas sin importar entidades de ventasmodule.
 *
 * Secciones del reporte impreso:
 *   1. Encabezado (cajero, fecha, hora, transacción inicial/final, # transacciones, Z)
 *   2. Movimiento de Cuentas
 *   3. Ventas por Línea
 *   4. Formas de Pago
 *   5. Recibos de Caja  (tipo ABONO en MovimientoCajero)
 *   6. Comprobantes de Egreso  (tipo EGRESO en MovimientoCajero)
 *   7. Vales
 *   8. Devoluciones
 *   9. Resumen Final (Neto Caja, UPT, VPT, VPU)
 */
@Service
public class InformeDiarioService {

    private static final ZoneId            ZONA_BOGOTA = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter HORA_FMT    = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final DetalleCajeroRepository    detalleCajeroRepo;
    private final MovimientoCajeroRepository movimientoRepo;
    private final InformeDiarioRepository    informeDiarioRepo;

    @Autowired
    public InformeDiarioService(DetalleCajeroRepository detalleCajeroRepo,
                                MovimientoCajeroRepository movimientoRepo,
                                InformeDiarioRepository informeDiarioRepo) {
        this.detalleCajeroRepo  = detalleCajeroRepo;
        this.movimientoRepo     = movimientoRepo;
        this.informeDiarioRepo  = informeDiarioRepo;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  MÉTODO PRINCIPAL
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Genera el Informe Diario de Ventas para una sesión y un día específico.
     *
     * @param detalleCajeroId ID de la sesión de caja
     * @param fecha           Día del informe (si es null → hoy)
     */
    @Transactional(readOnly = true)
    public InformeDiarioVentasDTO generarInforme(Long detalleCajeroId, LocalDate fecha) {

        DetalleCajero sesion = detalleCajeroRepo.findById(detalleCajeroId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada: " + detalleCajeroId));

        // Fecha del informe: parámetro recibido, o la fecha de hoy
        LocalDate fechaInforme = (fecha != null) ? fecha : LocalDate.now(ZONA_BOGOTA);

        return construirInforme(sesion, fechaInforme);
    }

    /**
     * Genera el Informe Diario buscando automáticamente la sesión correcta
     * a partir del cajeroId y la fecha.
     *
     * Ideal para consultar informes de días anteriores sin conocer el detalleCajeroId.
     *
     * @param cajeroId ID del cajero
     * @param fecha    Día del informe
     */
    @Transactional(readOnly = true)
    public InformeDiarioVentasDTO generarInformePorCajero(Integer cajeroId, LocalDate fecha) {

        DetalleCajero sesion = detalleCajeroRepo.findByCajeroIdAndFechaMovimiento(cajeroId, fecha)
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró una sesión del cajero " + cajeroId
                      + " con movimientos en la fecha " + fecha));

        return construirInforme(sesion, fecha);
    }

    /**
     * Lógica común para construir el informe a partir de una sesión y una fecha.
     */
    private InformeDiarioVentasDTO construirInforme(DetalleCajero sesion, LocalDate fechaInforme) {

        Long detalleCajeroId = sesion.getDetalleCajeroId();

        // Cargar todos los movimientos de la sesión y filtrar solo los del día solicitado
        List<MovimientoCajero> todosMovimientos =
                movimientoRepo.findByDetalleCajero_DetalleCajeroIdOrderByConsecutivoAsc(detalleCajeroId);

        List<MovimientoCajero> movimientosDia = todosMovimientos.stream()
                .filter(m -> m.getFechaMovimiento() != null
                          && m.getFechaMovimiento().toLocalDate().equals(fechaInforme))
                .collect(Collectors.toList());

        InformeDiarioVentasDTO dto = new InformeDiarioVentasDTO();

        buildEncabezado(dto, sesion, movimientosDia, fechaInforme);
        buildMovimientoCuentas(dto, detalleCajeroId, fechaInforme);
        buildVentasPorLinea(dto, detalleCajeroId, fechaInforme);
        buildFormasDePago(dto, detalleCajeroId, fechaInforme);
        buildRecibosCaja(dto, movimientosDia);
        buildEgresos(dto, movimientosDia);
        buildVales(dto, movimientosDia);
        buildDevoluciones(dto, detalleCajeroId, fechaInforme, movimientosDia);
        buildResumenFinal(dto, detalleCajeroId, fechaInforme, movimientosDia);

        return dto;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  1. ENCABEZADO
    // ════════════════════════════════════════════════════════════════════════
    private void buildEncabezado(InformeDiarioVentasDTO dto,
                                  DetalleCajero sesion,
                                  List<MovimientoCajero> movimientos,
                                  LocalDate fecha) {
        dto.setDetalleCajeroId(sesion.getDetalleCajeroId());
        dto.setCajeroId(sesion.getCajero().getCajeroId());
        dto.setCajeroNombre(sesion.getCajero().getNombre());
        dto.setFecha(fecha);
        dto.setHora(LocalDateTime.now(ZONA_BOGOTA).format(HORA_FMT));
        dto.setFechaApertura(sesion.getFechaApertura());
        dto.setFechaCierre(sesion.getFechaCierre());
        dto.setEstadoSesion(sesion.getEstado().name());

        int transInicial = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.VENTA)
                .mapToInt(MovimientoCajero::getConsecutivoTipo)
                .min().orElse(0);
        int transFinal = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.VENTA)
                .mapToInt(MovimientoCajero::getConsecutivoTipo)
                .max().orElse(0);
        long numVentas = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.VENTA)
                .count();

        dto.setTransaccionInicial(transInicial);
        dto.setTransaccionFinal(transFinal);
        dto.setNumeroTransacciones((int) numVentas);
        // Zeta = número de cierres acumulados del cajero (consecutivo de la sesión)
        dto.setZeta(sesion.getConsecutivo());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  2. MOVIMIENTO DE CUENTAS
    // ════════════════════════════════════════════════════════════════════════
    private void buildMovimientoCuentas(InformeDiarioVentasDTO dto,
                                         Long detalleCajeroId, LocalDate fecha) {
        MovimientoCuentas mc = new MovimientoCuentas();
        Object[] row = informeDiarioRepo.getTotalesVentas(detalleCajeroId, fecha);

        if (row != null && row[0] != null) {
            BigDecimal bruta      = toBD(row[0]);
            BigDecimal gravada    = toBD(row[1]);
            BigDecimal iva        = toBD(row[2]);
            BigDecimal descuentos = toBD(row[3]);
            // Exentas = bruta − gravada − iva  (ventas sin IVA que no son gravadas)
            BigDecimal exentas = bruta.subtract(gravada).subtract(iva).max(BigDecimal.ZERO);

            mc.setTotalVentaBruta(bruta);
            mc.setTotalDescuentos(descuentos);
            mc.setTotalRetenciones(BigDecimal.ZERO);
            mc.setVentasGravadas(gravada);
            mc.setVentasExentas(exentas);
            mc.setTotalIVA(iva);
            mc.setTotalVentas(bruta.subtract(descuentos));
        }
        dto.setMovimientoCuentas(mc);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  3. VENTAS POR LÍNEA
    // ════════════════════════════════════════════════════════════════════════
    private void buildVentasPorLinea(InformeDiarioVentasDTO dto,
                                      Long detalleCajeroId, LocalDate fecha) {
        List<Object[]> rows = informeDiarioRepo.getVentasPorLinea(detalleCajeroId, fecha);
        List<VentaLinea> lineas = new ArrayList<>();
        for (Object[] row : rows) {
            lineas.add(new VentaLinea(
                    row[0] != null ? row[0].toString() : "SIN LÍNEA",
                    toBD(row[1])));
        }
        dto.setVentasPorLinea(lineas);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  4. FORMAS DE PAGO
    // ════════════════════════════════════════════════════════════════════════
    private void buildFormasDePago(InformeDiarioVentasDTO dto,
                                    Long detalleCajeroId, LocalDate fecha) {
        List<Object[]> rows = informeDiarioRepo.getFormasPago(detalleCajeroId, fecha);
        List<FormaPago> formas = new ArrayList<>();
        for (Object[] row : rows) {
            formas.add(new FormaPago(
                    row[0] != null ? row[0].toString() : "OTRO",
                    toBD(row[1])));
        }
        dto.setFormasDePago(formas);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  5. RECIBOS DE CAJA  (ABONO)
    // ════════════════════════════════════════════════════════════════════════
    private void buildRecibosCaja(InformeDiarioVentasDTO dto,
                                   List<MovimientoCajero> movimientos) {
        SeccionRecibosCaja seccion = new SeccionRecibosCaja();

        List<ReciboCaja> recibos = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.ABONO)
                .map(m -> new ReciboCaja(
                        m.getTerceroNombre() != null ? m.getTerceroNombre() : "",
                        m.getMonto(),
                        m.getDescripcion()))
                .collect(Collectors.toList());
        seccion.setRecibos(recibos);

        BigDecimal totalEfectivo = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.ABONO)
                .map(MovimientoCajero::getMontoEfectivo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalElectronico = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.ABONO)
                .map(MovimientoCajero::getMontoElectronico)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRecibos = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.ABONO)
                .map(MovimientoCajero::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        seccion.setTotalEfectivo(totalEfectivo);
        seccion.setTotalTCredito(totalElectronico);
        seccion.setTotalRecibosCaja(totalRecibos);
        dto.setRecibosCaja(seccion);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  6. COMPROBANTES DE EGRESO
    // ════════════════════════════════════════════════════════════════════════
    private void buildEgresos(InformeDiarioVentasDTO dto,
                               List<MovimientoCajero> movimientos) {
        SeccionEgresos seccion = new SeccionEgresos();

        List<ComprobanteEgreso> egresos = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.EGRESO)
                .map(m -> new ComprobanteEgreso(
                        m.getTerceroNombre() != null ? m.getTerceroNombre() : "",
                        m.getMonto(),
                        m.getDescripcion()))
                .collect(Collectors.toList());

        BigDecimal totalEgresos = egresos.stream()
                .map(ComprobanteEgreso::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        seccion.setEgresos(egresos);
        seccion.setTotalEgresos(totalEgresos);
        dto.setEgresos(seccion);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  7. VALES
    // ════════════════════════════════════════════════════════════════════════
    private void buildVales(InformeDiarioVentasDTO dto,
                             List<MovimientoCajero> movimientos) {
        // Se detectan como INGRESO_EFECTIVO con descripción que contiene "VALE"
        BigDecimal totalVales = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.INGRESO_EFECTIVO
                          && m.getDescripcion() != null
                          && m.getDescripcion().toUpperCase().contains("VALE"))
                .map(MovimientoCajero::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalVales(totalVales);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  8. DEVOLUCIONES
    // ════════════════════════════════════════════════════════════════════════
    private void buildDevoluciones(InformeDiarioVentasDTO dto,
                                    Long detalleCajeroId, LocalDate fecha,
                                    List<MovimientoCajero> movimientos) {
        SeccionDevoluciones seccion = new SeccionDevoluciones();

        Object[] row = informeDiarioRepo.getTotalesDevoluciones(detalleCajeroId, fecha);
        if (row != null) {
            seccion.setDevGravada(toBD(row[0]));
            seccion.setIvaDevGravada(toBD(row[1]));
            seccion.setTotDevoluciones(toBD(row[2]));
        }

        // Total contado = efectivo restituido en devoluciones
        BigDecimal totalContado = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.DEVOLUCION)
                .map(MovimientoCajero::getMontoEfectivo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total CxC devoluciones = electrónico restituido en devoluciones
        // (devoluciones sobre ventas a crédito no devuelven efectivo)
        BigDecimal totalCxC = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.DEVOLUCION)
                .map(MovimientoCajero::getMontoElectronico)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        seccion.setDevExentas(BigDecimal.ZERO);
        seccion.setTotalContado(totalContado);
        seccion.setTotalCxC(totalCxC);
        seccion.setTotalDsc(BigDecimal.ZERO);
        dto.setDevoluciones(seccion);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  9. RESUMEN FINAL  (Neto Caja, UPT, VPT, VPU)
    // ════════════════════════════════════════════════════════════════════════
    private void buildResumenFinal(InformeDiarioVentasDTO dto,
                                    Long detalleCajeroId, LocalDate fecha,
                                    List<MovimientoCajero> movimientos) {
        ResumenFinal resumen = new ResumenFinal();

        BigDecimal totalVentas  = dto.getMovimientoCuentas().getTotalVentas();
        BigDecimal totalEgresos = dto.getEgresos().getTotalEgresos();
        BigDecimal totalRecibos = dto.getRecibosCaja().getTotalRecibosCaja();
        BigDecimal totalDev     = dto.getDevoluciones().getTotDevoluciones();

        // NETO CAJA = TotalVentas − TotalEgresos + TotalRecibosCaja − TotDevoluciones
        resumen.setNetoCaja(totalVentas
                .subtract(totalEgresos)
                .add(totalRecibos)
                .subtract(totalDev));

        int numTransacciones = dto.getNumeroTransacciones() != null ? dto.getNumeroTransacciones() : 0;
        int totalUnidades    = informeDiarioRepo.getTotalUnidades(detalleCajeroId, fecha);
        resumen.setTotalUnidades(totalUnidades);

        // UPT = (unidades / transacciones) * 100  → expresado como %
        if (numTransacciones > 0) {
            resumen.setUpt(BigDecimal.valueOf(totalUnidades)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(numTransacciones), 1, RoundingMode.HALF_UP));
        }
        // VPT = totalVentas / transacciones
        if (numTransacciones > 0) {
            resumen.setVpt(totalVentas
                    .divide(BigDecimal.valueOf(numTransacciones), 2, RoundingMode.HALF_UP));
        }
        // VPU = totalVentas / unidades
        if (totalUnidades > 0) {
            resumen.setVpu(totalVentas
                    .divide(BigDecimal.valueOf(totalUnidades), 3, RoundingMode.HALF_UP));
        }

        dto.setResumenFinal(resumen);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  HELPER
    // ════════════════════════════════════════════════════════════════════════
    private BigDecimal toBD(Object value) {
        if (value == null)                return BigDecimal.ZERO;
        if (value instanceof BigDecimal)  return (BigDecimal) value;
        if (value instanceof Number)      return BigDecimal.valueOf(((Number) value).doubleValue());
        return BigDecimal.ZERO;
    }
}

