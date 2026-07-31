-- Migración: 2026-07-30
-- Agrega centro_costo_id (FK → centrocosto.codigo) a ventas, ordenes_compra y movimientos_inventario
-- Ejecutar en cada tenant. El campo es nullable para no romper registros existentes.

-- ── ventas ──────────────────────────────────────────────────────────────────
ALTER TABLE `ventas`
  ADD COLUMN `centro_costo_id` int DEFAULT NULL AFTER `reteica`,
  ADD KEY `idx_venta_centro_costo` (`centro_costo_id`),
  ADD CONSTRAINT `fk_venta_centro_costo`
      FOREIGN KEY (`centro_costo_id`) REFERENCES `centrocosto` (`codigo`);

-- ── ordenes_compra ───────────────────────────────────────────────────────────
ALTER TABLE `ordenes_compra`
  ADD COLUMN `centro_costo_id` int DEFAULT NULL AFTER `plazo`,
  ADD KEY `idx_oc_centro_costo` (`centro_costo_id`),
  ADD CONSTRAINT `fk_oc_centro_costo`
      FOREIGN KEY (`centro_costo_id`) REFERENCES `centrocosto` (`codigo`);

-- ── movimientos_inventario ───────────────────────────────────────────────────
ALTER TABLE `movimientos_inventario`
  ADD COLUMN `centro_costo_id` int DEFAULT NULL AFTER `documento_origen_id`,
  ADD KEY `idx_movinv_centro_costo` (`centro_costo_id`),
  ADD CONSTRAINT `fk_movinv_centro_costo`
      FOREIGN KEY (`centro_costo_id`) REFERENCES `centrocosto` (`codigo`);
