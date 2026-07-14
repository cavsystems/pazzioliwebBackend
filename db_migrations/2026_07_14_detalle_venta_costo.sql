-- Costo unitario persistido en el detalle de venta (COGS real para reversar en devoluciones).
-- Aplicar por cada schema/tenant.
ALTER TABLE detalles_venta
    ADD COLUMN costo_unitario DECIMAL(18,2) NULL;
