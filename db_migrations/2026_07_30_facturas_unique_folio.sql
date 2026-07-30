-- ══════════════════════════════════════════════════════════════════
-- Facturación electrónica: folios únicos por comprobante y 1 factura por venta.
--
-- Respaldo en BD del fix de concurrencia en FacturacionElectronicaService
-- (lock pesimista sobre el comprobante al asignar consecutivo): aunque dos
-- transacciones calculen el mismo MAX(consecutivo)+1, la segunda inserción
-- falla aquí en lugar de emitir dos facturas con el mismo folio (ENC_6) —
-- Facturatech rechaza el folio repetido con 409 y esa factura quedaba
-- rechazada para siempre.
--
-- Ejecutar POR TENANT (sin schema quemado):
--     mysql -u root -p <tenant> < 2026_07_30_facturas_unique_folio.sql
-- Antes de aplicar, verificar que no existan duplicados:
--     SELECT comprobante_id, consecutivo, COUNT(*) n FROM facturas GROUP BY 1,2 HAVING n>1;
--     SELECT venta_id, COUNT(*) n FROM facturas WHERE venta_id IS NOT NULL GROUP BY 1 HAVING n>1;
-- ══════════════════════════════════════════════════════════════════
ALTER TABLE facturas ADD UNIQUE KEY uq_facturas_comp_consec (comprobante_id, consecutivo);
ALTER TABLE facturas ADD UNIQUE KEY uq_facturas_venta (venta_id);
