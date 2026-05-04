-- ============================================================
-- Migración v2: Recibos de Caja / Comprobantes de Egreso
--   * Catálogo de cuentas contables (REUSA tabla existente cuentas_contables)
--   * Catálogo de conceptos abiertos (RECIBO/EGRESO)
--   * Tipos en métodos de pago (RECIBO/EGRESO/VENTA)
--   * Bug: saldo en cuentas_por_pagar (faltaba)
--   * Anulación de recibos / egresos
-- ============================================================
-- NOTA: La parametrización por empresa la garantiza el multi-tenant
--       (header X-TenantID), por lo que NO se agregan columnas
--       empresa_id; cada empresa usa su propia base.
-- ============================================================

-- 1) Cuentas contables (tabla ya existe; agregar columnas faltantes) -------
ALTER TABLE cavsystems.cuentas_contables ADD COLUMN estado VARCHAR(15) NOT NULL DEFAULT 'ACTIVO';
ALTER TABLE cavsystems.cuentas_contables ADD COLUMN fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- 2) Conceptos abiertos ----------------------------------------------------
CREATE TABLE IF NOT EXISTS cavsystems.conceptos_abiertos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(200) NOT NULL,
  tipo VARCHAR(15) NOT NULL,
  cuenta_contable_id INT NOT NULL,
  tercero_id INT NULL,
  beneficiario_nombre VARCHAR(200) NULL,
  beneficiario_documento VARCHAR(50) NULL,
  info_extra TEXT NULL,
  estado VARCHAR(15) NOT NULL DEFAULT 'ACTIVO',
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ca_cuenta_contable FOREIGN KEY (cuenta_contable_id)
      REFERENCES cuentas_contables(cuenta_id),
  CONSTRAINT fk_ca_tercero FOREIGN KEY (tercero_id)
      REFERENCES terceros(tercero_id)
);

-- 3) Tipos en métodos de pago (CSV: "RECIBO,EGRESO,VENTA") -----------------
ALTER TABLE cavsystems.metodos_pago ADD COLUMN tipos VARCHAR(100) NOT NULL DEFAULT 'RECIBO,EGRESO,VENTA';

-- 4) Bug fix: saldo en cuentas_por_pagar -----------------------------------
ALTER TABLE cavsystems.cuentas_por_pagar ADD COLUMN saldo DECIMAL(15,2) NULL;
UPDATE cavsystems.cuentas_por_pagar SET saldo = valor_neto WHERE saldo IS NULL;
ALTER TABLE cavsystems.cuentas_por_pagar MODIFY COLUMN saldo DECIMAL(15,2) NOT NULL DEFAULT 0;

-- 5) Recibos de caja: nuevos campos ----------------------------------------
ALTER TABLE cavsystems.recibos_caja ADD COLUMN concepto_abierto_id BIGINT NULL;
ALTER TABLE cavsystems.recibos_caja ADD COLUMN cuenta_contable_id INT NULL;
ALTER TABLE cavsystems.recibos_caja ADD COLUMN beneficiario_nombre VARCHAR(200) NULL;
ALTER TABLE cavsystems.recibos_caja ADD COLUMN beneficiario_documento VARCHAR(50) NULL;
ALTER TABLE cavsystems.recibos_caja ADD COLUMN motivo_anulacion TEXT NULL;
ALTER TABLE cavsystems.recibos_caja ADD COLUMN fecha_anulacion DATETIME NULL;
ALTER TABLE cavsystems.recibos_caja ADD COLUMN anulado_por_usuario_id INT NULL;

-- 6) Comprobantes de egreso: nuevos campos ---------------------------------
ALTER TABLE cavsystems.comprobantes_egreso ADD COLUMN concepto_abierto_id BIGINT NULL;
ALTER TABLE cavsystems.comprobantes_egreso ADD COLUMN cuenta_contable_id INT NULL;
ALTER TABLE cavsystems.comprobantes_egreso ADD COLUMN beneficiario_nombre VARCHAR(200) NULL;
ALTER TABLE cavsystems.comprobantes_egreso ADD COLUMN beneficiario_documento VARCHAR(50) NULL;
ALTER TABLE cavsystems.comprobantes_egreso ADD COLUMN motivo_anulacion TEXT NULL;
ALTER TABLE cavsystems.comprobantes_egreso ADD COLUMN fecha_anulacion DATETIME NULL;
ALTER TABLE cavsystems.comprobantes_egreso ADD COLUMN anulado_por_usuario_id INT NULL;

-- 7) Permiso para anular ----------------------------------------------------
INSERT INTO cavsystems.permisos (nombre)
SELECT 'Anular comprobantes' FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM cavsystems.permisos WHERE nombre = 'Anular comprobantes');
