-- Monto realmente debitado a CxP en la nota débito (para reponer exacto al anular).
-- Aplicar por cada schema/tenant.
ALTER TABLE devoluciones_compra
    ADD COLUMN monto_cxp_debitado DECIMAL(18,2) NOT NULL DEFAULT 0;
