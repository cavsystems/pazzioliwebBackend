-- Migración para estandarizar fecha_creacion a TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
-- Solo para la tabla ventas (cambio específico solicitado por el usuario)
USE cavsystems;

-- Ventas
ALTER TABLE ventas
    MODIFY COLUMN fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
