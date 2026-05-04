package com.pazzioliweb.reportesmodule.service.impl;

import com.pazzioliweb.reportesmodule.dtos.*;
import com.pazzioliweb.reportesmodule.repository.MisReportesRepository;
import com.pazzioliweb.reportesmodule.service.MisReportesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MisReportesServiceImpl implements MisReportesService {

    private final MisReportesRepository repo;

    public MisReportesServiceImpl(MisReportesRepository repo) {
        this.repo = repo;
    }

    // ════════════════════════════════════════════════════════════
    // RESUMEN DEL DÍA (KPIs)
    // ════════════════════════════════════════════════════════════

    @Override
    public MiResumenHoyDTO getResumenHoy(Integer cajeroId, Integer usuarioId, LocalDate fecha) {
        MiResumenHoyDTO dto = new MiResumenHoyDTO();
        if (fecha == null) fecha = LocalDate.now();

        // Mis ventas (solo si tengo cajeroId)
        if (cajeroId != null) {
            List<Object[]> rv = repo.resumenMisVentas(cajeroId, fecha, fecha);
            if (rv != null && !rv.isEmpty()) {
                Object[] r = rv.get(0);
                dto.setMisVentas(toBigDecimal(r[0]));
                dto.setCantidadVentas(toLong(r[1]));
                if (dto.getCantidadVentas() > 0) {
                    dto.setTicketPromedio(dto.getMisVentas()
                            .divide(BigDecimal.valueOf(dto.getCantidadVentas()), 2, RoundingMode.HALF_UP));
                }
            }
            // desglose pagos
            List<Object[]> rp = repo.misDesglosePagos(cajeroId, fecha, fecha);
            if (rp != null && !rp.isEmpty()) {
                Object[] r = rp.get(0);
                dto.setTotalEfectivo(toBigDecimal(r[0]));
                dto.setTotalCredito(toBigDecimal(r[1]));
                dto.setTotalElectronico(toBigDecimal(r[2]));
            }
            // devoluciones que afectaron mis ventas
            List<Object[]> rd = repo.resumenMisDevoluciones(cajeroId, fecha, fecha);
            if (rd != null && !rd.isEmpty()) {
                Object[] r = rd.get(0);
                dto.setMisDevoluciones(toBigDecimal(r[0]));
                dto.setCantidadDevoluciones(toLong(r[1]));
            }

            // sesión activa
            List<Object[]> sesion = repo.sesionActivaCajero(cajeroId);
            if (sesion != null && !sesion.isEmpty()) {
                Object[] s = sesion.get(0);
                Long detalleCajeroId = toLong(s[0]);
                BigDecimal base = toBigDecimal(s[1]);
                BigDecimal saldoNeto = repo.saldoEfectivoSesion(detalleCajeroId);
                dto.setTieneSesionAbierta(true);
                dto.setDetalleCajeroId(detalleCajeroId);
                dto.setBaseCaja(base);
                dto.setSaldoEsperadoCaja(base.add(saldoNeto));
            }
        }

        // Mis recibos / mis egresos (por usuarioId)
        if (usuarioId != null) {
            List<Object[]> rr = repo.resumenMisRecibos(usuarioId, fecha, fecha);
            if (rr != null && !rr.isEmpty()) {
                Object[] r = rr.get(0);
                dto.setMisRecibosCaja(toBigDecimal(r[0]));
                dto.setCantidadRecibos(toLong(r[1]));
            }
            List<Object[]> re = repo.resumenMisEgresos(usuarioId, fecha, fecha);
            if (re != null && !re.isEmpty()) {
                Object[] r = re.get(0);
                dto.setMisEgresos(toBigDecimal(r[0]));
                dto.setCantidadEgresos(toLong(r[1]));
            }
            List<Object[]> ra = repo.resumenMisAnulaciones(usuarioId, fecha, fecha);
            if (ra != null && !ra.isEmpty()) {
                Object[] r = ra.get(0);
                dto.setMisAnulaciones(toBigDecimal(r[0]));
                dto.setCantidadAnulaciones(toLong(r[1]));
            }
        }

        return dto;
    }

    // ════════════════════════════════════════════════════════════
    // LISTAS
    // ════════════════════════════════════════════════════════════

    @Override
    public List<MiVentaDTO> getMisVentas(Integer cajeroId, LocalDate inicio, LocalDate fin) {
        if (cajeroId == null) return List.of();
        List<MiVentaDTO> result = new ArrayList<>();
        for (Object[] r : repo.misVentas(cajeroId, inicio, fin)) {
            result.add(new MiVentaDTO(
                    toLong(r[0]),
                    str(r[1]),
                    toLocalDateTime(r[2]),
                    str(r[3]),
                    str(r[4]),
                    toBigDecimal(r[5]),
                    str(r[6]),
                    str(r[7]),
                    toLong(r[8])
            ));
        }
        return result;
    }

    @Override
    public List<MiReciboDTO> getMisRecibos(Integer usuarioId, LocalDate inicio, LocalDate fin) {
        if (usuarioId == null) return List.of();
        List<MiReciboDTO> result = new ArrayList<>();
        for (Object[] r : repo.misRecibos(usuarioId, inicio, fin)) {
            result.add(new MiReciboDTO(
                    toLong(r[0]),
                    toInt(r[1]),
                    toLocalDateTime(r[2]),
                    str(r[3]),
                    str(r[4]),
                    toBoolean(r[5]),
                    toBigDecimal(r[6]),
                    str(r[7]),
                    str(r[8])
            ));
        }
        return result;
    }

    @Override
    public List<MiEgresoDTO> getMisEgresos(Integer usuarioId, LocalDate inicio, LocalDate fin) {
        if (usuarioId == null) return List.of();
        List<MiEgresoDTO> result = new ArrayList<>();
        for (Object[] r : repo.misEgresos(usuarioId, inicio, fin)) {
            result.add(new MiEgresoDTO(
                    toLong(r[0]),
                    toInt(r[1]),
                    toLocalDateTime(r[2]),
                    str(r[3]),
                    str(r[4]),
                    toBoolean(r[5]),
                    toBigDecimal(r[6]),
                    str(r[7]),
                    str(r[8])
            ));
        }
        return result;
    }

    @Override
    public List<ProductoMasVendidoDTO> getMisTopProductos(Integer cajeroId, LocalDate inicio, LocalDate fin, int topN) {
        if (cajeroId == null) return List.of();
        List<ProductoMasVendidoDTO> result = new ArrayList<>();
        for (Object[] r : repo.misTopProductos(cajeroId, inicio, fin, topN)) {
            BigDecimal totalVendido = toBigDecimal(r[5]);
            BigDecimal costoTotal = toBigDecimal(r[6]);
            result.add(new ProductoMasVendidoDTO(
                    str(r[0]), str(r[1]), str(r[2]), str(r[3]),
                    toLong(r[4]), totalVendido, costoTotal,
                    totalVendido.subtract(costoTotal), str(r[7])
            ));
        }
        return result;
    }

    @Override
    public List<VentasPorClienteDTO> getMisTopClientes(Integer cajeroId, LocalDate inicio, LocalDate fin, int topN) {
        if (cajeroId == null) return List.of();
        List<VentasPorClienteDTO> result = new ArrayList<>();
        for (Object[] r : repo.misTopClientes(cajeroId, inicio, fin, topN)) {
            result.add(new VentasPorClienteDTO(
                    toInt(r[0]), str(r[1]), str(r[2]),
                    toLong(r[3]), toBigDecimal(r[4]), BigDecimal.ZERO
            ));
        }
        return result;
    }

    @Override
    public List<MovimientoCajaTipoDTO> getMisMovimientosCaja(Integer cajeroId, LocalDate inicio, LocalDate fin) {
        if (cajeroId == null) return List.of();
        List<MovimientoCajaTipoDTO> result = new ArrayList<>();
        for (Object[] r : repo.misMovimientosCajaPorTipo(cajeroId, inicio, fin)) {
            result.add(new MovimientoCajaTipoDTO(
                    str(r[0]), toLong(r[1]),
                    toBigDecimal(r[2]), toBigDecimal(r[3]), toBigDecimal(r[4])
            ));
        }
        return result;
    }

    @Override
    public List<VentasPorPeriodoDTO> getMisVentasPorDia(Integer cajeroId, LocalDate inicio, LocalDate fin) {
        if (cajeroId == null) return List.of();
        List<VentasPorPeriodoDTO> result = new ArrayList<>();
        for (Object[] r : repo.misVentasPorDia(cajeroId, inicio, fin)) {
            BigDecimal total = toBigDecimal(r[1]);
            BigDecimal costo = toBigDecimal(r[3]);
            result.add(new VentasPorPeriodoDTO(
                    str(r[0]), total, toLong(r[2]),
                    costo, total.subtract(costo)
            ));
        }
        return result;
    }

    @Override
    public List<VentasPorPeriodoDTO> getMisVentasPorHora(Integer cajeroId, LocalDate inicio, LocalDate fin) {
        if (cajeroId == null) return List.of();
        List<VentasPorPeriodoDTO> result = new ArrayList<>();
        for (Object[] r : repo.misVentasPorHora(cajeroId, inicio, fin)) {
            result.add(new VentasPorPeriodoDTO(
                    str(r[0]), toBigDecimal(r[1]), toLong(r[2]),
                    BigDecimal.ZERO, BigDecimal.ZERO
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // UTILIDADES
    // ════════════════════════════════════════════════════════════

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        if (obj instanceof BigDecimal bd) return bd;
        return new BigDecimal(obj.toString());
    }

    private Long toLong(Object obj) {
        if (obj == null) return 0L;
        if (obj instanceof Long l) return l;
        if (obj instanceof Number n) return n.longValue();
        return Long.parseLong(obj.toString());
    }

    private Integer toInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Integer i) return i;
        if (obj instanceof Number n) return n.intValue();
        return Integer.parseInt(obj.toString());
    }

    private String str(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private Boolean toBoolean(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Boolean b) return b;
        if (obj instanceof Number n) return n.intValue() != 0;
        String s = obj.toString();
        return "1".equals(s) || "true".equalsIgnoreCase(s);
    }

    private LocalDateTime toLocalDateTime(Object obj) {
        if (obj == null) return null;
        if (obj instanceof LocalDateTime ldt) return ldt;
        if (obj instanceof Timestamp ts) return ts.toLocalDateTime();
        if (obj instanceof java.sql.Date d) return d.toLocalDate().atStartOfDay();
        return LocalDateTime.parse(obj.toString().replace(" ", "T"));
    }
}
