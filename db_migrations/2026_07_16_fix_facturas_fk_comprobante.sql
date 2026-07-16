-- La tabla `facturas` tenía la FK `facturas_ibfk_1` (comprobante_id) apuntando a la tabla LEGACY
-- `comprobantes` (que solo conserva ids 1 y 2, en desuso). El sistema ya usa `comprobantes_contables`
-- para los comprobantes de las ventas (p.ej. FC id 8), por lo que al generar la factura electrónica
-- de una venta con comprobante != 1/2, el INSERT violaba la FK y REVERTÍA TODA LA VENTA.
--
-- Se elimina la FK obsoleta (apunta a una tabla muerta; no aporta integridad real). facturas.comprobante_id
-- se sigue manejando por la aplicación como id de comprobantes_contables.
-- Idempotente: solo elimina la FK si existe.
SET @fk := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'facturas'
              AND COLUMN_NAME = 'comprobante_id' AND REFERENCED_TABLE_NAME = 'comprobantes' LIMIT 1);
SET @sql := IF(@fk IS NOT NULL, CONCAT('ALTER TABLE facturas DROP FOREIGN KEY ', @fk), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
