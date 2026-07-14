-- Costo unitario persistido en el detalle de devolución de venta (consistencia GL/kardex al anular).
-- Aplicar por cada schema/tenant.
ALTER TABLE detalles_devolucion_venta
    ADD COLUMN costo_unitario DECIMAL(18,2) NOT NULL DEFAULT 0;
