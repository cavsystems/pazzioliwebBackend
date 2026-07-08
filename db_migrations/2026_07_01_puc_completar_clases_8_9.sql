-- ═══════════════════════════════════════════════════════════════════════
-- PUC — completar clases faltantes (8 y 9) + reclasificar costos (6 y 7)
-- Aplicar en CADA schema de tenant (cavsystems, etc.). NO en 'administrador'.
--
-- Idempotente:
--   • Sección A usa INSERT IGNORE (codigo es UNIQUE) → NO duplica ni toca
--     ninguna de las cuentas existentes; solo agrega las que faltan.
--   • Sección B (opcional) reclasifica 6/7. Es segura: los reportes P&G y
--     Balance agrupan por PREFIJO de código, no por 'tipo', y la naturaleza
--     de COSTO / COSTO_PRODUCCION sigue siendo DÉBITO (igual que GASTO).
-- ═══════════════════════════════════════════════════════════════════════
USE cavsystems;

-- ─────────────────────────────────────────────────────────────────────
-- SECCIÓN A — Agregar clase 8 (Cuentas de orden deudoras) y
--             clase 9 (Cuentas de orden acreedoras). No afectan reportes.
--   nivel: 1=clase, 2=grupo, 3=cuenta, 4=subcuenta
--   es_movimiento: 0 en agrupadoras, 1 en subcuentas hoja (convención actual)
-- ─────────────────────────────────────────────────────────────────────
INSERT IGNORE INTO cuentas_contables
    (codigo, nombre, tipo, nivel, es_movimiento, requiere_tercero, requiere_documento_cruce, estado, fecha_creacion)
VALUES
    -- Clase 8: CUENTAS DE ORDEN DEUDORAS
    ('8',      'CUENTAS DE ORDEN DEUDORAS',                 'ORDEN_DEUDORAS', 1, 0, 0, 0, 'ACTIVO', NOW()),
    ('81',     'DERECHOS CONTINGENTES',                     'ORDEN_DEUDORAS', 2, 0, 0, 0, 'ACTIVO', NOW()),
    ('8105',   'BIENES Y VALORES ENTREGADOS EN CUSTODIA',   'ORDEN_DEUDORAS', 3, 0, 0, 0, 'ACTIVO', NOW()),
    ('810505', 'Bienes y valores entregados en custodia',   'ORDEN_DEUDORAS', 4, 1, 0, 0, 'ACTIVO', NOW()),
    ('8110',   'BIENES Y VALORES ENTREGADOS EN GARANTIA',   'ORDEN_DEUDORAS', 3, 0, 0, 0, 'ACTIVO', NOW()),
    ('811005', 'Bienes y valores entregados en garantia',   'ORDEN_DEUDORAS', 4, 1, 0, 0, 'ACTIVO', NOW()),
    ('8120',   'LITIGIOS Y/O DEMANDAS',                     'ORDEN_DEUDORAS', 3, 0, 0, 0, 'ACTIVO', NOW()),
    ('812005', 'Litigios y/o demandas',                     'ORDEN_DEUDORAS', 4, 1, 0, 0, 'ACTIVO', NOW()),
    ('83',     'DEUDORAS DE CONTROL',                       'ORDEN_DEUDORAS', 2, 0, 0, 0, 'ACTIVO', NOW()),
    ('8315',   'PROPIEDADES PLANTA Y EQUIPO TOTALMENTE DEPRECIADAS', 'ORDEN_DEUDORAS', 3, 0, 0, 0, 'ACTIVO', NOW()),
    ('831505', 'Propiedad planta y equipo totalmente depreciada',   'ORDEN_DEUDORAS', 4, 1, 0, 0, 'ACTIVO', NOW()),
    ('84',     'DERECHOS CONTINGENTES POR CONTRA (CR)',     'ORDEN_DEUDORAS', 2, 0, 0, 0, 'ACTIVO', NOW()),
    ('8405',   'Derechos contingentes por contra',          'ORDEN_DEUDORAS', 3, 1, 0, 0, 'ACTIVO', NOW()),

    -- Clase 9: CUENTAS DE ORDEN ACREEDORAS
    ('9',      'CUENTAS DE ORDEN ACREEDORAS',               'ORDEN_ACREEDORAS', 1, 0, 0, 0, 'ACTIVO', NOW()),
    ('91',     'RESPONSABILIDADES CONTINGENTES',            'ORDEN_ACREEDORAS', 2, 0, 0, 0, 'ACTIVO', NOW()),
    ('9105',   'BIENES Y VALORES RECIBIDOS EN CUSTODIA',    'ORDEN_ACREEDORAS', 3, 0, 0, 0, 'ACTIVO', NOW()),
    ('910505', 'Bienes y valores recibidos en custodia',    'ORDEN_ACREEDORAS', 4, 1, 0, 0, 'ACTIVO', NOW()),
    ('9110',   'BIENES Y VALORES RECIBIDOS EN GARANTIA',    'ORDEN_ACREEDORAS', 3, 0, 0, 0, 'ACTIVO', NOW()),
    ('911005', 'Bienes y valores recibidos en garantia',    'ORDEN_ACREEDORAS', 4, 1, 0, 0, 'ACTIVO', NOW()),
    ('9120',   'LITIGIOS Y/O DEMANDAS',                     'ORDEN_ACREEDORAS', 3, 0, 0, 0, 'ACTIVO', NOW()),
    ('912005', 'Litigios y/o demandas',                     'ORDEN_ACREEDORAS', 4, 1, 0, 0, 'ACTIVO', NOW()),
    ('93',     'ACREEDORAS DE CONTROL',                     'ORDEN_ACREEDORAS', 2, 0, 0, 0, 'ACTIVO', NOW()),
    ('9395',   'OTRAS CUENTAS DE ORDEN ACREEDORAS DE CONTROL', 'ORDEN_ACREEDORAS', 3, 0, 0, 0, 'ACTIVO', NOW()),
    ('939505', 'Otras cuentas de orden acreedoras de control', 'ORDEN_ACREEDORAS', 4, 1, 0, 0, 'ACTIVO', NOW()),
    ('94',     'RESPONSABILIDADES CONTINGENTES POR CONTRA (DB)', 'ORDEN_ACREEDORAS', 2, 0, 0, 0, 'ACTIVO', NOW()),
    ('9405',   'Responsabilidades contingentes por contra', 'ORDEN_ACREEDORAS', 3, 1, 0, 0, 'ACTIVO', NOW());

-- ─────────────────────────────────────────────────────────────────────
-- SECCIÓN B (OPCIONAL) — Reclasificar clases 6 y 7 al tipo correcto.
--   Seguro: reportes agrupan por prefijo de código, NO por 'tipo'; y la
--   naturaleza (DÉBITO) no cambia. Solo mejora el badge del catálogo.
--   Si prefieres dejar 6/7 como GASTO, no ejecutes estas dos líneas.
-- ─────────────────────────────────────────────────────────────────────
UPDATE cuentas_contables SET tipo = 'COSTO'            WHERE codigo LIKE '6%';
UPDATE cuentas_contables SET tipo = 'COSTO_PRODUCCION' WHERE codigo LIKE '7%';
