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
 *   ventas, detalles_venta, productos, lineas,
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
        String sql = """
                SELECT
                    COALESCE(SUM(v.total_venta), 0),
                    COALESCE(SUM(v.gravada),     0),
                    COALESCE(SUM(v.iva),         0),
                    COALESCE(SUM(v.descuentos),  0)
                FROM ventas v
                JOIN movimiento_cajero mc ON mc.referencia_documento_id = v.id
                WHERE mc.detalle_cajero_id   = :detalleCajeroId
                  AND mc.tipo_movimiento     = 'VENTA'
                  AND DATE(mc.fecha_movimiento) = :fecha
                  AND v.estado               = 'COMPLETADA'
                """;
        return (Object[]) em.createNativeQuery(sql)
                .setParameter("detalleCajeroId", detalleCajeroId)
                .setParameter("fecha", fecha)
                .getSingleResult();
    }

    // ══════════════════════════════════════════════════════════════════
    //  VENTAS POR LÍNEA
    //  Retorna: [[lineaDescripcion, total], ...]
    // ══════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    public List<Object[]> getVentasPorLinea(Long detalleCajeroId, LocalDate fecha) {
        String sql = """
                SELECT l.descripcion, COALESCE(SUM(d.total), 0)
                FROM detalles_venta d
                JOIN ventas    v  ON d.venta_id       = v.id
                JOIN productos p  ON d.codigo_barras  = p.codigo_barras
                JOIN lineas    l  ON p.linea_id       = l.linea_id
                JOIN movimiento_cajero mc ON mc.referencia_documento_id = v.id
                WHERE mc.detalle_cajero_id   = :detalleCajeroId
                  AND mc.tipo_movimiento     = 'VENTA'
                  AND DATE(mc.fecha_movimiento) = :fecha
                  AND v.estado               = 'COMPLETADA'
                GROUP BY l.descripcion
                ORDER BY l.descripcion
                """;
        return em.createNativeQuery(sql)
                .setParameter("detalleCajeroId", detalleCajeroId)
                .setParameter("fecha", fecha)
                .getResultList();
    }

    // ══════════════════════════════════════════════════════════════════
    //  FORMAS DE PAGO
    //  Retorna: [[descripcionMetodo, total], ...]
    // ══════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    public List<Object[]> getFormasPago(Long detalleCajeroId, LocalDate fecha) {
        String sql = """
                SELECT mp.descripcion, COALESCE(SUM(vmp.monto), 0)
                FROM venta_metodos_pago vmp
                JOIN ventas       v  ON vmp.venta_id        = v.id
                JOIN metodos_pago mp ON vmp.metodo_pago_id  = mp.metodo_pago_id
                JOIN movimiento_cajero mc ON mc.referencia_documento_id = v.id
                WHERE mc.detalle_cajero_id   = :detalleCajeroId
                  AND mc.tipo_movimiento     = 'VENTA'
                  AND DATE(mc.fecha_movimiento) = :fecha
                  AND v.estado               = 'COMPLETADA'
                GROUP BY mp.descripcion
                ORDER BY mp.descripcion
                """;
        return em.createNativeQuery(sql)
                .setParameter("detalleCajeroId", detalleCajeroId)
                .setParameter("fecha", fecha)
                .getResultList();
    }

    // ══════════════════════════════════════════════════════════════════
    //  DEVOLUCIONES
    //  Retorna: [totalDevuelto, ivaDevuelto, totalNeto]
    //  Si la tabla no existe aún, retorna ceros sin fallar.
    // ══════════════════════════════════════════════════════════════════
    public Object[] getTotalesDevoluciones(Long detalleCajeroId, LocalDate fecha) {
        String sql = """
                SELECT
                    COALESCE(SUM(dv.total_devuelto), 0),
                    COALESCE(SUM(dv.iva_devuelto),   0),
                    COALESCE(SUM(dv.total_neto),     0)
                FROM devoluciones_venta dv
                JOIN movimiento_cajero mc ON mc.referencia_documento_id = dv.id
                WHERE mc.detalle_cajero_id   = :detalleCajeroId
                  AND mc.tipo_movimiento     = 'DEVOLUCION'
                  AND DATE(mc.fecha_movimiento) = :fecha
                  AND dv.estado              = 'REGISTRADA'
                """;
        try {
            return (Object[]) em.createNativeQuery(sql)
                    .setParameter("detalleCajeroId", detalleCajeroId)
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
        String sql = """
                SELECT COALESCE(SUM(d.cantidad), 0)
                FROM detalles_venta d
                JOIN ventas v ON d.venta_id = v.id
                JOIN movimiento_cajero mc ON mc.referencia_documento_id = v.id
                WHERE mc.detalle_cajero_id   = :detalleCajeroId
                  AND mc.tipo_movimiento     = 'VENTA'
                  AND DATE(mc.fecha_movimiento) = :fecha
                  AND v.estado               = 'COMPLETADA'
                """;
        Object result = em.createNativeQuery(sql)
                .setParameter("detalleCajeroId", detalleCajeroId)
                .setParameter("fecha", fecha)
                .getSingleResult();
        return result != null ? ((Number) result).intValue() : 0;
    }
}

