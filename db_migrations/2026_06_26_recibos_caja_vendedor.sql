-- Agrega columna vendedor_id a recibos_caja para el módulo de recibos de caja
-- Relaciona con la tabla vendedores (opcional: sin FK para evitar restricciones al borrar vendedores)

ALTER TABLE cavsystems.recibos_caja
    ADD COLUMN vendedor_id INT NULL AFTER cajero_id,
    ADD INDEX idx_recibos_caja_vendedor (vendedor_id);
