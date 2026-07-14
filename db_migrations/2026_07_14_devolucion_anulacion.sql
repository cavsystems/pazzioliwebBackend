-- Campos de anulación para devoluciones de venta.
-- Aplicar por cada schema/tenant. Idempotente si se corre con cuidado (revisar antes de repetir).
ALTER TABLE devoluciones_venta
    ADD COLUMN motivo_anulacion  VARCHAR(300) NULL,
    ADD COLUMN fecha_anulacion   DATE         NULL,
    ADD COLUMN usuario_anulacion VARCHAR(100) NULL;
