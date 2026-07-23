-- Migración V3: Agregar columna clave a la tabla parametros
-- Idempotente: solo agrega la columna si no existe
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'parametros'
    AND COLUMN_NAME = 'clave'
);

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE parametros ADD COLUMN clave VARCHAR(100)',
    'SELECT ''Columna clave ya existe en parametros'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
