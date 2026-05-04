package com.pazzioliweb.reportesmodule.repository;

import com.pazzioliweb.ventasmodule.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio de solo lectura para construir reportes.
 * Utiliza la entidad Venta como ancla pero hace JOINs cruzados
 * con todas las tablas relevantes del POS.
 */
public interface ReportesRepository extends JpaRepository<Venta, Long> {

    // ══════════════════════════════════════════════════════════════
    // DASHBOARD KPI
    // ══════════════════════════════════════════════════════════════

    @Query("SELECT COALESCE(SUM(v.totalVenta), 0) FROM Venta v WHERE v.estado = 'COMPLETADA' AND v.fechaEmision BETWEEN :inicio AND :fin")
    BigDecimal totalVentasPeriodo(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COUNT(v) FROM Venta v WHERE v.estado = 'COMPLETADA' AND v.fechaEmision BETWEEN :inicio AND :fin")
    Long cantidadVentasPeriodo(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    /** Costo real = SUM(cantidad vendida × costo del producto) en el periodo. */
    @Query(value = """
            SELECT COALESCE(SUM(dv.cantidad * p.costo), 0)
              FROM detalles_venta dv
              JOIN ventas v ON v.id = dv.venta_id
              LEFT JOIN producto_variantes pv ON pv.sku = dv.codigo_producto
              LEFT JOIN productos p ON p.producto_id = pv.producto_id
             WHERE v.estado = 'COMPLETADA'
               AND v.fecha_emision BETWEEN :inicio AND :fin
            """, nativeQuery = true)
    BigDecimal totalCostoPeriodo(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // VENTAS POR DÍA (gráfica de líneas)
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT
                ventas.periodo                    AS periodo,
                ventas.total                      AS total,
                ventas.cantidad                   AS cantidad,
                COALESCE(c.costoTotal, 0)         AS costoTotal
            FROM (
                SELECT
                    DATE(v.fecha_emision)             AS periodo,
                    COALESCE(SUM(v.total_venta), 0)   AS total,
                    COUNT(DISTINCT v.id)              AS cantidad
                FROM ventas v
                WHERE v.estado = 'COMPLETADA'
                  AND v.fecha_emision BETWEEN :inicio AND :fin
                GROUP BY DATE(v.fecha_emision)
            ) ventas
            LEFT JOIN (
                SELECT
                    DATE(v2.fecha_emision)        AS periodo,
                    SUM(dv.cantidad * pp.costo)   AS costoTotal
                FROM detalles_venta dv
                JOIN ventas v2 ON v2.id = dv.venta_id
                LEFT JOIN producto_variantes pv ON pv.sku = dv.codigo_producto
                LEFT JOIN productos pp ON pp.producto_id = pv.producto_id
                WHERE v2.estado='COMPLETADA'
                  AND v2.fecha_emision BETWEEN :inicio AND :fin
                GROUP BY DATE(v2.fecha_emision)
            ) c ON c.periodo = ventas.periodo
            ORDER BY ventas.periodo ASC
            """, nativeQuery = true)
    List<Object[]> ventasPorDia(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // VENTAS POR MES (gráfica de barras anual)
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT
                ventas.periodo                    AS periodo,
                ventas.total                      AS total,
                ventas.cantidad                   AS cantidad,
                COALESCE(c.costoTotal, 0)         AS costoTotal
            FROM (
                SELECT
                    DATE_FORMAT(v.fecha_emision,'%Y-%m') AS periodo,
                    COALESCE(SUM(v.total_venta), 0)      AS total,
                    COUNT(DISTINCT v.id)                 AS cantidad
                FROM ventas v
                WHERE v.estado = 'COMPLETADA'
                  AND v.fecha_emision BETWEEN :inicio AND :fin
                GROUP BY DATE_FORMAT(v.fecha_emision,'%Y-%m')
            ) ventas
            LEFT JOIN (
                SELECT
                    DATE_FORMAT(v2.fecha_emision,'%Y-%m') AS periodo,
                    SUM(dv.cantidad * pp.costo)           AS costoTotal
                FROM detalles_venta dv
                JOIN ventas v2 ON v2.id = dv.venta_id
                LEFT JOIN producto_variantes pv ON pv.sku = dv.codigo_producto
                LEFT JOIN productos pp ON pp.producto_id = pv.producto_id
                WHERE v2.estado='COMPLETADA'
                  AND v2.fecha_emision BETWEEN :inicio AND :fin
                GROUP BY DATE_FORMAT(v2.fecha_emision,'%Y-%m')
            ) c ON c.periodo = ventas.periodo
            ORDER BY ventas.periodo ASC
            """, nativeQuery = true)
    List<Object[]> ventasPorMes(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // VENTAS POR HORA DEL DÍA (gráfica de barras — horarios pico)
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT
                HOUR(v.fecha_emision)             AS periodo,
                COALESCE(SUM(v.total_venta), 0)   AS total,
                COUNT(DISTINCT v.id)              AS cantidad,
                0                                 AS costoTotal
            FROM ventas v
            WHERE v.estado = 'COMPLETADA'
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY HOUR(v.fecha_emision)
            ORDER BY periodo ASC
            """, nativeQuery = true)
    List<Object[]> ventasPorHora(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // TOP PRODUCTOS MÁS VENDIDOS
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT 
                dv.codigo_producto AS codigoProducto,
                MAX(p.descripcion) AS descripcion,
                MAX(l.descripcion) AS linea,
                MAX(g.descripcion) AS grupo,
                SUM(dv.cantidad) AS cantidadVendida,
                SUM(dv.total) AS totalVendido,
                SUM(dv.cantidad * p.costo) AS costoTotal,
                MAX(COALESCE(pv.imagen, p.imagen)) AS imagen
            FROM detalles_venta dv
            JOIN ventas v ON v.id = dv.venta_id
            LEFT JOIN producto_variantes pv ON pv.sku = dv.codigo_producto
            LEFT JOIN productos p ON p.producto_id = pv.producto_id
            LEFT JOIN lineas l ON l.linea_id = p.linea_id
            LEFT JOIN grupos g ON g.grupo_id = p.grupo_id
            WHERE v.estado = 'COMPLETADA'
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY dv.codigo_producto
            ORDER BY cantidadVendida DESC
            LIMIT :topN
            """, nativeQuery = true)
    List<Object[]> topProductosMasVendidos(@Param("inicio") LocalDate inicio,
                                           @Param("fin") LocalDate fin,
                                           @Param("topN") int topN);

    // ══════════════════════════════════════════════════════════════
    // VENTAS POR VENDEDOR
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT 
                ve.vendedor_id AS vendedorId,
                ve.nombre AS vendedorNombre,
                COUNT(v.id) AS cantidadVentas,
                COALESCE(SUM(v.total_venta), 0) AS totalVendido
            FROM ventas v
            JOIN vendedores ve ON ve.vendedor_id = v.vendedor_id
            WHERE v.estado = 'COMPLETADA'
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY ve.vendedor_id, ve.nombre
            ORDER BY totalVendido DESC
            """, nativeQuery = true)
    List<Object[]> ventasPorVendedor(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // VENTAS POR CAJERO
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT
                c.cajero_id                                                                AS cajeroId,
                c.nombre                                                                   AS cajeroNombre,
                COUNT(DISTINCT v.id)                                                       AS cantidadVentas,
                COALESCE(SUM(v.total_venta), 0)                                            AS totalVendido,
                COALESCE((SELECT SUM(CASE WHEN mp.sigla LIKE 'EF%' THEN vmp.monto ELSE 0 END)
                          FROM venta_metodos_pago vmp
                          JOIN metodos_pago mp ON mp.metodo_pago_id = vmp.metodo_pago_id
                          JOIN ventas vv ON vv.id = vmp.venta_id
                         WHERE vv.estado='COMPLETADA'
                           AND vv.cajero_id = c.cajero_id
                           AND vv.fecha_emision BETWEEN :inicio AND :fin), 0)              AS totalEfectivo,
                COALESCE((SELECT SUM(CASE WHEN mp.sigla NOT LIKE 'EF%' AND mp.tipo_negociacion != 'Credito' THEN vmp.monto ELSE 0 END)
                          FROM venta_metodos_pago vmp
                          JOIN metodos_pago mp ON mp.metodo_pago_id = vmp.metodo_pago_id
                          JOIN ventas vv ON vv.id = vmp.venta_id
                         WHERE vv.estado='COMPLETADA'
                           AND vv.cajero_id = c.cajero_id
                           AND vv.fecha_emision BETWEEN :inicio AND :fin), 0)              AS totalElectronico
            FROM ventas v
            JOIN cajeros c ON c.cajero_id = v.cajero_id
            WHERE v.estado = 'COMPLETADA'
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY c.cajero_id, c.nombre
            ORDER BY totalVendido DESC
            """, nativeQuery = true)
    List<Object[]> ventasPorCajero(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // VENTAS POR MÉTODO DE PAGO (gráfica de torta)
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT 
                mp.metodo_pago_id AS metodoPagoId,
                mp.descripcion AS nombre,
                mp.sigla AS sigla,
                COUNT(vmp.id) AS cantidadTransacciones,
                COALESCE(SUM(vmp.monto), 0) AS totalMonto
            FROM venta_metodos_pago vmp
            JOIN metodos_pago mp ON mp.metodo_pago_id = vmp.metodo_pago_id
            JOIN ventas v ON v.id = vmp.venta_id
            WHERE v.estado = 'COMPLETADA'
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY mp.metodo_pago_id, mp.descripcion, mp.sigla
            ORDER BY totalMonto DESC
            """, nativeQuery = true)
    List<Object[]> ventasPorMetodoPago(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // TOP CLIENTES
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT
                t.tercero_id                                                          AS clienteId,
                t.identificacion                                                      AS identificacion,
                COALESCE(t.razon_social,
                         CONCAT(COALESCE(t.nombre_1,''),' ',COALESCE(t.apellido_1,''))) AS nombre,
                COUNT(DISTINCT v.id)                                                  AS cantidadVentas,
                COALESCE(SUM(v.total_venta), 0)                                       AS totalComprado,
                COALESCE((SELECT SUM(saldo) FROM cuentas_por_cobrar
                           WHERE cliente_id = t.tercero_id
                             AND estado IN ('PENDIENTE','PARCIAL')), 0)               AS saldoCartera
            FROM ventas v
            JOIN terceros t ON t.tercero_id = v.cliente_id
            WHERE v.estado = 'COMPLETADA'
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY t.tercero_id, t.identificacion, t.razon_social, t.nombre_1, t.apellido_1
            ORDER BY totalComprado DESC
            LIMIT :topN
            """, nativeQuery = true)
    List<Object[]> topClientes(@Param("inicio") LocalDate inicio,
                                @Param("fin") LocalDate fin,
                                @Param("topN") int topN);

    // ══════════════════════════════════════════════════════════════
    // VENTAS POR LÍNEA / CATEGORÍA (gráfica de torta)
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT 
                COALESCE(l.descripcion, 'Sin línea') AS categoria,
                SUM(dv.cantidad) AS cantidadItems,
                COALESCE(SUM(dv.total), 0) AS totalVendido
            FROM detalles_venta dv
            JOIN ventas v ON v.id = dv.venta_id
            LEFT JOIN producto_variantes pv ON pv.sku = dv.codigo_producto
            LEFT JOIN productos p ON p.producto_id = pv.producto_id
            LEFT JOIN lineas l ON l.linea_id = p.linea_id
            WHERE v.estado = 'COMPLETADA'
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY l.descripcion
            ORDER BY totalVendido DESC
            """, nativeQuery = true)
    List<Object[]> ventasPorLinea(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // CARTERA — CUENTAS POR COBRAR POR ESTADO
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT 
                cxc.estado,
                COUNT(cxc.id) AS cantidad,
                COALESCE(SUM(cxc.valor_neto), 0) AS totalValorNeto,
                COALESCE(SUM(cxc.saldo), 0) AS totalSaldo
            FROM cuentas_por_cobrar cxc
            GROUP BY cxc.estado
            ORDER BY totalSaldo DESC
            """, nativeQuery = true)
    List<Object[]> carteraPorEstado();

    // ══════════════════════════════════════════════════════════════
    // DEVOLUCIONES
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT 
                COALESCE(SUM(d.total_neto), 0) AS totalDevoluciones,
                COUNT(d.id) AS cantidadDevoluciones
            FROM devoluciones_venta d
            WHERE d.fecha_creacion BETWEEN :inicio AND :fin
            """, nativeQuery = true)
    List<Object[]> totalDevolucionesPeriodo(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // INVENTARIO — PRODUCTOS CON STOCK BAJO
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT COUNT(*) FROM existencias e
            JOIN producto_variantes pv ON pv.producto_variantes_id = e.producto_variantes_id
            WHERE e.existencia <= e.stock_min AND e.existencia > 0
            """, nativeQuery = true)
    Long contarProductosStockBajo();

    @Query(value = """
            SELECT COALESCE(SUM(e.existencia * p.costo), 0)
            FROM existencias e
            JOIN producto_variantes pv ON pv.producto_variantes_id = e.producto_variantes_id
            JOIN productos p ON p.producto_id = pv.producto_id
            """, nativeQuery = true)
    BigDecimal valorTotalInventario();

    // ══════════════════════════════════════════════════════════════
    // RESUMEN CAJERO — movimientos de caja por tipo
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT
                mc.tipo_movimiento                            AS tipo,
                COUNT(mc.movimiento_cajero_id)                AS cantidad,
                COALESCE(SUM(mc.monto), 0)                    AS totalMonto,
                COALESCE(SUM(mc.monto_efectivo), 0)           AS totalEfectivo,
                COALESCE(SUM(mc.monto_electronico), 0)        AS totalElectronico
            FROM movimiento_cajero mc
            WHERE DATE(mc.fecha_movimiento) BETWEEN :inicio AND :fin
            GROUP BY mc.tipo_movimiento
            ORDER BY totalMonto DESC
            """, nativeQuery = true)
    List<Object[]> movimientosCajaPorTipo(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // CONTADORES GENERALES
    // ══════════════════════════════════════════════════════════════

    @Query(value = "SELECT COUNT(*) FROM terceros WHERE 1=1", nativeQuery = true)
    Long contarClientes();

    @Query(value = "SELECT COUNT(*) FROM producto_variantes WHERE activo = 1", nativeQuery = true)
    Long contarProductosActivos();

    @Query(value = """
            SELECT COALESCE(SUM(cxc.saldo), 0)
            FROM cuentas_por_cobrar cxc
            WHERE cxc.estado IN ('PENDIENTE', 'PARCIAL')
            """, nativeQuery = true)
    BigDecimal carteraPendienteTotal();

    @Query(value = """
            SELECT COUNT(*)
            FROM cuentas_por_cobrar cxc
            WHERE cxc.estado IN ('PENDIENTE', 'PARCIAL')
              AND cxc.fecha_vencimiento < CURDATE()
            """, nativeQuery = true)
    Long contarCuentasVencidas();

    // ══════════════════════════════════════════════════════════════
    // EFECTIVO / ELECTRÓNICO / CRÉDITO en un período
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT 
                COALESCE(SUM(CASE WHEN mp.sigla LIKE 'EF%' THEN vmp.monto ELSE 0 END), 0) AS totalEfectivo,
                COALESCE(SUM(CASE WHEN mp.tipo_negociacion = 'Credito' THEN vmp.monto ELSE 0 END), 0) AS totalCredito,
                COALESCE(SUM(CASE WHEN mp.sigla NOT LIKE 'EF%' AND mp.tipo_negociacion != 'Credito' THEN vmp.monto ELSE 0 END), 0) AS totalElectronico
            FROM venta_metodos_pago vmp
            JOIN metodos_pago mp ON mp.metodo_pago_id = vmp.metodo_pago_id
            JOIN ventas v ON v.id = vmp.venta_id
            WHERE v.estado = 'COMPLETADA'
              AND v.fecha_emision BETWEEN :inicio AND :fin
            """, nativeQuery = true)
    List<Object[]> desglosePagos(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // COMPRAS POR PROVEEDOR
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT 
                t.tercero_id AS proveedorId,
                COALESCE(t.razon_social, CONCAT(t.nombre_1,' ',COALESCE(t.apellido_1,''))) AS proveedorNombre,
                COUNT(oc.id) AS cantidadOrdenes,
                COALESCE(SUM(oc.total_orden_compra), 0) AS totalComprado
            FROM ordenes_compra oc
            JOIN terceros t ON t.tercero_id = oc.proveedor_id
            WHERE oc.fecha_emision BETWEEN :inicio AND :fin
              AND oc.estado != 'ANULADA'
            GROUP BY t.tercero_id, t.razon_social, t.nombre_1, t.apellido_1
            ORDER BY totalComprado DESC
            LIMIT :topN
            """, nativeQuery = true)
    List<Object[]> comprasPorProveedor(@Param("inicio") LocalDate inicio,
                                       @Param("fin") LocalDate fin,
                                       @Param("topN") int topN);

    // ══════════════════════════════════════════════════════════════
    // COMPRAS VS VENTAS POR MES (comparativa)
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT 
                meses.periodo,
                COALESCE(v.totalVentas, 0) AS totalVentas,
                COALESCE(c.totalCompras, 0) AS totalCompras
            FROM (
                SELECT DISTINCT DATE_FORMAT(fecha_emision, '%Y-%m') AS periodo 
                FROM ventas WHERE fecha_emision BETWEEN :inicio AND :fin
                UNION
                SELECT DISTINCT DATE_FORMAT(fecha_emision, '%Y-%m') AS periodo 
                FROM ordenes_compra WHERE fecha_emision BETWEEN :inicio AND :fin
            ) meses
            LEFT JOIN (
                SELECT DATE_FORMAT(fecha_emision, '%Y-%m') AS periodo, SUM(total_venta) AS totalVentas
                FROM ventas WHERE estado = 'COMPLETADA' AND fecha_emision BETWEEN :inicio AND :fin
                GROUP BY DATE_FORMAT(fecha_emision, '%Y-%m')
            ) v ON v.periodo = meses.periodo
            LEFT JOIN (
                SELECT DATE_FORMAT(fecha_emision, '%Y-%m') AS periodo, SUM(total_orden_compra) AS totalCompras
                FROM ordenes_compra WHERE estado != 'ANULADA' AND fecha_emision BETWEEN :inicio AND :fin
                GROUP BY DATE_FORMAT(fecha_emision, '%Y-%m')
            ) c ON c.periodo = meses.periodo
            ORDER BY meses.periodo ASC
            """, nativeQuery = true)
    List<Object[]> comprasVsVentasPorMes(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // PRODUCTOS CON STOCK BAJO — LISTADO DETALLADO
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT 
                pv.producto_variantes_id AS varianteId,
                pv.sku,
                IF(pv.predeterminada=0, CONCAT(p.descripcion,' - ',pv.referencia_variantes), p.descripcion) AS descripcion,
                COALESCE(l.descripcion, 'Sin línea') AS linea,
                b.nombre AS bodega,
                e.existencia AS existenciaActual,
                e.stock_min AS stockMinimo,
                e.stock_max AS stockMaximo,
                p.costo,
                COALESCE(pv.imagen, p.imagen) AS imagen
            FROM existencias e
            JOIN producto_variantes pv ON pv.producto_variantes_id = e.producto_variantes_id
            JOIN productos p ON p.producto_id = pv.producto_id
            JOIN bodegas b ON b.codigo = e.bodega_id
            LEFT JOIN lineas l ON l.linea_id = p.linea_id
            WHERE e.existencia <= e.stock_min AND e.existencia >= 0
            ORDER BY e.existencia ASC
            LIMIT :limite
            """, nativeQuery = true)
    List<Object[]> productosStockBajo(@Param("limite") int limite);

    // ══════════════════════════════════════════════════════════════
    // RENTABILIDAD POR PRODUCTO (margen de ganancia)
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT 
                dv.codigo_producto AS codigoProducto,
                MAX(p.descripcion) AS descripcion,
                MAX(COALESCE(l.descripcion, 'Sin línea')) AS linea,
                SUM(dv.cantidad) AS cantidadVendida,
                SUM(dv.total) AS ingresos,
                SUM(dv.cantidad * p.costo) AS costoTotal
            FROM detalles_venta dv
            JOIN ventas v ON v.id = dv.venta_id
            LEFT JOIN producto_variantes pv ON pv.sku = dv.codigo_producto
            LEFT JOIN productos p ON p.producto_id = pv.producto_id
            LEFT JOIN lineas l ON l.linea_id = p.linea_id
            WHERE v.estado = 'COMPLETADA'
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY dv.codigo_producto
            ORDER BY (SUM(dv.total) - SUM(dv.cantidad * p.costo)) DESC
            LIMIT :topN
            """, nativeQuery = true)
    List<Object[]> rentabilidadPorProducto(@Param("inicio") LocalDate inicio,
                                            @Param("fin") LocalDate fin,
                                            @Param("topN") int topN);

    // ══════════════════════════════════════════════════════════════
    // VENTAS POR DÍA DE LA SEMANA (Lunes, Martes... Domingo)
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT 
                DAYOFWEEK(v.fecha_emision) AS diaSemana,
                CASE DAYOFWEEK(v.fecha_emision)
                    WHEN 1 THEN 'Domingo'
                    WHEN 2 THEN 'Lunes'
                    WHEN 3 THEN 'Martes'
                    WHEN 4 THEN 'Miércoles'
                    WHEN 5 THEN 'Jueves'
                    WHEN 6 THEN 'Viernes'
                    WHEN 7 THEN 'Sábado'
                END AS nombreDia,
                COALESCE(SUM(v.total_venta), 0) AS total,
                COUNT(v.id) AS cantidad
            FROM ventas v
            WHERE v.estado = 'COMPLETADA'
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY DAYOFWEEK(v.fecha_emision)
            ORDER BY diaSemana ASC
            """, nativeQuery = true)
    List<Object[]> ventasPorDiaSemana(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // TOTAL COMPRAS EN PERÍODO
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT COALESCE(SUM(oc.total_orden_compra), 0)
            FROM ordenes_compra oc
            WHERE oc.estado != 'ANULADA'
              AND oc.fecha_emision BETWEEN :inicio AND :fin
            """, nativeQuery = true)
    BigDecimal totalComprasPeriodo(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // CUENTAS POR PAGAR PENDIENTES
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT COALESCE(SUM(cpp.valor_neto), 0)
            FROM cuentas_por_pagar cpp
            WHERE cpp.estado IN ('PENDIENTE', 'PARCIAL')
            """, nativeQuery = true)
    BigDecimal cuentasPorPagarTotal();

    // ══════════════════════════════════════════════════════════════
    // ANULACIONES (vista unificada: ventas + recibos + egresos)
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT
                'VENTA'                                      AS tipoDocumento,
                v.id                                         AS documentoId,
                v.numero_venta                               AS consecutivo,
                v.fecha_emision                              AS fechaCreacion,
                NULL                                         AS fechaAnulacion,
                COALESCE(t.razon_social,
                         CONCAT(COALESCE(t.nombre_1,''),' ',COALESCE(t.apellido_1,''))) AS terceroNombre,
                COALESCE(v.total_venta, 0)                   AS total,
                NULL                                         AS motivoAnulacion,
                NULL                                         AS anuladoPor,
                NULL                                         AS anuladoNombre
            FROM ventas v
            LEFT JOIN terceros t ON t.tercero_id = v.cliente_id
            WHERE v.estado = 'ANULADA'
              AND v.fecha_emision BETWEEN :inicio AND :fin
            UNION ALL
            SELECT
                'RECIBO'                                     AS tipoDocumento,
                rc.id                                        AS documentoId,
                CAST(rc.consecutivo AS CHAR)                 AS consecutivo,
                rc.fecha_creacion                            AS fechaCreacion,
                rc.fecha_anulacion                           AS fechaAnulacion,
                rc.tercero_nombre                            AS terceroNombre,
                COALESCE(rc.total, 0)                        AS total,
                rc.motivo_anulacion                          AS motivoAnulacion,
                rc.anulado_por_usuario_id                    AS anuladoPor,
                u.nombre                                     AS anuladoNombre
            FROM recibos_caja rc
            LEFT JOIN usuarios u ON u.codigo = rc.anulado_por_usuario_id
            WHERE rc.estado = 'ANULADO'
              AND DATE(rc.fecha_anulacion) BETWEEN :inicio AND :fin
            UNION ALL
            SELECT
                'EGRESO'                                     AS tipoDocumento,
                ce.id                                        AS documentoId,
                CAST(ce.consecutivo AS CHAR)                 AS consecutivo,
                ce.fecha_creacion                            AS fechaCreacion,
                ce.fecha_anulacion                           AS fechaAnulacion,
                ce.tercero_nombre                            AS terceroNombre,
                COALESCE(ce.total, 0)                        AS total,
                ce.motivo_anulacion                          AS motivoAnulacion,
                ce.anulado_por_usuario_id                    AS anuladoPor,
                u.nombre                                     AS anuladoNombre
            FROM comprobantes_egreso ce
            LEFT JOIN usuarios u ON u.codigo = ce.anulado_por_usuario_id
            WHERE ce.estado = 'ANULADO'
              AND DATE(ce.fecha_anulacion) BETWEEN :inicio AND :fin
            ORDER BY fechaCreacion DESC
            """, nativeQuery = true)
    List<Object[]> anulacionesEnPeriodo(@Param("inicio") LocalDate inicio,
                                        @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // CONCEPTOS ABIERTOS — uso por monto
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT
                ca.id                              AS conceptoId,
                ca.descripcion                     AS descripcion,
                ca.tipo                            AS tipo,
                cc.codigo                          AS cuentaContableCodigo,
                cc.nombre                          AS cuentaContableNombre,
                COALESCE(SUM(uso.cantidad), 0)     AS cantidadUsos,
                COALESCE(SUM(uso.total),    0)     AS totalMovido
            FROM conceptos_abiertos ca
            LEFT JOIN cuentas_contables cc ON cc.cuenta_id = ca.cuenta_contable_id
            LEFT JOIN (
                SELECT concepto_abierto_id AS cid,
                       1 AS cantidad,
                       COALESCE(total, 0) AS total,
                       fecha_creacion
                FROM recibos_caja
                WHERE estado != 'ANULADO'
                  AND concepto_abierto_id IS NOT NULL
                  AND DATE(fecha_creacion) BETWEEN :inicio AND :fin
                UNION ALL
                SELECT concepto_abierto_id AS cid,
                       1 AS cantidad,
                       COALESCE(total, 0) AS total,
                       fecha_creacion
                FROM comprobantes_egreso
                WHERE estado != 'ANULADO'
                  AND concepto_abierto_id IS NOT NULL
                  AND DATE(fecha_creacion) BETWEEN :inicio AND :fin
            ) uso ON uso.cid = ca.id
            WHERE (:tipoFiltro IS NULL OR ca.tipo = :tipoFiltro OR :tipoFiltro = 'TODOS')
            GROUP BY ca.id, ca.descripcion, ca.tipo, cc.codigo, cc.nombre
            HAVING totalMovido > 0
            ORDER BY totalMovido DESC
            """, nativeQuery = true)
    List<Object[]> conceptosAbiertosUso(@Param("inicio") LocalDate inicio,
                                        @Param("fin") LocalDate fin,
                                        @Param("tipoFiltro") String tipoFiltro);

    // ══════════════════════════════════════════════════════════════
    // DEVOLUCIONES DETALLADAS
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT
                d.id                                          AS devolucionId,
                d.fecha_creacion                              AS fechaCreacion,
                v.numero_venta                                AS numeroVenta,
                COALESCE(t.razon_social,
                         CONCAT(COALESCE(t.nombre_1,''),' ',COALESCE(t.apellido_1,''))) AS clienteNombre,
                c.nombre                                      AS cajeroNombre,
                COALESCE(d.total_neto, 0)                     AS totalNeto,
                (SELECT COALESCE(SUM(dd.cantidad_devuelta), 0)
                   FROM detalles_devolucion_venta dd
                  WHERE dd.devolucion_id = d.id)             AS cantidadItems,
                d.motivo                                      AS motivo
            FROM devoluciones_venta d
            LEFT JOIN ventas v ON v.id = d.venta_id
            LEFT JOIN terceros t ON t.tercero_id = v.cliente_id
            LEFT JOIN cajeros c ON c.cajero_id = COALESCE(d.cajero_id, v.cajero_id)
            WHERE d.fecha_creacion BETWEEN :inicio AND :fin
            ORDER BY d.fecha_creacion DESC
            """, nativeQuery = true)
    List<Object[]> devolucionesDetalladas(@Param("inicio") LocalDate inicio,
                                          @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // TICKET PROMEDIO POR CAJERO
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT
                c.nombre                                       AS etiqueta,
                COUNT(v.id)                                    AS cantidadVentas,
                COALESCE(SUM(v.total_venta), 0)                AS totalVendido,
                (SELECT COALESCE(SUM(dv.cantidad), 0)
                   FROM detalles_venta dv
                   JOIN ventas v2 ON v2.id = dv.venta_id
                  WHERE v2.cajero_id = c.cajero_id
                    AND v2.estado = 'COMPLETADA'
                    AND v2.fecha_emision BETWEEN :inicio AND :fin) AS unidadesTotales
            FROM ventas v
            JOIN cajeros c ON c.cajero_id = v.cajero_id
            WHERE v.estado = 'COMPLETADA'
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY c.cajero_id, c.nombre
            ORDER BY totalVendido DESC
            """, nativeQuery = true)
    List<Object[]> ticketPromedioPorCajero(@Param("inicio") LocalDate inicio,
                                           @Param("fin") LocalDate fin);

    @Query(value = """
            SELECT
                ve.nombre                                      AS etiqueta,
                COUNT(v.id)                                    AS cantidadVentas,
                COALESCE(SUM(v.total_venta), 0)                AS totalVendido,
                (SELECT COALESCE(SUM(dv.cantidad), 0)
                   FROM detalles_venta dv
                   JOIN ventas v2 ON v2.id = dv.venta_id
                  WHERE v2.vendedor_id = ve.vendedor_id
                    AND v2.estado = 'COMPLETADA'
                    AND v2.fecha_emision BETWEEN :inicio AND :fin) AS unidadesTotales
            FROM ventas v
            JOIN vendedores ve ON ve.vendedor_id = v.vendedor_id
            WHERE v.estado = 'COMPLETADA'
              AND v.fecha_emision BETWEEN :inicio AND :fin
            GROUP BY ve.vendedor_id, ve.nombre
            ORDER BY totalVendido DESC
            """, nativeQuery = true)
    List<Object[]> ticketPromedioPorVendedor(@Param("inicio") LocalDate inicio,
                                             @Param("fin") LocalDate fin);

    // ══════════════════════════════════════════════════════════════
    // CARTERA POR ANTIGÜEDAD (aging buckets)
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT
                CASE
                    WHEN DATEDIFF(CURDATE(), fecha_vencimiento) <= 0  THEN 'Al día'
                    WHEN DATEDIFF(CURDATE(), fecha_vencimiento) BETWEEN 1 AND 30  THEN '1-30 días'
                    WHEN DATEDIFF(CURDATE(), fecha_vencimiento) BETWEEN 31 AND 60 THEN '31-60 días'
                    WHEN DATEDIFF(CURDATE(), fecha_vencimiento) BETWEEN 61 AND 90 THEN '61-90 días'
                    ELSE '>90 días'
                END AS rangoEdad,
                CASE
                    WHEN DATEDIFF(CURDATE(), fecha_vencimiento) <= 0  THEN 0
                    WHEN DATEDIFF(CURDATE(), fecha_vencimiento) BETWEEN 1 AND 30  THEN 1
                    WHEN DATEDIFF(CURDATE(), fecha_vencimiento) BETWEEN 31 AND 60 THEN 2
                    WHEN DATEDIFF(CURDATE(), fecha_vencimiento) BETWEEN 61 AND 90 THEN 3
                    ELSE 4
                END AS ordenRango,
                COUNT(*)                AS cantidad,
                COALESCE(SUM(saldo), 0) AS totalSaldo
            FROM cuentas_por_cobrar
            WHERE estado IN ('PENDIENTE','PARCIAL')
              AND saldo > 0
            GROUP BY rangoEdad, ordenRango
            ORDER BY ordenRango ASC
            """, nativeQuery = true)
    List<Object[]> carteraAging();

    // ══════════════════════════════════════════════════════════════
    // PRODUCTOS SIN MOVIMIENTO (inventario inmovilizado)
    // ══════════════════════════════════════════════════════════════

    @Query(value = """
            SELECT
                pv.producto_variantes_id                                       AS productoVarianteId,
                pv.sku                                                         AS sku,
                IF(pv.predeterminada=0,
                   CONCAT(p.descripcion,' - ',pv.referencia_variantes),
                   p.descripcion)                                              AS descripcion,
                COALESCE(l.descripcion, 'Sin línea')                           AS linea,
                COALESCE((SELECT SUM(e.existencia) FROM existencias e
                           WHERE e.producto_variantes_id = pv.producto_variantes_id), 0) AS existencia,
                p.costo                                                        AS costo,
                COALESCE((SELECT SUM(e.existencia) FROM existencias e
                           WHERE e.producto_variantes_id = pv.producto_variantes_id), 0) * p.costo AS valorInmovilizado,
                p.fecha_ultima_venta                                           AS fechaUltimaVenta,
                p.fecha_ultima_compra                                          AS fechaUltimaCompra,
                CASE WHEN p.fecha_ultima_venta IS NULL THEN NULL
                     ELSE DATEDIFF(CURDATE(), p.fecha_ultima_venta)
                END                                                            AS diasSinVender
            FROM producto_variantes pv
            JOIN productos p ON p.producto_id = pv.producto_id
            LEFT JOIN lineas l ON l.linea_id = p.linea_id
            WHERE pv.activo = 1
              AND p.estado = 'ACTIVO'
              AND NOT EXISTS (
                  SELECT 1 FROM detalles_venta dv
                    JOIN ventas v ON v.id = dv.venta_id
                   WHERE dv.codigo_producto = pv.sku
                     AND v.estado = 'COMPLETADA'
                     AND v.fecha_emision BETWEEN :inicio AND :fin
              )
            ORDER BY valorInmovilizado DESC
            LIMIT :topN
            """, nativeQuery = true)
    List<Object[]> productosSinMovimiento(@Param("inicio") LocalDate inicio,
                                          @Param("fin") LocalDate fin,
                                          @Param("topN") int topN);
}

