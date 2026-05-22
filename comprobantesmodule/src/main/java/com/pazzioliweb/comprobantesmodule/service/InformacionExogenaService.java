package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.dtos.RegistroExogenoDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio que genera los formatos de Información Exógena Tributaria (DIAN).
 * Cada método agrega los movimientos del año fiscal por tercero y produce
 * los registros que se deben reportar.
 *
 * Formatos implementados (Fase A):
 *   1001 — Pagos y abonos en cuenta a terceros
 *   1005 — IVA descontable
 *   1006 — IVA generado
 *   1007 — Ingresos recibidos
 *   1008 — Cuentas por cobrar al 31/dic
 *   1009 — Cuentas por pagar al 31/dic
 */
@Service
public class InformacionExogenaService {

    private static final Logger log = LoggerFactory.getLogger(InformacionExogenaService.class);

    @PersistenceContext
    private EntityManager em;

    // ─── Conceptos DIAN estándar (Resolución 162/2023) ─────────────────────
    public static final String CONCEPTO_1001_COMPRAS  = "5008";  // Compras de inventario
    public static final String CONCEPTO_1001_SERVICIO = "5003";  // Servicios
    public static final String CONCEPTO_1001_OTROS    = "5099";  // Otros pagos
    public static final String CONCEPTO_1005_IVA_DESC = "5005";  // IVA descontable
    public static final String CONCEPTO_1006_IVA_GEN  = "4006";  // IVA generado
    public static final String CONCEPTO_1007_VENTAS   = "4001";  // Ingresos operacionales
    public static final String CONCEPTO_1007_OTROS    = "4002";  // Ingresos no operacionales
    public static final String CONCEPTO_1008_CXC      = "1305";  // Clientes nacionales
    public static final String CONCEPTO_1009_CXP      = "2205";  // Proveedores nacionales

    /**
     * Formato 1007 — Ingresos recibidos por venta a clientes durante el año.
     * Se basa en la tabla `ventas` (ventas COMPLETADAS).
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<RegistroExogenoDTO> generar1007(int anio) {
        log.info("Generando Formato 1007 — Ingresos recibidos año {}", anio);
        List<Object[]> rows = em.createNativeQuery(
            "SELECT COALESCE(ti.codigoTipoIdentificacion, 13) AS tipo_id, " +
            "       t.identificacion, t.dv, " +
            "       COALESCE(t.razon_social, CONCAT_WS(' ', t.nombre_1, t.nombre_2, t.apellido_1, t.apellido_2)) AS razon, " +
            "       SUM(COALESCE(v.gravada, 0)) AS ingresos_brutos, " +
            "       SUM(COALESCE(v.iva, 0)) AS iva_generado " +
            "FROM ventas v " +
            "JOIN terceros t ON t.tercero_id = v.cliente_id " +
            "LEFT JOIN tipoidentificacion ti ON ti.codigo = t.tipo_identificacion_id " +
            "WHERE YEAR(v.fecha_emision) = :anio " +
            "  AND v.estado IN ('COMPLETADA','DEVOLUCION_PARCIAL','DEVUELTA') " +
            "GROUP BY tipo_id, t.identificacion, t.dv, razon")
            .setParameter("anio", anio).getResultList();

        // Calcular devoluciones por cliente
        Map<String, BigDecimal> devolucionesMap = new HashMap<>();
        try {
            List<Object[]> devs = em.createNativeQuery(
                "SELECT t.identificacion, SUM(d.total_neto) AS dev_total " +
                "FROM devoluciones_venta d " +
                "JOIN ventas v ON v.id = d.venta_id " +
                "JOIN terceros t ON t.tercero_id = v.cliente_id " +
                "WHERE YEAR(d.fecha_creacion) = :anio " +
                "GROUP BY t.identificacion")
                .setParameter("anio", anio).getResultList();
            for (Object[] r : devs) {
                devolucionesMap.put((String) r[0], (BigDecimal) r[1]);
            }
        } catch (Exception e) {
            log.warn("No se pudieron calcular devoluciones: {}", e.getMessage());
        }

        List<RegistroExogenoDTO> result = new ArrayList<>();
        for (Object[] r : rows) {
            RegistroExogenoDTO d = new RegistroExogenoDTO();
            d.setTipoDocumento(r[0] != null ? String.valueOf(r[0]) : "13");
            d.setNumeroIdentificacion((String) r[1]);
            d.setDv((String) r[2]);
            d.setRazonSocial((String) r[3]);
            d.setConcepto(CONCEPTO_1007_VENTAS);
            d.setIngresosBrutos(toBd(r[4]));
            d.setIvaGenerado(toBd(r[5]));
            d.setDevolucionesRebajasDescuentos(devolucionesMap.getOrDefault(d.getNumeroIdentificacion(), BigDecimal.ZERO));
            result.add(d);
        }
        log.info("1007: {} registros generados", result.size());
        return result;
    }

    /** Convierte un Object (que puede ser BigDecimal, Number o null) a BigDecimal. */
    private BigDecimal toBd(Object v) {
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof BigDecimal) return (BigDecimal) v;
        if (v instanceof Number) return BigDecimal.valueOf(((Number) v).doubleValue());
        try { return new BigDecimal(v.toString()); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    /**
     * Formato 1001 — Pagos y abonos en cuenta a terceros (proveedores y gastos).
     * Se basa en órdenes de compra legalizadas (ingresadas) + comprobantes de egreso.
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<RegistroExogenoDTO> generar1001(int anio) {
        log.info("Generando Formato 1001 — Pagos a terceros año {}", anio);
        List<Object[]> rows = em.createNativeQuery(
            "SELECT COALESCE(ti.codigoTipoIdentificacion, 31) AS tipo_id, " +
            "       t.identificacion, t.dv, " +
            "       COALESCE(t.razon_social, CONCAT_WS(' ', t.nombre_1, t.apellido_1)) AS razon, " +
            "       SUM(COALESCE(oc.gravada, 0)) AS base_compras, " +
            "       SUM(COALESCE(oc.iva, 0)) AS iva_total " +
            "FROM ordenes_compra oc " +
            "JOIN terceros t ON t.tercero_id = oc.proveedor_id " +
            "LEFT JOIN tipoidentificacion ti ON ti.codigo = t.tipo_identificacion_id " +
            "WHERE YEAR(oc.fecha_emision) = :anio " +
            "  AND oc.estado IN ('RECIBIDA','RECIBIDA_PARCIAL') " +
            "GROUP BY tipo_id, t.identificacion, t.dv, razon")
            .setParameter("anio", anio).getResultList();

        List<RegistroExogenoDTO> result = new ArrayList<>();
        for (Object[] r : rows) {
            RegistroExogenoDTO d = new RegistroExogenoDTO();
            d.setTipoDocumento(r[0] != null ? String.valueOf(r[0]) : "31");
            d.setNumeroIdentificacion((String) r[1]);
            d.setDv((String) r[2]);
            d.setRazonSocial((String) r[3]);
            d.setConcepto(CONCEPTO_1001_COMPRAS);
            d.setPagosSujetosARetencion(toBd(r[4]));
            d.setIvaMayorValorCosto(toBd(r[5]));
            result.add(d);
        }
        log.info("1001: {} registros generados", result.size());
        return result;
    }

    /**
     * Formato 1006 — IVA generado (lo cobrado a clientes por IVA).
     */
    @Transactional(readOnly = true)
    public List<RegistroExogenoDTO> generar1006(int anio) {
        log.info("Generando Formato 1006 — IVA generado año {}", anio);
        List<RegistroExogenoDTO> result = new ArrayList<>();
        for (RegistroExogenoDTO ing : generar1007(anio)) {
            if (ing.getIvaGenerado().compareTo(BigDecimal.ZERO) <= 0) continue;
            RegistroExogenoDTO d = new RegistroExogenoDTO();
            d.setTipoDocumento(ing.getTipoDocumento());
            d.setNumeroIdentificacion(ing.getNumeroIdentificacion());
            d.setDv(ing.getDv());
            d.setRazonSocial(ing.getRazonSocial());
            d.setConcepto(CONCEPTO_1006_IVA_GEN);
            d.setIvaGenerado(ing.getIvaGenerado());
            result.add(d);
        }
        log.info("1006: {} registros", result.size());
        return result;
    }

    /**
     * Formato 1005 — IVA descontable (lo pagado a proveedores por IVA).
     */
    @Transactional(readOnly = true)
    public List<RegistroExogenoDTO> generar1005(int anio) {
        log.info("Generando Formato 1005 — IVA descontable año {}", anio);
        List<RegistroExogenoDTO> result = new ArrayList<>();
        for (RegistroExogenoDTO pago : generar1001(anio)) {
            if (pago.getIvaMayorValorCosto().compareTo(BigDecimal.ZERO) <= 0) continue;
            RegistroExogenoDTO d = new RegistroExogenoDTO();
            d.setTipoDocumento(pago.getTipoDocumento());
            d.setNumeroIdentificacion(pago.getNumeroIdentificacion());
            d.setDv(pago.getDv());
            d.setRazonSocial(pago.getRazonSocial());
            d.setConcepto(CONCEPTO_1005_IVA_DESC);
            d.setIvaDescontable(pago.getIvaMayorValorCosto());
            result.add(d);
        }
        log.info("1005: {} registros", result.size());
        return result;
    }

    /**
     * Formato 1008 — Cuentas por Cobrar al 31/dic del año fiscal.
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<RegistroExogenoDTO> generar1008(int anio) {
        log.info("Generando Formato 1008 — CxC al 31/dic/{}", anio);
        LocalDate corte = LocalDate.of(anio, 12, 31);
        List<Object[]> rows;
        try {
            rows = em.createNativeQuery(
                "SELECT COALESCE(ti.codigoTipoIdentificacion, 13) AS tipo_id, " +
                "       t.identificacion, t.dv, " +
                "       COALESCE(t.razon_social, CONCAT_WS(' ', t.nombre_1, t.apellido_1)) AS razon, " +
                "       SUM(COALESCE(cxc.saldo, 0)) AS saldo " +
                "FROM cuentas_por_cobrar cxc " +
                "JOIN terceros t ON t.tercero_id = cxc.cliente_id " +
                "LEFT JOIN tipoidentificacion ti ON ti.codigo = t.tipo_identificacion_id " +
                "WHERE cxc.fecha_creacion <= :corte " +
                "  AND cxc.estado = 'PENDIENTE' " +
                "  AND cxc.saldo > 0 " +
                "GROUP BY tipo_id, t.identificacion, t.dv, razon")
                .setParameter("corte", corte).getResultList();
        } catch (Exception e) {
            log.warn("Error consultando CxC: {}", e.getMessage());
            return new ArrayList<>();
        }

        List<RegistroExogenoDTO> result = new ArrayList<>();
        for (Object[] r : rows) {
            RegistroExogenoDTO d = new RegistroExogenoDTO();
            d.setTipoDocumento(r[0] != null ? String.valueOf(r[0]) : "13");
            d.setNumeroIdentificacion((String) r[1]);
            d.setDv((String) r[2]);
            d.setRazonSocial((String) r[3]);
            d.setConcepto(CONCEPTO_1008_CXC);
            d.setSaldo(toBd(r[4]));
            result.add(d);
        }
        log.info("1008: {} registros", result.size());
        return result;
    }

    /**
     * Formato 1009 — Cuentas por Pagar al 31/dic del año fiscal.
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<RegistroExogenoDTO> generar1009(int anio) {
        log.info("Generando Formato 1009 — CxP al 31/dic/{}", anio);
        LocalDate corte = LocalDate.of(anio, 12, 31);
        List<Object[]> rows;
        try {
            rows = em.createNativeQuery(
                "SELECT COALESCE(ti.codigoTipoIdentificacion, 31) AS tipo_id, " +
                "       t.identificacion, t.dv, " +
                "       COALESCE(t.razon_social, CONCAT_WS(' ', t.nombre_1, t.apellido_1)) AS razon, " +
                "       SUM(COALESCE(cxp.saldo, 0)) AS saldo " +
                "FROM cuentas_por_pagar cxp " +
                "JOIN terceros t ON t.tercero_id = cxp.proveedor_id " +
                "LEFT JOIN tipoidentificacion ti ON ti.codigo = t.tipo_identificacion_id " +
                "WHERE cxp.fecha_creacion <= :corte " +
                "  AND cxp.estado = 'PENDIENTE' " +
                "  AND cxp.saldo > 0 " +
                "GROUP BY tipo_id, t.identificacion, t.dv, razon")
                .setParameter("corte", corte).getResultList();
        } catch (Exception e) {
            log.warn("Error consultando CxP: {}", e.getMessage());
            return new ArrayList<>();
        }

        List<RegistroExogenoDTO> result = new ArrayList<>();
        for (Object[] r : rows) {
            RegistroExogenoDTO d = new RegistroExogenoDTO();
            d.setTipoDocumento(r[0] != null ? String.valueOf(r[0]) : "31");
            d.setNumeroIdentificacion((String) r[1]);
            d.setDv((String) r[2]);
            d.setRazonSocial((String) r[3]);
            d.setConcepto(CONCEPTO_1009_CXP);
            d.setSaldo(toBd(r[4]));
            result.add(d);
        }
        log.info("1009: {} registros", result.size());
        return result;
    }

    /**
     * Resumen consolidado de todos los formatos para una vista rápida.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> resumen(int anio) {
        Map<String, Object> out = new HashMap<>();
        out.put("anio", anio);

        // 1007
        List<RegistroExogenoDTO> r1007 = generar1007(anio);
        out.put("formato1007", buildSummary(r1007, "Ingresos recibidos",
                r1007.stream().map(RegistroExogenoDTO::getIngresosBrutos).reduce(BigDecimal.ZERO, BigDecimal::add)));

        // 1006
        List<RegistroExogenoDTO> r1006 = generar1006(anio);
        out.put("formato1006", buildSummary(r1006, "IVA generado",
                r1006.stream().map(RegistroExogenoDTO::getIvaGenerado).reduce(BigDecimal.ZERO, BigDecimal::add)));

        // 1001
        List<RegistroExogenoDTO> r1001 = generar1001(anio);
        out.put("formato1001", buildSummary(r1001, "Pagos a terceros",
                r1001.stream().map(RegistroExogenoDTO::getPagosSujetosARetencion).reduce(BigDecimal.ZERO, BigDecimal::add)));

        // 1005
        List<RegistroExogenoDTO> r1005 = generar1005(anio);
        out.put("formato1005", buildSummary(r1005, "IVA descontable",
                r1005.stream().map(RegistroExogenoDTO::getIvaDescontable).reduce(BigDecimal.ZERO, BigDecimal::add)));

        // 1008
        List<RegistroExogenoDTO> r1008 = generar1008(anio);
        out.put("formato1008", buildSummary(r1008, "Cuentas por Cobrar al 31/dic",
                r1008.stream().map(RegistroExogenoDTO::getSaldo).reduce(BigDecimal.ZERO, BigDecimal::add)));

        // 1009
        List<RegistroExogenoDTO> r1009 = generar1009(anio);
        out.put("formato1009", buildSummary(r1009, "Cuentas por Pagar al 31/dic",
                r1009.stream().map(RegistroExogenoDTO::getSaldo).reduce(BigDecimal.ZERO, BigDecimal::add)));

        // ── Cálculo de obligatoriedad según topes DIAN 2024 ──
        BigDecimal ingresos = r1007.stream().map(RegistroExogenoDTO::getIngresosBrutos)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> topes = new HashMap<>();
        BigDecimal topeIngresos = new BigDecimal("500000000");
        topes.put("topeIngresosBrutos", topeIngresos);
        topes.put("ingresosBrutos", ingresos);
        topes.put("obligadoPorIngresos", ingresos.compareTo(topeIngresos) > 0);
        topes.put("obligadoPorTopes", ingresos.compareTo(topeIngresos) > 0);
        out.put("topes", topes);

        return out;
    }

    private Map<String, Object> buildSummary(List<RegistroExogenoDTO> registros, String descripcion, BigDecimal total) {
        Map<String, Object> s = new HashMap<>();
        s.put("descripcion", descripcion);
        s.put("registros", registros.size());
        s.put("total", total);
        return s;
    }
}
