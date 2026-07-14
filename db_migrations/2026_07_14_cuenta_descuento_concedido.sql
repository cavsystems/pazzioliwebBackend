-- Cuenta para el DESCUENTO comercial condicionado CONCEDIDO a clientes (pronto pago),
-- usado por el Recibo de Caja. Es un GASTO financiero (5305), no un menor ingreso.
-- Antes se debitaba a 421040 (cuenta de INGRESO, para descuentos RECIBIDOS de proveedores).
-- Idempotente: solo inserta si no existe.
INSERT INTO cuentas_contables (codigo, nombre, tipo, nivel, padre_id, es_movimiento, estado, requiere_tercero, requiere_documento_cruce, requiere_centro_costo)
SELECT '530535', 'Descuentos comerciales condicionados (concedidos)', 'GASTO', 6,
       (SELECT cuenta_id FROM (SELECT cuenta_id FROM cuentas_contables WHERE codigo = '5305' LIMIT 1) AS p),
       1, 'ACTIVO', 0, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM (SELECT codigo FROM cuentas_contables WHERE codigo = '530535') AS x);
