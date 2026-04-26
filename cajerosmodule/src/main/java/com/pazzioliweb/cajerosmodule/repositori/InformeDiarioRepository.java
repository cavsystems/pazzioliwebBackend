package com.pazzioliweb.cajerosmodule.repositori;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio de consultas nativas para el Informe Diario de Ventas.
 *
 * Usa SQL nativo para evitar la dependencia circular:
 *   cajerosmodule → ventasmodule → cajerosmodule
 *
 * Filtra las ventas que pertenecen a una sesión específica (detalle_cajero_id)
 * usando JOIN con movimiento_cajero, y además filtra por la fecha del día
 * (DATE(mc.fecha_movimiento)) para que el informe sea estrictamente DIARIO.
 *
 * Tablas consultadas directamente:
 *   ventas, detalles_venta, productos, producto_variantes, lineas,
 *   venta_metodos_pago, metodos_pago, devoluciones_venta,
 *   movimiento_cajero
 */
@Repository
public class InformeDiarioRepository {

    @PersistenceContext
    private EntityManager em;

    // ══════════════════════════════════════════════════════════════════
    //  MOVIMIENTO DE CUENTAS
    //  Retorna: [totalVentaBruta, totalGravada, totalIva, totalDescuentos]
    // ══════════════════════════════════════════════════════════════════
    public Object[] getTotalesVentas(Long detalleCajeroId, LocalDate fecha) {
        return getTotalesVentasConsolidado(List.of(detalleCajeroId), fecha);
    }

    public Object[] getTotalesVentasConsolidado(List<Long> detalleCajeroIds, LocalDate fecha) {
        String sql = """
                SELECT
                    COALESCE(SUM(v.total_venta), 0),
                    COALESCE(SUM(v.gravada),     0),
                    COALESCE(SUM(v.iva),         0),
                    COALESCE(SUM(v.descuentos),  0)
                FROM ventas v
                JOIN movimiento_cajero mc ON mc.referencia_documento_id = v.id
                WHERE mc.detalle_cajero_id   IN (:detalleCajeroIds)
                  AND mc.tipo_movimiento     = 'VENTA'
                  AND DATE(mc.fecha_movimiento) = :fecha
                  AND v.estado               = 'COMPLETADA'
                """;
        return (Object[]) em.createNativeQuery(sql)
                .setParameter("detalleCajeroIds", detalleCajeroIds)
                .setParameter("fecha", fecha)
                .getSingleResult();
    }

    // ══════════════════════════════════════════════════════════════════
    //  VENTAS POR LÍNEA
    //  Retorna: [[lineaDescripcion, total], ...]
    //  JOIN corregido: detalles_venta.codigo_barras → producto_variantes → productos → lineas
    // ══════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    public List<Object[]> getVentasPorLinea(Long detalleCajeroId, LocalDate fecha) {
        return getVentasPorLineaConsolidado(List.of(detalleCajeroId), fecha);
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getVentasPorLineaConsolidado(List<Long> detalleCajeroIds, LocalDate fecha) {
        String sql = """
                SELECT l.descripcion, COALESCE(SUM(d.total), 0)
                FROM detalles_venta d
                JOIN ventas              v  ON d.venta_id             = v.id
                JOIN producto_variantes  pv ON d.codigo_barras        = pv.codigo_barras
                JOIN productos           p  ON pv.producto_id         = p.producto_id
                JOIN lineas              l  ON p.linea_id             = l.linea_id
                JOIN movimiento_cajero   mc ON mc.referencia_documento_id = v.id
                WHERE mc.detalle_cajero_id   IN (:detalleCajeroIds)
                  AND mc.tipo_movimiento     = 'VENTA'
                  AND DATE(mc.fecha_movimiento) = :fecha
                  AND v.estado               = 'COMPLETADA'
                GROUP BY l.descripcion
                ORDER BY l.descripcion
                """;
        return em.createNativeQuery(sql)
                .setParameter("detalleCajeroIds", detalleCajeroIds)
                .setParameter("fecha", fecha)
                .getResultList();
    }

    // ══════════════════════════════════════════════════════════════════
    //  FORMAS DE PAGO
    //  Retorna: [[descripcionMetodo, total], ...]
    // ══════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    public List<Object[]> getFormasPago(Long detalleCajeroId, LocalDate fecha) {
        return getFormasPagoConsolidado(List.of(detalleCajeroId), fecha);
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getFormasPagoConsolidado(List<Long> detalleCajeroIds, LocalDate fecha) {
        String sql = """
                SELECT mp.descripcion, COALESCE(SUM(vmp.monto), 0)
                FROM venta_metodos_pago vmp
                JOIN ventas       v  ON vmp.venta_id        = v.id
                JOIN metodos_pago mp ON vmp.metodo_pago_id  = mp.metodo_pago_id
                JOIN movimiento_cajero mc ON mc.referencia_documento_id = v.id
                WHERE mc.detalle_cajero_id   IN (:detalleCajeroIds)
                  AND mc.tipo_movimiento     = 'VENTA'
                  AND DATE(mc.fecha_movimiento) = :fecha
                  AND v.estado               = 'COMPLETADA'
                GROUP BY mp.descripcion
                ORDER BY mp.descripcion
                """;
        return em.createNativeQuery(sql)
                .setParameter("detalleCajeroIds", detalleCajeroIds)
                .setParameter("fecha", fecha)
                .getResultList();
    }

    // ══════════════════════════════════════════════════════════════════
    //  DEVOLUCIONES
    //  Retorna: [totalDevuelto, ivaDevuelto, totalNeto]
    //  Si la tabla no existe aún, retorna ceros sin fallar.
    // ══════════════════════════════════════════════════════════════════
    public Object[] getTotalesDevoluciones(Long detalleCajeroId, LocalDate fecha) {
        return getTotalesDevolucionesConsolidado(List.of(detalleCajeroId), fecha);
    }

    public Object[] getTotalesDevolucionesConsolidado(List<Long> detalleCajeroIds, LocalDate fecha) {
        String sql = """
                SELECT
                    COALESCE(SUM(dv.total_devuelto), 0),
                    COALESCE(SUM(dv.iva_devuelto),   0),
                    COALESCE(SUM(dv.total_neto),     0)
                FROM devoluciones_venta dv
                JOIN movimiento_cajero mc ON mc.referencia_documento_id = dv.id
                WHERE mc.detalle_cajero_id   IN (:detalleCajeroIds)
                  AND mc.tipo_movimiento     = 'DEVOLUCION'
                  AND DATE(mc.fecha_movimiento) = :fecha
                  AND dv.estado              = 'REGISTRADA'
                """;
        try {
            return (Object[]) em.createNativeQuery(sql)
                    .setParameter("detalleCajeroIds", detalleCajeroIds)
                    .setParameter("fecha", fecha)
                    .getSingleResult();
        } catch (Exception e) {
            return new Object[]{ java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO };
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  TOTAL UNIDADES VENDIDAS (para UPT y VPU)
    // ══════════════════════════════════════════════════════════════════
    public int getTotalUnidades(Long detalleCajeroId, LocalDate fecha) {
        return getTotalUnidadesConsolidado(List.of(detalleCajeroId), fecha);
    }

    public int getTotalUnidadesConsolidado(List<Long> detalleCajeroIds, LocalDate fecha) {
        String sql = """
                SELECT COALESCE(SUM(d.cantidad), 0)
                FROM detalles_venta d
                JOIN ventas v ON d.venta_id = v.id
                JOIN movimiento_cajero mc ON mc.referencia_documento_id = v.id
                WHERE mc.detalle_cajero_id   IN (:detalleCajeroIds)
                  AND mc.tipo_movimiento     = 'VENTA'
                  AND DATE(mc.fecha_movimiento) = :fecha
                  AND v.estado               = 'COMPLETADA'
                """;
        Object result = em.createNativeQuery(sql)
                .setParameter("detalleCajeroIds", detalleCajeroIds)
                .setParameter("fecha", fecha)
                .getSingleResult();
        return result != null ? ((Number) result).intValue() : 0;
    }

    // ══════════════════════════════════════════════════════════════════
    //  TOTAL CxC (Cuentas por Cobrar)
    //  Suma de montos de ventas con método de pago tipo_negociacion = 'Credito'
    // ══════════════════════════════════════════════════════════════════
    public java.math.BigDecimal getTotalCxC(Long detalleCajeroId, LocalDate fecha) {
        return getTotalCxCConsolidado(List.of(detalleCajeroId), fecha);
    }

    public java.math.BigDecimal getTotalCxCConsolidado(List<Long> detalleCajeroIds, LocalDate fecha) {
        String sql = """
                SELECT COALESCE(SUM(vmp.monto), 0)
                FROM venta_metodos_pago vmp
                JOIN ventas       v  ON vmp.venta_id        = v.id
                JOIN metodos_pago mp ON vmp.metodo_pago_id  = mp.metodo_pago_id
                JOIN movimiento_cajero mc ON mc.referencia_documento_id = v.id
                WHERE mc.detalle_cajero_id   IN (:detalleCajeroIds)
                  AND mc.tipo_movimiento     = 'VENTA'
                  AND DATE(mc.fecha_movimiento) = :fecha
                  AND v.estado               = 'COMPLETADA'
                  AND mp.tipo_negociacion    = 'Credito'
                """;
        Object result = em.createNativeQuery(sql)
                .setParameter("detalleCajeroIds", detalleCajeroIds)
                .setParameter("fecha", fecha)
                .getSingleResult();
        if (result == null) return java.math.BigDecimal.ZERO;
        if (result instanceof java.math.BigDecimal) return (java.math.BigDecimal) result;
        return java.math.BigDecimal.valueOf(((Number) result).doubleValue());
    }

    // ══════════════════════════════════════════════════════════════════
    //  FORMAS DE PAGO — RECIBOS DE CAJA
    //  Consulta la tabla recibo_caja_medio_pago para obtener el desglose
    //  real por método de pago de los recibos registrados en la sesión.
    //  Retorna: [[descripcionMetodo, total], ...]
    // ══════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    public List<Object[]> getFormasPagoRecibosCaja(List<Long> detalleCajeroIds, LocalDate fecha) {
        String sql = """
                SELECT mp.descripcion, COALESCE(SUM(rcmp.monto), 0)
                FROM recibo_caja_medio_pago rcmp
                JOIN recibos_caja     rc ON rcmp.recibo_caja_id   = rc.id
                JOIN metodos_pago     mp ON rcmp.metodo_pago_id   = mp.metodo_pago_id
                JOIN movimiento_cajero mc ON mc.referencia_documento_id = rc.id
                WHERE mc.detalle_cajero_id   IN (:detalleCajeroIds)
                  AND mc.tipo_movimiento     = 'RECIBO_CAJA'
                  AND DATE(mc.fecha_movimiento) = :fecha
                  AND rc.estado              = 'ACTIVO'
                GROUP BY mp.descripcion
                ORDER BY mp.descripcion
                """;
        try {
            return em.createNativeQuery(sql)
                    .setParameter("detalleCajeroIds", detalleCajeroIds)
                    .setParameter("fecha", fecha)
                    .getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  FORMAS DE PAGO — COMPROBANTES DE EGRESO
    //  Consulta la tabla comprobante_egreso_medio_pago para obtener
    //  el desglose real por método de pago de los egresos.
    //  Retorna: [[descripcionMetodo, total], ...]
    // ══════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    public List<Object[]> getFormasPagoEgresos(List<Long> detalleCajeroIds, LocalDate fecha) {
        String sql = """
                SELECT mp.descripcion, COALESCE(SUM(cemp.monto), 0)
                FROM comprobante_egreso_medio_pago cemp
                JOIN comprobantes_egreso ce ON cemp.comprobante_egreso_id = ce.id
                JOIN metodos_pago        mp ON cemp.metodo_pago_id       = mp.metodo_pago_id
                JOIN movimiento_cajero   mc ON mc.referencia_documento_id = ce.id
                WHERE mc.detalle_cajero_id   IN (:detalleCajeroIds)
                  AND mc.tipo_movimiento     = 'EGRESO'
                  AND DATE(mc.fecha_movimiento) = :fecha
                  AND ce.estado              = 'ACTIVO'
                GROUP BY mp.descripcion
                ORDER BY mp.descripcion
                """;
        try {
            return em.createNativeQuery(sql)
                    .setParameter("detalleCajeroIds", detalleCajeroIds)
                    .setParameter("fecha", fecha)
                    .getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }
}

