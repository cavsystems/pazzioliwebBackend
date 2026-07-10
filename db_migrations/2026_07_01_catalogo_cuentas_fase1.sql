-- ═══════════════════════════════════════════════════════════════════════
-- Catálogo de cuentas — Fase 1
-- Aplicar en CADA schema de tenant (cavsystems, y demás). NO en 'administrador'.
-- Todos los cambios son ADITIVOS: no alteran datos existentes.
-- ═══════════════════════════════════════════════════════════════════════
USE cavsystems;

-- 1. Ampliar los tipos de cuenta a las 9 clases del PUC.
--    Los valores actuales (ACTIVO..GASTO) se conservan intactos.
ALTER TABLE cuentas_contables
    MODIFY COLUMN tipo ENUM(
        'ACTIVO',            -- 1
        'PASIVO',            -- 2
        'PATRIMONIO',        -- 3
        'INGRESO',           -- 4
        'GASTO',             -- 5
        'COSTO',             -- 6  Costos
        'COSTO_PRODUCCION',  -- 7  Costos de producción
        'ORDEN_DEUDORAS',    -- 8  Cuentas de orden deudoras
        'ORDEN_ACREEDORAS'   -- 9  Cuentas de orden acreedoras
    ) NOT NULL;

-- 2. Nuevo flag "documento de cruce", independiente de requiere_tercero.
--    Por defecto 0 → ninguna cuenta existente cambia su comportamiento.
ALTER TABLE cuentas_contables
    ADD COLUMN requiere_documento_cruce TINYINT(1) NOT NULL DEFAULT 0;

-- 3. Documento de cruce en las líneas de asiento (usado por asientos manuales
--    contra cuentas de cartera/proveedores). Nullable → no afecta líneas previas.
ALTER TABLE asientos_contables_lineas
    ADD COLUMN documento_cruce VARCHAR(100) NULL;
