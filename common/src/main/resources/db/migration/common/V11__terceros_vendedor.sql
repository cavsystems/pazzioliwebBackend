-- Agrega terceros.vendedor_id (vendedor asignado por defecto al tercero). Confirmado como cambio
-- terminado y querido (módulo de vendedores/terceros). Columna simple, sin FK ni índice —tal como
-- quedó definida donde se originó el cambio.
--
-- MySQL no soporta "ALTER TABLE ... ADD COLUMN IF NOT EXISTS" (probado en 8.0.45: error de sintaxis);
-- se usa el mismo patrón de information_schema + PREPARE/EXECUTE que V8/V9/V10.
SET @col := (SELECT COUNT(*) FROM information_schema.COLUMNS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'terceros' AND COLUMN_NAME = 'vendedor_id');
SET @sql := IF(@col = 0, 'ALTER TABLE terceros ADD COLUMN vendedor_id INT DEFAULT NULL', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
