-- ============================================================
-- FIX: facturas.caja_id debe referenciar cajeros.cajero_id
--       en vez de cajas.caja_id
-- EJECUTADO: 2026-04-26
-- ============================================================

-- 1. Eliminar el FK actual que apuntaba a cajas
ALTER TABLE facturas DROP FOREIGN KEY facturas_ibfk_5;

-- 2. Limpiar datos huérfanos (caja_id que no existen en cajeros)
UPDATE facturas SET caja_id = NULL
WHERE caja_id IS NOT NULL
  AND caja_id NOT IN (SELECT cajero_id FROM cajeros);

-- 3. Crear el nuevo FK apuntando a cajeros
ALTER TABLE facturas
  ADD CONSTRAINT facturas_fk_cajero
  FOREIGN KEY (caja_id) REFERENCES cajeros (cajero_id);

