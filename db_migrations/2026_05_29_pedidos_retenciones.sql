-- Agrega retenciones a pedidos para que se propaguen correctamente al convertir a venta
ALTER TABLE pedidos
    ADD COLUMN retefuente DECIMAL(15,2) NOT NULL DEFAULT 0,
    ADD COLUMN reteiva    DECIMAL(15,2) NOT NULL DEFAULT 0,
    ADD COLUMN reteica    DECIMAL(15,2) NOT NULL DEFAULT 0;
