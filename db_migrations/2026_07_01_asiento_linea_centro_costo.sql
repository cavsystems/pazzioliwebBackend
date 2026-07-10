-- ═══════════════════════════════════════════════════════════════════════
-- Centro de costo por línea de asiento contable
-- Aplicar en CADA schema de tenant (cavsystems, etc.). NO en 'administrador'.
-- Aditivo: columna nullable → no afecta líneas ni flujos existentes.
-- ═══════════════════════════════════════════════════════════════════════
USE cavsystems;

ALTER TABLE asientos_contables_lineas
    ADD COLUMN centro_costo_id INT NULL;
