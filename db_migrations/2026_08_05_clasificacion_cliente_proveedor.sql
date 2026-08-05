-- ─────────────────────────────────────────────────────────────────────────────
-- Restaurar clasificaciones 'Empleado' y 'Cliente-Proveedor' en tenants EXISTENTES.
-- (Los tenants ya creados no corren Flyway; el template se repara solo con
--  V7__clasificaciones_cliente_proveedor.sql en el arranque.)
--
-- Ejecutar UNA VEZ POR SCHEMA de tenant, reemplazando el USE:
--   mysql -u root -p < 2026_08_05_clasificacion_cliente_proveedor.sql
-- ─────────────────────────────────────────────────────────────────────────────
USE cavsystems;  -- ← cambiar por el schema del tenant

UPDATE clasificaciones_terceros
   SET nombre = 'Cliente-Proveedor'
 WHERE UPPER(nombre) IN ('CLIENTE-PROVEDPR', 'CLIENTE-PROVEDOR');

INSERT INTO clasificaciones_terceros (nombre)
SELECT 'Empleado' FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM clasificaciones_terceros WHERE UPPER(nombre) = 'EMPLEADO');

INSERT INTO clasificaciones_terceros (nombre)
SELECT 'Cliente-Proveedor' FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM clasificaciones_terceros WHERE UPPER(nombre) = 'CLIENTE-PROVEEDOR');
