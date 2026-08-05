-- ─────────────────────────────────────────────────────────────────────────────
-- Clasificaciones de tercero faltantes: 'Empleado' y 'Cliente-Proveedor'.
--
-- V2 las insertaba solo en instalaciones NUEVAS (y con el nombre mal escrito
-- 'Cliente-provedpr', que no coincide con el filtro del backend, que espera
-- 'CLIENTE-PROVEEDOR'). Como V2 ya está aplicada en el template, editarla no
-- re-ejecuta nada: esta migración repara el nombre e inserta las filas que
-- falten, de forma idempotente.
--
-- La búsqueda de compras/ventas (traerTercerosXFiltropro) matchea por NOMBRE:
--   tipo 1 (ventas)  → CLIENTE, CLIENTE-PROVEEDOR
--   tipo 2 (compras) → PROVEEDOR, CLIENTE-PROVEEDOR
-- por lo que el id exacto puede variar entre tenants sin afectar la lógica.
--
-- OJO: los tenants YA CREADOS no corren Flyway (solo el template). Para ellos
-- ejecutar este mismo SQL por schema — ver db_migrations/2026_08_05_clasificacion_cliente_proveedor.sql
-- ─────────────────────────────────────────────────────────────────────────────

-- Reparar el typo si la fila existe
UPDATE clasificaciones_terceros
   SET nombre = 'Cliente-Proveedor'
 WHERE UPPER(nombre) IN ('CLIENTE-PROVEDPR', 'CLIENTE-PROVEDOR');

-- Insertar 'Empleado' si no existe
INSERT INTO clasificaciones_terceros (nombre)
SELECT 'Empleado' FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM clasificaciones_terceros WHERE UPPER(nombre) = 'EMPLEADO');

-- Insertar 'Cliente-Proveedor' si no existe
INSERT INTO clasificaciones_terceros (nombre)
SELECT 'Cliente-Proveedor' FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM clasificaciones_terceros WHERE UPPER(nombre) = 'CLIENTE-PROVEEDOR');
