-- Marca de cuentas que exigen centro de costo al digitar el asiento.
-- Ejecutar manualmente en la base de datos de CADA tenant (menos 'administrador').
ALTER TABLE cuentas_contables ADD COLUMN requiere_centro_costo TINYINT(1) NOT NULL DEFAULT 0;
