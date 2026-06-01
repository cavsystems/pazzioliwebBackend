-- ─────────────────────────────────────────────────────────────────────────
-- Retenciones SUFRIDAS en ventas (el cliente nos practica retención).
-- total_venta sigue siendo el BRUTO (gravada + IVA). El cliente paga el neto
-- (= bruto − retenciones) y la diferencia se registra como anticipo de
-- impuestos (PUC 1355: 135515 retefuente, 135517 reteIVA, 135518 reteICA).
--
-- MySQL 8.0 no soporta "ADD COLUMN IF NOT EXISTS": ejecutar una sola vez.
-- ─────────────────────────────────────────────────────────────────────────
USE cavsystems;

ALTER TABLE ventas
    ADD COLUMN retefuente DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    ADD COLUMN reteiva    DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    ADD COLUMN reteica    DECIMAL(18,2) NOT NULL DEFAULT 0.00;
