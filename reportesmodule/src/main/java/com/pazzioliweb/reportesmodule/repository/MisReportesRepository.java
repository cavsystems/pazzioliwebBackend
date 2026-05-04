package com.pazzioliweb.reportesmodule.repository;

import com.pazzioliweb.ventasmodule.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Reportes de un cajero / usuario individual ("Mi panel").
 * Todas las queries son de SOLO LECTURA y filtran por cajeroId / usuarioId.
 */
public interface MisReportesRepository extends JpaRepository<Venta, Long> {

    // ── Mis ventas (KPI) ──────────────────────────────────────────
    @Query(value = """
            SELECT
                COALESCE(SUM(v.total_venta), 0)  AS total,
                COUNT(v.id)                       AS cantidad,
                COALESCE(SUM(v.gravada), 0)       AS costoTotal
            FROM ventas v
            WHERE v.estado = 'COMPLETADA'
              AND v.cajero_id = :cajeroId
              AND v.fecha_emision BETWEEN :inicio AND :fin
            """, nativeQuery = true)
    List<Object[]> resumenMisVentas(@Param("cajeroId") Integer cajeroId,
                                    @Param("inicio") LocalDate inicio,
                                    @Param("fin") LocalDate fin);

    // ── Desglose de pagos del cajero ──────────────────────────────
    @Query(value = """
            SELECT
                COALESCE(SUM(CASE WHEN mp.sigla LIKE 'EF%' THEN vmp.monto ELSE 0 END), 0) AS efectivo,
                COALESCE(SUM(CASE WHEN mp.tipo_negociacion = 'Credito' THEN vmp.monto ELSE 0 END), 0) AS credito,
                COALESCE(SUM(CASE WHEN mp.sigla NOT LIKE 'EF%' AND mp.tipo_negociacion != 'Credito' THEN vmp.monto ELSE 0 END), 0) AS electronico
            FROM venta_metodos_pago vmp
            JOIN metodos_pago mp ON mp.metodo_pago_id = vmp.metodo_pago_id
            JOIN ventas v ON v.id = vmp.venta_id
            WHERE v.estado = 'COMPLETADA'
              AND v.cajero_id = :cajeroId
              AND v.fecha_emision BETWEEN :inicio AND :fin
            """, nativeQuery = true)
    List<Object[]> misDesglosePagos(@Param("cajeroId") Integer cajeroId,
                                    @Param("inicio") LocalDate inicio,
                                    @Param("fin") LocalDate fin);

    // ── Mis recibos de caja en el periodo (resumen) ──────────────
    @Query(value = """
            SELECT
                COALESCE(SUM(rc.total), 0)  AS total,
                COUNT(rc.id)                AS cantidad
            FROM recibos_caja rc
            WHERE rc.usuario_id = :usuarioId
              AND rc.estado != 'ANULADO'
              AND DATE(rc.fecha_creacion) BETWEEN :inicio AND :fin
            """, nativeQuery = true)
    List<Object[]> resumenMisRecibos(@Param("usuarioId") Integer usuarioId,
                                     @Param("inicio") LocalDate inicio,
                                     @Param("fin") LocalDate fin);

    @Query(value = """
            SELECT
                COALESCE(SUM(ce.total), 0)  AS total,
                COUNT(ce.id)                AS cantidad
            FROM comprobantes_egreso ce
            WHERE ce.usuario_id = :usuarioId
              AND ce.estado != 'ANULADO'
              AND DATE(ce.fecha_creacion) BETWEEN :inicio AND :fin
            """, nativeQuery = true)
    List<Object[]> resumenMisEgresos(@Param("usuarioId") Integer usuarioId,
                                     @Param("inicio") LocalDate inicio,
                                     @Param("fin") LocalDate fin);

    @Query(value = """
            SELECT
                COALESCE(SUM(d.total_neto), 0)  AS total,
                COUNT(d.id)                     AS cantidad
            FROM devoluciones_venta d
            JOIN ventas v ON v.id = d.venta_id
            WHERE v.cajero_id = :cajeroId
              AND DATE(d.fecha_creacion) BETWEEN :inicio AND :fin
            """, nativeQuery = true)
    List<Object[]> resumenMisDevoluciones(@Param("cajeroId") Integer cajeroId,
                                          @Param("inicio") LocalDate inicio,
                                          @Param("fin") LocalDate fin);

    /** Suma y cuenta de recibos+egresos anulados por el usuario en el periodo. */
    @Query(value = """
            SELECT
                COALESCE(SUM(total), 0) AS total,
                COUNT(*)                AS cantidad
            FROM (
                SELECT total FROM recibos_caja
                 WHERE estado = 'ANULADO' AND anulado_por_usuario_id = :usuarioId
                   AND DATE(fecha_anulacion) BETWEEN :inicio AND :fin
                UNION ALL
                SELECT total FROM comprobantes_egreso
                 WHERE estado = 'ANULADO' AND anulado_por_usuario_id = :usuarioId
                   AND DATE(fecha_anulacion) BETWEEN :inicio AND :fin
            ) anulados
            """, nativeQuery = true)
    List<Object[]> resumenMisAnulaciones(@Param("usuarioId") Integer usuarioId,
                                         @Param("inicio") LocalDate inicio,
                                         @Param("fin") LocalDate fin);

    // ── Lista de mis ventas en el periodo ─────────────────────────
    @Query(value = """
            SELECT
                v.id                            AS ventaId,
                v.numero_venta                  AS numeroVenta,
                v.fecha_emision                 AS fechaEmision,
                COALESCE(t.razon_social,
                         CONCAT(COALESCE(t.nombre_1,''),' ',COALESCE(t.apellido_1,''))) AS clienteNombre,
                t.identificacion                AS clienteIdentificacion,
                COALESCE(v.total_venta,0)       AS totalVenta,
                v.estado                        AS estado,
                (SELECT GROUP_CONCAT(mp.descripcion SEPARATOR ', ')
                   FROM venta_metodos_pago vmp
                   JOIN metodos_pago mp ON mp.metodo_pago_id = vmp.metodo_pago_id
                  WHERE vmp.venta_id = v.id)   AS metodoPago,
                (SELECT COALESCE(SUM(dv.cantidad),0) FROM detalles_venta dv WHERE dv.venta_id = v.id) AS cantidadItems
            FROM ventas v
            LEFT JOIN terceros t ON t.tercero_id = v.cliente_id
            WHERE v.cajero_id = :cajeroId
              AND v.fecha_emision BETWEEN :inicio AND :fin
            ORDER BY v.fecha_emision DESC
            """, nativeQuery = true)
    List<Object[]> misVentas(@Param("cajeroId") Integer cajeroId,
                             @Param("inicio") LocalDate inicio,
                             @Param("fin") LocalDate fin);

    // ── Lista de mis recibos de caja en el periodo ───────────────
    @Query(value = """
            SELECT
                rc.id                           AS id,
                rc.consecutivo                  AS consecutivo,
                rc.fecha_creacion               AS fechaCreacion,
                rc.tercero_nombre               AS terceroNombre,
                rc.concepto                     AS concepto,
                rc.concepto_abierto             AS conceptoAbierto,
                COALESCE(rc.total,0)            AS total,
                (SELECT GROUP_CONCAT(mp.descripcion SEPARATOR ', ')
                   FROM recibo_caja_medio_pago rmp
                   JOIN metodos_pago mp ON mp.metodo_pago_id = rmp.metodo_pago_id
                  WHERE rmp.recibo_caja_id = rc.id) AS metodoPago,
                rc.estado                       AS estado
            FROM recibos_caja rc
            WHERE rc.usuario_id = :usuarioId
              AND DATE(rc.fecha_creacion) BETWEEN :inicio AND :fin
            ORDER BY rc.fecha_creacion DESC
            """, nativeQuery = true)
    List<Object[]> misRecibos(@Param("usuarioId") Integer usuarioId,
                              @Param("inicio") LocalDate inicio,
                              @Param("fin") LocalDate fin);

    // ── Lista de mis egresos en el periodo ───────────────────────
    @Query(value = """
            SELECT
                ce.id                           AS id,
                ce.consecutivo                  AS consecutivo,
                ce.fecha_creacion               AS fechaCreacion,
                ce.tercero_nombre               AS terceroNombre,
                ce.concepto                     AS concepto,
                ce.concepto_abierto             AS conceptoAbierto,
                COALESCE(ce.total,0)            AS total,
                (SELECT GROUP_CONCAT(mp.descripcion SEPARATOR ', ')
                   FROM comprobante_egreso_medio_pago cmp
                   JOIN metodos_pago mp ON mp.metodo_pago_id = cmp.metodo_pago_id
                  WHERE cmp.comprobante_egreso_id = ce.id) AS metodoPago,
                ce.estado                       AS estado
            FROM comprobantes_egreso ce
            WHERE ce.usuario_id = :usuarioId
              AND DATE(ce.fecha_creacion) BETWEEN :inicio AND :fin
            ORDER BY ce.fecha_creacion DESC
            """, nativeQuery = true)
    List<Object[]> misEgresos(@Param("usuarioId") Integer usuarioId,
                              @Param("inicio") LocalDate inicio,
                              @Param("fin") LocalDate fin);

    // ── Mi top productos ────────────────────────────────────────
    @Query(value = """
            SELECT
                dv.codigo_producto                          AS codigoProducto,
                MAX(p.descripcion)                          AS descripcion,
                MAX(COALESCE(l.descripcion,'Sin línea'))    AS linea,
                MAX(COALESCE(g.descripcion,'Sin grupo'))    AS grupo,
                SUM(dv.cantidad)                            AS cantidadVendida,
                SUM(dv.total)                               AS totalVendido,
                SUM(dv.cantidad * p.costo)                  AS costoTotal,
                MAX(COALESCE(pv.imagen, p.imagen))          AS imagen
            FROM detalles_venta dv
            JOIN ventas v ON v.id = dv.venta_id
            LEFT JOIN producto_variantes pv ON pv.sku = dv.codigo_producto
            LEFT JOIN productos p ON p.producto_id = pv.producto_id
            LEFT JOIN lineas l ON l.linea_id = p.linea_id
            LEFT JOIN grupos g ON g.grupo_id = p.grupo_id
            WHERE v.estado = 'COMPLETADA'
              AND v.cajero_id = :cajeroId
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY dv.codigo_producto
            ORDER BY cantidadVendida DESC
            LIMIT :topN
            """, nativeQuery = true)
    List<Object[]> misTopProductos(@Param("cajeroId") Integer cajeroId,
                                   @Param("inicio") LocalDate inicio,
                                   @Param("fin") LocalDate fin,
                                   @Param("topN") int topN);

    // ── Mi top clientes ─────────────────────────────────────────
    @Query(value = """
            SELECT
                t.tercero_id      AS clienteId,
                t.identificacion  AS identificacion,
                CONCAT(COALESCE(t.nombre_1,''),' ',COALESCE(t.apellido_1,'')) AS nombre,
                COUNT(v.id)       AS cantidadVentas,
                COALESCE(SUM(v.total_venta), 0) AS totalComprado
            FROM ventas v
            JOIN terceros t ON t.tercero_id = v.cliente_id
            WHERE v.estado = 'COMPLETADA'
              AND v.cajero_id = :cajeroId
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY t.tercero_id, t.identificacion, t.nombre_1, t.apellido_1
            ORDER BY totalComprado DESC
            LIMIT :topN
            """, nativeQuery = true)
    List<Object[]> misTopClientes(@Param("cajeroId") Integer cajeroId,
                                  @Param("inicio") LocalDate inicio,
                                  @Param("fin") LocalDate fin,
                                  @Param("topN") int topN);

    // ── Mis movimientos de caja por tipo ─────────────────────────
    @Query(value = """
            SELECT
                mc.tipo_movimiento                            AS tipo,
                COUNT(mc.movimiento_cajero_id)                AS cantidad,
                COALESCE(SUM(mc.monto), 0)                    AS totalMonto,
                COALESCE(SUM(mc.monto_efectivo), 0)           AS totalEfectivo,
                COALESCE(SUM(mc.monto_electronico), 0)        AS totalElectronico
            FROM movimiento_cajero mc
            WHERE mc.cajero_id = :cajeroId
              AND DATE(mc.fecha_movimiento) BETWEEN :inicio AND :fin
            GROUP BY mc.tipo_movimiento
            ORDER BY totalMonto DESC
            """, nativeQuery = true)
    List<Object[]> misMovimientosCajaPorTipo(@Param("cajeroId") Integer cajeroId,
                                             @Param("inicio") LocalDate inicio,
                                             @Param("fin") LocalDate fin);

    // ── Mis ventas por día ──────────────────────────────────────
    @Query(value = """
            SELECT
                DATE(v.fecha_emision)             AS periodo,
                COALESCE(SUM(v.total_venta), 0)   AS total,
                COUNT(v.id)                       AS cantidad,
                COALESCE(SUM(v.gravada), 0)       AS costoTotal
            FROM ventas v
            WHERE v.estado = 'COMPLETADA'
              AND v.cajero_id = :cajeroId
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY DATE(v.fecha_emision)
            ORDER BY periodo ASC
            """, nativeQuery = true)
    List<Object[]> misVentasPorDia(@Param("cajeroId") Integer cajeroId,
                                   @Param("inicio") LocalDate inicio,
                                   @Param("fin") LocalDate fin);

    // ── Mis ventas por hora (para el día actual) ─────────────────
    @Query(value = """
            SELECT
                HOUR(v.fecha_emision)             AS hora,
                COALESCE(SUM(v.total_venta), 0)   AS total,
                COUNT(v.id)                       AS cantidad
            FROM ventas v
            WHERE v.estado = 'COMPLETADA'
              AND v.cajero_id = :cajeroId
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY HOUR(v.fecha_emision)
            ORDER BY hora ASC
            """, nativeQuery = true)
    List<Object[]> misVentasPorHora(@Param("cajeroId") Integer cajeroId,
                                    @Param("inicio") LocalDate inicio,
                                    @Param("fin") LocalDate fin);

    // ── Sesión activa del cajero ────────────────────────────────
    @Query(value = """
            SELECT
                dc.detalle_cajero_id    AS detalleCajeroId,
                dc.base_caja            AS baseCaja
            FROM detalle_cajero dc
            WHERE dc.cajero_id = :cajeroId
              AND dc.fecha_cierre IS NULL
            ORDER BY dc.fecha_apertura DESC
            LIMIT 1
            """, nativeQuery = true)
    List<Object[]> sesionActivaCajero(@Param("cajeroId") Integer cajeroId);

    /** Total de efectivo neto generado por el cajero en su sesión activa. */
    @Query(value = """
            SELECT COALESCE(SUM(
                CASE
                    WHEN mc.tipo_movimiento IN ('VENTA','RECIBO_CAJA','ABONO','INGRESO_EFECTIVO') THEN mc.monto_efectivo
                    WHEN mc.tipo_movimiento IN ('EGRESO','DEVOLUCION','ANULACION') THEN -ABS(mc.monto_efectivo)
                    ELSE 0
                END
            ), 0)
            FROM movimiento_cajero mc
            WHERE mc.detalle_cajero_id = :detalleCajeroId
            """, nativeQuery = true)
    BigDecimal saldoEfectivoSesion(@Param("detalleCajeroId") Long detalleCajeroId);
}
