-- Agrega retenciones a cuentas_por_pagar para auto-cálculo en Comprobante de Egreso
ALTER TABLE cuentas_por_pagar
    ADD COLUMN IF NOT EXISTS retefuente DECIMAL(15,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS reteiva    DECIMAL(15,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS reteica    DECIMAL(15,2) NOT NULL DEFAULT 0;
