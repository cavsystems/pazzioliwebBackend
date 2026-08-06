-- V6: Agrega columna vendedor_id a la tabla terceros (nullable)
ALTER TABLE terceros
    ADD COLUMN IF NOT EXISTS vendedor_id INT NULL;
