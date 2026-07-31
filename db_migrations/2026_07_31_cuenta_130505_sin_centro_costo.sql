-- ══════════════════════════════════════════════════════════════════
-- La cuenta 130505 (Clientes nacionales) estaba marcada con
-- requiere_centro_costo=1: el asiento automático de TODA venta a crédito
-- fallaba ("la cuenta exige centro de costo, pero la línea no lo trae")
-- y quedaba en asientos_fallidos — la venta y la factura electrónica
-- salían bien, pero sin asiento contable.
-- El flujo POS no maneja centros de costo, así que la exigencia se quita.
--
-- Ejecutar POR TENANT (sin schema quemado):
--     mysql -u root -p <tenant> < 2026_07_31_cuenta_130505_sin_centro_costo.sql
-- Después de aplicar: regenerar los asientos pendientes con
--     POST /api/ventas/{id}/regenerar-asiento   (ids en GET /api/asientos-fallidos)
-- ══════════════════════════════════════════════════════════════════
UPDATE cuentas_contables SET requiere_centro_costo = 0
 WHERE codigo = '130505' AND requiere_centro_costo = 1;
