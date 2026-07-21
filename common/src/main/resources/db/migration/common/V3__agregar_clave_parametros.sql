-- Migración V3: Agregar columna clave a la tabla parametros
ALTER TABLE parametros ADD COLUMN clave VARCHAR(100);
