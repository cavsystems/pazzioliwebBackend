-- Migración aplicable: lo que REALMENTE falta en cavsystems al 2026-05-25
USE cavsystems;

-- 1. Empresa: campos fiscales DIAN
ALTER TABLE empresa
    ADD COLUMN responsabilidad_fiscal VARCHAR(100),
    ADD COLUMN tipo_contribuyente     VARCHAR(30),
    ADD COLUMN gran_contribuyente     TINYINT(1) NOT NULL DEFAULT 0,
    ADD COLUMN autorretenedor         TINYINT(1) NOT NULL DEFAULT 0,
    ADD COLUMN responsable_iva        TINYINT(1) NOT NULL DEFAULT 1;

-- 2. Ordenes de compra: retenciones aplicadas
ALTER TABLE ordenes_compra
    ADD COLUMN retefuente DECIMAL(18,2) NOT NULL DEFAULT 0,
    ADD COLUMN reteiva    DECIMAL(18,2) NOT NULL DEFAULT 0,
    ADD COLUMN reteica    DECIMAL(18,2) NOT NULL DEFAULT 0;

-- 3. PUC: cuentas de retenciones por pagar (faltaban)
INSERT INTO cuentas_contables (codigo, nombre, tipo, nivel, es_movimiento, requiere_tercero, estado, fecha_creacion) VALUES
  ('236505', 'Retención en la fuente', 'PASIVO', 5, 1, 1, 'ACTIVO', NOW()),
  ('236540', 'Retención de IVA',       'PASIVO', 5, 1, 1, 'ACTIVO', NOW()),
  ('236570', 'Retención de ICA',       'PASIVO', 5, 1, 1, 'ACTIVO', NOW());
