-- V5: Índices de rendimiento para compras, ingreso y legalización
-- Seguro para nuevos tenants (clonados desde template) y existentes (usa IF NOT EXISTS vía INFORMATION_SCHEMA).
-- Cada llamada a sp_add_idx verifica: índice no existe Y columna existe → crea; de lo contrario no hace nada.

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_add_idx$$

CREATE PROCEDURE sp_add_idx(
    IN p_tbl       VARCHAR(100),   -- nombre de la tabla
    IN p_idx       VARCHAR(100),   -- nombre del índice a crear
    IN p_check_col VARCHAR(100),   -- primera columna (usada para verificar existencia)
    IN p_col_expr  TEXT            -- expresión completa de columnas para CREATE INDEX
)
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME   = p_tbl
          AND INDEX_NAME   = p_idx
    ) AND EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME   = p_tbl
          AND COLUMN_NAME  = p_check_col
    ) THEN
        SET @_sql = CONCAT('CREATE INDEX ', p_idx, ' ON ', p_tbl, ' (', p_col_expr, ')');
        PREPARE _s FROM @_sql;
        EXECUTE _s;
        DEALLOCATE PREPARE _s;
    END IF;
END$$

DELIMITER ;

-- ─────────────────────────────────────────────────────────────────────────────
-- ordenes_compra
-- ─────────────────────────────────────────────────────────────────────────────

-- Filtros por estado (PENDIENTE, RECIBIDA, ANULADA…) — buscarConFiltros, findOrdenesPendientes
CALL sp_add_idx('ordenes_compra', 'idx_oc_estado',           'estado',       'estado');

-- Rangos de fecha — buscarConFiltros, historial, sumTotalByPeriodo, métricas
CALL sp_add_idx('ordenes_compra', 'idx_oc_fecha_emision',    'fecha_emision','fecha_emision');

-- Combinado estado + fecha — cubre la mayoría de filtros de la paginación principal
CALL sp_add_idx('ordenes_compra', 'idx_oc_estado_fecha',     'estado',       'estado, fecha_emision');

-- proveedor + estado — findOrdenesPendientesByProveedorId, findOrdenesPorProveedorYEstado
CALL sp_add_idx('ordenes_compra', 'idx_oc_proveedor_estado', 'proveedor_id', 'proveedor_id, estado');

-- bodega + fecha — sumTotalByPeriodo, countByPeriodo, countByEstadoAndPeriodo, findTopProveedores
CALL sp_add_idx('ordenes_compra', 'idx_oc_bodega_fecha',     'bodega_id',    'bodega_id, fecha_emision');

-- ─────────────────────────────────────────────────────────────────────────────
-- detalles_orden_compra
-- ─────────────────────────────────────────────────────────────────────────────

-- Búsqueda por código de producto (VARCHAR, sin FK → sin índice automático)
-- Usado al actualizar inventario por producto y en actualizarCostosYPrecios
CALL sp_add_idx('detalles_orden_compra', 'idx_doc_codigo_producto', 'codigo_producto', 'codigo_producto');

-- ─────────────────────────────────────────────────────────────────────────────
-- legalizaciones
-- ─────────────────────────────────────────────────────────────────────────────

-- Filtro por proveedor — obtenerLegalizacionesPorProveedor (no es FK, sin índice automático)
CALL sp_add_idx('legalizaciones', 'idx_leg_proveedor_id', 'proveedor_id', 'proveedor_id');

-- ─────────────────────────────────────────────────────────────────────────────

DROP PROCEDURE IF EXISTS sp_add_idx;
