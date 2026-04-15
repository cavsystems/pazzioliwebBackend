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
     * Genera el Informe Diario buscando automáticamente la sesión (o sesiones) correctas
     * a partir del cajeroId y la fecha.
     *
     * Si hay más de una sesión con movimientos en esa fecha (por cierre/reapertura de
     * medianoche), consolida los datos de todas las sesiones en un solo informe.
     *
     * @param cajeroId ID del cajero
     * @param fecha    Día del informe
     */
    @Transactional(readOnly = true)
    public InformeDiarioVentasDTO generarInformePorCajero(Integer cajeroId, LocalDate fecha) {

        List<DetalleCajero> sesiones = detalleCajeroRepo.findByCajeroIdAndFechaMovimiento(cajeroId, fecha);

        if (sesiones.isEmpty()) {
            throw new RuntimeException(
                    "No se encontró una sesión del cajero " + cajeroId
                  + " con movimientos en la fecha " + fecha);
        }

        // Si solo hay una sesión, generar informe normal
        if (sesiones.size() == 1) {
            return construirInforme(sesiones.get(0), fecha);
        }

        // Si hay múltiples sesiones, consolidar datos de todas
        return construirInformeConsolidado(sesiones, fecha);
    }

    /**
     * Construye un informe consolidado combinando datos de múltiples sesiones
     * de un mismo cajero en la misma fecha.
     */
    private InformeDiarioVentasDTO construirInformeConsolidado(List<DetalleCajero> sesiones, LocalDate fechaInforme) {
        // Usamos la sesión más reciente (primera en la lista, ya ordenada DESC) para datos de encabezado
        DetalleCajero sesionPrincipal = sesiones.get(0);

        // Recopilar todos los movimientos del día de todas las sesiones
        List<MovimientoCajero> todosMovimientosDia = new ArrayList<>();
        for (DetalleCajero sesion : sesiones) {
            List<MovimientoCajero> movs = movimientoRepo
                    .findByDetalleCajero_DetalleCajeroIdOrderByConsecutivoAsc(sesion.getDetalleCajeroId())
                    .stream()
                    .filter(m -> m.getFechaMovimiento() != null
                              && m.getFechaMovimiento().toLocalDate().equals(fechaInforme))
                    .collect(Collectors.toList());
            todosMovimientosDia.addAll(movs);
        }

        InformeDiarioVentasDTO dto = new InformeDiarioVentasDTO();

        // Encabezado con datos de la sesión principal
        buildEncabezado(dto, sesionPrincipal, todosMovimientosDia, fechaInforme);

        // Para las queries nativas, consolidamos datos de todas las sesiones
        List<Long> sesionIds = sesiones.stream()
                .map(DetalleCajero::getDetalleCajeroId)
                .collect(Collectors.toList());

        buildMovimientoCuentasConsolidado(dto, sesionIds, fechaInforme);
        buildVentasPorLineaConsolidado(dto, sesionIds, fechaInforme);
        buildFormasDePagoConsolidado(dto, sesionIds, fechaInforme);
        buildRecibosCaja(dto, todosMovimientosDia);
        buildEgresos(dto, todosMovimientosDia);
        buildVales(dto, todosMovimientosDia);
        buildTotalCxCConsolidado(dto, sesionIds, fechaInforme);
        buildDevolucionesConsolidado(dto, sesionIds, fechaInforme, todosMovimientosDia);
        buildResumenFinalConsolidado(dto, sesionIds, fechaInforme, todosMovimientosDia);

        return dto;
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
        buildTotalCxC(dto, detalleCajeroId, fechaInforme);
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

        // Resumen de movimientos agrupados por tipo
        List<InformeDiarioVentasDTO.MovimientoTipo> resumen = movimientos.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getTipoMovimiento().name(),
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .map(e -> new InformeDiarioVentasDTO.MovimientoTipo(e.getKey(), e.getValue().intValue()))
                .collect(Collectors.toList());
        dto.setResumenMovimientos(resumen);
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
    //  7b. TOTAL CxC (Cuentas por Cobrar — ventas a crédito)
    // ════════════════════════════════════════════════════════════════════════
    private void buildTotalCxC(InformeDiarioVentasDTO dto,
                                Long detalleCajeroId, LocalDate fecha) {
        buildTotalCxCConsolidado(dto, List.of(detalleCajeroId), fecha);
    }

    private void buildTotalCxCConsolidado(InformeDiarioVentasDTO dto,
                                           List<Long> sesionIds, LocalDate fecha) {
        dto.setTotalCxC(informeDiarioRepo.getTotalCxCConsolidado(sesionIds, fecha));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  8. DEVOLUCIONES
    // ════════════════════════════════════════════════════════════════════════
    private void buildDevoluciones(InformeDiarioVentasDTO dto,
                                    Long detalleCajeroId, LocalDate fecha,
                                    List<MovimientoCajero> movimientos) {
        buildDevolucionesConsolidado(dto, List.of(detalleCajeroId), fecha, movimientos);
    }

    private void buildDevolucionesConsolidado(InformeDiarioVentasDTO dto,
                                               List<Long> sesionIds, LocalDate fecha,
                                               List<MovimientoCajero> movimientos) {
        SeccionDevoluciones seccion = new SeccionDevoluciones();

        Object[] row = informeDiarioRepo.getTotalesDevolucionesConsolidado(sesionIds, fecha);
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

        // Total medios electrónicos = tarjeta/transferencia restituidos en devoluciones
        BigDecimal totalMedElec = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.DEVOLUCION)
                .map(MovimientoCajero::getMontoElectronico)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        seccion.setDevExentas(BigDecimal.ZERO);
        seccion.setTotalContado(totalContado);
        seccion.setTotalMedElectronico(totalMedElec);
        seccion.setTotalDsc(BigDecimal.ZERO);
        dto.setDevoluciones(seccion);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  9. RESUMEN FINAL  (Neto Caja, UPT, VPT, VPU)
    // ════════════════════════════════════════════════════════════════════════
    private void buildResumenFinal(InformeDiarioVentasDTO dto,
                                    Long detalleCajeroId, LocalDate fecha,
                                    List<MovimientoCajero> movimientos) {
        buildResumenFinalConsolidado(dto, List.of(detalleCajeroId), fecha, movimientos);
    }

    private void buildResumenFinalConsolidado(InformeDiarioVentasDTO dto,
                                               List<Long> sesionIds, LocalDate fecha,
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
        int totalUnidades    = informeDiarioRepo.getTotalUnidadesConsolidado(sesionIds, fecha);
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
    //  MÉTODOS CONSOLIDADOS PARA MOVIMIENTO DE CUENTAS, VENTAS POR LÍNEA
    //  Y FORMAS DE PAGO (múltiples sesiones)
    // ════════════════════════════════════════════════════════════════════════
    private void buildMovimientoCuentasConsolidado(InformeDiarioVentasDTO dto,
                                                    List<Long> sesionIds, LocalDate fecha) {
        MovimientoCuentas mc = new MovimientoCuentas();
        Object[] row = informeDiarioRepo.getTotalesVentasConsolidado(sesionIds, fecha);

        if (row != null && row[0] != null) {
            BigDecimal bruta      = toBD(row[0]);
            BigDecimal gravada    = toBD(row[1]);
            BigDecimal iva        = toBD(row[2]);
            BigDecimal descuentos = toBD(row[3]);
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

    private void buildVentasPorLineaConsolidado(InformeDiarioVentasDTO dto,
                                                  List<Long> sesionIds, LocalDate fecha) {
        List<Object[]> rows = informeDiarioRepo.getVentasPorLineaConsolidado(sesionIds, fecha);
        List<VentaLinea> lineas = new ArrayList<>();
        for (Object[] row : rows) {
            lineas.add(new VentaLinea(
                    row[0] != null ? row[0].toString() : "SIN LÍNEA",
                    toBD(row[1])));
        }
        dto.setVentasPorLinea(lineas);
    }

    private void buildFormasDePagoConsolidado(InformeDiarioVentasDTO dto,
                                                List<Long> sesionIds, LocalDate fecha) {
        List<Object[]> rows = informeDiarioRepo.getFormasPagoConsolidado(sesionIds, fecha);
        List<FormaPago> formas = new ArrayList<>();
        for (Object[] row : rows) {
            formas.add(new FormaPago(
                    row[0] != null ? row[0].toString() : "OTRO",
                    toBD(row[1])));
        }
        dto.setFormasDePago(formas);
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

