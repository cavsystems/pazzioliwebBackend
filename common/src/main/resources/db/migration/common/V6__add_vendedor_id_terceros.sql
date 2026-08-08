-- V6: Agrega columna vendedor_id a la tabla terceros (nullable)
--
-- MySQL no soporta "ALTER TABLE ... ADD COLUMN IF NOT EXISTS" (probado en 8.0: error de
-- sintaxis 1064) — la sintaxis original de este archivo nunca pudo ejecutarse de verdad
-- contra un MySQL real. Se reescribe con el mismo patrón de information_schema +
-- PREPARE/EXECUTE que V8/V9/V10/V11, ya usado en el resto de migraciones de este proyecto
-- justamente para poder aplicarse de forma idempotente contra tenants con datos reales.
SET @col := (SELECT COUNT(*) FROM information_schema.COLUMNS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'terceros' AND COLUMN_NAME = 'vendedor_id');
SET @sql := IF(@col = 0, 'ALTER TABLE terceros ADD COLUMN vendedor_id INT NULL', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
