-- Saldo a favor consumido en recibos de caja / comprobantes de egreso (para incluirlo en la
-- partida doble como anticipos 2805/1330 y reponerlo al anular). Aplicar por cada schema/tenant.
ALTER TABLE recibos_caja
    ADD COLUMN saldo_favor_usado DECIMAL(18,2) NOT NULL DEFAULT 0;
ALTER TABLE comprobantes_egreso
    ADD COLUMN saldo_favor_usado DECIMAL(18,2) NOT NULL DEFAULT 0;
