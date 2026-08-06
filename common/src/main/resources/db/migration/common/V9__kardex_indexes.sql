-- Portado de db_migrations/2026_07_31_kardex_indexes.sql
-- Índices optimizados para el query de kardex-reporte
-- Optimiza el endpoint: GET /api/inventario/movimientos/kardex-reporte
--
-- MySQL no soporta "CREATE INDEX ... IF NOT EXISTS" ni "ALTER TABLE ... ADD INDEX IF NOT EXISTS"
-- (probado en 8.0.45: error de sintaxis). Se usa el mismo patrón que V8 (chequeo contra
-- information_schema + PREPARE/EXECUTE) para que sea idempotente en cualquier tenant.

-- Índice compuesto para filtros principales del kardex
-- fecha_creacion (filtro rango + order by), producto_variante_id (filtro), bodega_id (filtro + join)
SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'kardex' AND INDEX_NAME = 'idx_kardex_fecha_variante_bodega');
SET @sql := IF(@idx = 0, 'CREATE INDEX idx_kardex_fecha_variante_bodega ON kardex (fecha_creacion, producto_variante_id, bodega_id)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Índice para búsqueda por variante de producto (filtro más frecuente)
SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'kardex' AND INDEX_NAME = 'idx_kardex_producto_variante');
SET @sql := IF(@idx = 0, 'CREATE INDEX idx_kardex_producto_variante ON kardex (producto_variante_id)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Índice para búsqueda por bodega (filtro + join)
SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'kardex' AND INDEX_NAME = 'idx_kardex_bodega');
SET @sql := IF(@idx = 0, 'CREATE INDEX idx_kardex_bodega ON kardex (bodega_id)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Índice para join con movimientos_inventario
SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'kardex' AND INDEX_NAME = 'idx_kardex_movimiento');
SET @sql := IF(@idx = 0, 'CREATE INDEX idx_kardex_movimiento ON kardex (movimiento_inventario_id)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Índice para ordenamiento por fecha de emisión (usado en ORDER BY)
SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'kardex' AND INDEX_NAME = 'idx_kardex_fecha_emision');
SET @sql := IF(@idx = 0, 'CREATE INDEX idx_kardex_fecha_emision ON kardex (fecha_emision)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Índice para filtro por tipo de movimiento (cuando se usa)
-- Nota: el archivo ad-hoc original referenciaba la columna `tipo`, que no existe en `kardex`
-- (la columna real es `tipo_movimiento`) — por eso este índice nunca se aplicó exitosamente
-- a mano en ningún tenant.
SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'kardex' AND INDEX_NAME = 'idx_kardex_tipo');
SET @sql := IF(@idx = 0, 'CREATE INDEX idx_kardex_tipo ON kardex (tipo_movimiento)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Índice compuesto para movimientos_inventario (join con kardex)
SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'movimientos_inventario' AND INDEX_NAME = 'idx_movimientos_inventario_comprobante');
SET @sql := IF(@idx = 0, 'CREATE INDEX idx_movimientos_inventario_comprobante ON movimientos_inventario (comprobante_id, consecutivo)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Índice para producto_variantes (join principal)
SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'producto_variantes' AND INDEX_NAME = 'idx_producto_variantes_producto');
SET @sql := IF(@idx = 0, 'CREATE INDEX idx_producto_variantes_producto ON producto_variantes (producto_id)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Índice para bodegas (join)
SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'bodegas' AND INDEX_NAME = 'idx_bodegas_codigo');
SET @sql := IF(@idx = 0, 'CREATE INDEX idx_bodegas_codigo ON bodegas (codigo)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
