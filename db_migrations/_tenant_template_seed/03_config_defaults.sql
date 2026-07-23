-- modoPOS / template — configuración por defecto (single-row) + métodos de pago base.
-- Se siembra saneado (SIN cajero por defecto ni cuentas company-specific): cada empresa lo ajusta.

-- Contabilidad: por defecto suite completa (activa=1). Una empresa POS se pone en 0 al crearla/editarla.
REPLACE INTO configuracion_contabilidad (id, contabilidad_activa, contabilidad_desde, fecha_actualizacion)
VALUES (1, 1, NULL, NOW());

-- Config de compras: sin cajero por defecto (cada empresa asigna el suyo).
REPLACE INTO configuracion_compras (id, cajero_default_id, fecha_actualizacion)
VALUES (1, NULL, NOW());

-- Métodos de pago base (sin cuenta contable/bancaria: la empresa mapea su PUC luego).
INSERT IGNORE INTO metodos_pago (metodo_pago_id, descripcion, sigla, estado, tipo_negociacion, tipos, cuenta_bancaria_id, cuenta_contable_id) VALUES
 (1,'Efectivo','Efc','ACTIVO','Contado','RECIBO,EGRESO,VENTA,COMPRA',NULL,NULL),
 (2,'Transferencia','Transf','ACTIVO','Contado','RECIBO,EGRESO,VENTA,COMPRA',NULL,NULL),
 (3,'T.Debito','tdb','ACTIVO','Contado','RECIBO,EGRESO,VENTA,COMPRA',NULL,NULL),
 (4,'T.Credito','tcr','ACTIVO','Contado','RECIBO,EGRESO,VENTA,COMPRA',NULL,NULL),
 (5,'Credito','CRE','ACTIVO','Credito','RECIBO,EGRESO,VENTA,COMPRA',NULL,NULL);

-- Cliente por defecto "Consumidor Final" (necesario para POS: la venta lo auto-selecciona
-- y de él sale la lista de precios por defecto). identificacion 222222222222, precio_id=1 (Detal).
SET FOREIGN_KEY_CHECKS=0;
INSERT IGNORE INTO terceros
  (tercero_id, tipo_identificacion_id, identificacion, dv, nombre_1, nombre_2, apellido_1, apellido_2,
   razon_social, regimen_id, clasificacion_tercero_id, direccion, correo, plazo, cupo, precio_id,
   actividad_economica_id, tipo_persona_id, estado, saldofavor_cliente, saldofavor_empresa)
VALUES
  (1, 3, '222222222222', '7', 'Consumidor', 'Final', '', '',
   'Consumidor Final', 1, 1, 'Direccion', 'consu@gmail.com', 0, 2000000, 1,
   0, 1, 'ACTIVO', 0, 0);
SET FOREIGN_KEY_CHECKS=1;
