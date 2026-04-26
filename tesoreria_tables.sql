CREATE TABLE IF NOT EXISTS cavsystems.comprobantes_egreso (id BIGINT AUTO_INCREMENT PRIMARY KEY, consecutivo INT NOT NULL, tercero_id INT NOT NULL, tercero_nombre VARCHAR(200), tercero_nit VARCHAR(50), fecha DATE NOT NULL, subtotal DECIMAL(15,2) NOT NULL DEFAULT 0, retefuente DECIMAL(15,2) NOT NULL DEFAULT 0, reteica DECIMAL(15,2) NOT NULL DEFAULT 0, reteiva DECIMAL(15,2) NOT NULL DEFAULT 0, descuento DECIMAL(15,2) NOT NULL DEFAULT 0, total DECIMAL(15,2) NOT NULL DEFAULT 0, metodo_pago_id INT, concepto VARCHAR(500), centro_costo VARCHAR(100), estado VARCHAR(30) NOT NULL DEFAULT 'ACTIVO', usuario_id INT, cajero_id INT, fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (tercero_id) REFERENCES terceros(tercero_id), FOREIGN KEY (cajero_id) REFERENCES cajeros(cajero_id), FOREIGN KEY (metodo_pago_id) REFERENCES metodos_pago(metodo_pago_id));
CREATE TABLE IF NOT EXISTS cavsystems.detalle_comprobante_egreso (id BIGINT AUTO_INCREMENT PRIMARY KEY, comprobante_egreso_id BIGINT NOT NULL, cuenta_por_pagar_id BIGINT NOT NULL, monto_abonado DECIMAL(15,2) NOT NULL DEFAULT 0, FOREIGN KEY (comprobante_egreso_id) REFERENCES comprobantes_egreso(id), FOREIGN KEY (cuenta_por_pagar_id) REFERENCES cuentas_por_pagar(id));
CREATE TABLE IF NOT EXISTS cavsystems.recibos_caja (id BIGINT AUTO_INCREMENT PRIMARY KEY, consecutivo INT NOT NULL, tercero_id INT NOT NULL, tercero_nombre VARCHAR(200), tercero_nit VARCHAR(50), fecha DATE NOT NULL, subtotal DECIMAL(15,2) NOT NULL DEFAULT 0, retefuente DECIMAL(15,2) NOT NULL DEFAULT 0, reteica DECIMAL(15,2) NOT NULL DEFAULT 0, reteiva DECIMAL(15,2) NOT NULL DEFAULT 0, descuento DECIMAL(15,2) NOT NULL DEFAULT 0, total DECIMAL(15,2) NOT NULL DEFAULT 0, metodo_pago_id INT, concepto VARCHAR(500), centro_costo VARCHAR(100), estado VARCHAR(30) NOT NULL DEFAULT 'ACTIVO', usuario_id INT, cajero_id INT, fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (tercero_id) REFERENCES terceros(tercero_id), FOREIGN KEY (cajero_id) REFERENCES cajeros(cajero_id), FOREIGN KEY (metodo_pago_id) REFERENCES metodos_pago(metodo_pago_id));
CREATE TABLE IF NOT EXISTS cavsystems.detalle_recibo_caja (id BIGINT AUTO_INCREMENT PRIMARY KEY, recibo_caja_id BIGINT NOT NULL, cuenta_por_cobrar_id BIGINT NOT NULL, monto_abonado DECIMAL(15,2) NOT NULL DEFAULT 0, FOREIGN KEY (recibo_caja_id) REFERENCES recibos_caja(id), FOREIGN KEY (cuenta_por_cobrar_id) REFERENCES cuentas_por_cobrar(id));

-- Migración: nuevos campos en recibos_caja
ALTER TABLE cavsystems.recibos_caja ADD COLUMN fecha_recibo DATE NULL;
ALTER TABLE cavsystems.recibos_caja ADD COLUMN averias DECIMAL(15,2) NOT NULL DEFAULT 0;
ALTER TABLE cavsystems.recibos_caja ADD COLUMN fletes DECIMAL(15,2) NOT NULL DEFAULT 0;
ALTER TABLE cavsystems.recibos_caja MODIFY COLUMN concepto TEXT;

-- Nueva tabla: medios de pago por recibo de caja
CREATE TABLE IF NOT EXISTS cavsystems.recibo_caja_medio_pago (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  recibo_caja_id BIGINT NOT NULL,
  metodo_pago_id INT NOT NULL,
  monto DECIMAL(18,2) NOT NULL,
  FOREIGN KEY (recibo_caja_id) REFERENCES recibos_caja(id),
  FOREIGN KEY (metodo_pago_id) REFERENCES metodos_pago(metodo_pago_id)
);

-- Migración: concepto abierto en recibos_caja
ALTER TABLE cavsystems.recibos_caja ADD COLUMN concepto_abierto BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE cavsystems.recibos_caja ADD COLUMN monto_concepto_abierto DECIMAL(18,2) NULL;
ALTER TABLE cavsystems.recibos_caja MODIFY COLUMN tercero_id INT NULL;

-- Migración: concepto abierto en comprobantes_egreso
ALTER TABLE cavsystems.comprobantes_egreso ADD COLUMN concepto_abierto BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE cavsystems.comprobantes_egreso ADD COLUMN monto_concepto_abierto DECIMAL(18,2) NULL;
ALTER TABLE cavsystems.comprobantes_egreso MODIFY COLUMN tercero_id INT NULL;

-- Migración: fecha_egreso y medios de pago múltiples en comprobantes_egreso
ALTER TABLE cavsystems.comprobantes_egreso ADD COLUMN fecha_egreso DATE NULL;
ALTER TABLE cavsystems.comprobantes_egreso MODIFY COLUMN concepto TEXT;

CREATE TABLE IF NOT EXISTS cavsystems.comprobante_egreso_medio_pago (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  comprobante_egreso_id BIGINT NOT NULL,
  metodo_pago_id INT NOT NULL,
  monto DECIMAL(18,2) NOT NULL,
  FOREIGN KEY (comprobante_egreso_id) REFERENCES comprobantes_egreso(id),
  FOREIGN KEY (metodo_pago_id) REFERENCES metodos_pago(metodo_pago_id)
);
