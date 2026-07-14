-- Cuentas de contrapartida para deducciones en recaudo/pago (descuento pronto pago, averías, fletes).
-- Permiten que el mayor de CxC/CxP cierre exacto cuando el recibo/egreso trae esas deducciones.
-- Ejecutar manualmente en la base de datos de CADA tenant (menos 'administrador').

INSERT INTO cuentas_contables (codigo,nombre,tipo,nivel,es_movimiento,estado,fecha_creacion,requiere_tercero,requiere_documento_cruce)
SELECT '421040','Descuentos comerciales condicionados (pronto pago)','INGRESO',6,1,'ACTIVO',CURDATE(),0,0 FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM cuentas_contables WHERE codigo='421040');

INSERT INTO cuentas_contables (codigo,nombre,tipo,nivel,es_movimiento,estado,fecha_creacion,requiere_tercero,requiere_documento_cruce)
SELECT '539540','Averias','GASTO',6,1,'ACTIVO',CURDATE(),0,0 FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM cuentas_contables WHERE codigo='539540');

INSERT INTO cuentas_contables (codigo,nombre,tipo,nivel,es_movimiento,estado,fecha_creacion,requiere_tercero,requiere_documento_cruce)
SELECT '513535','Fletes','GASTO',6,1,'ACTIVO',CURDATE(),0,0 FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM cuentas_contables WHERE codigo='513535');
