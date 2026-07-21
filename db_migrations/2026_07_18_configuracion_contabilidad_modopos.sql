-- modoPOS — Fase 1: flag por empresa (tenant) para activar/desactivar la CAPA CONTABLE.
--
-- La edición "POS puro" NO genera asientos contables ni gestiona periodos, pero mantiene
-- intactos: numeración, comprobantes, DIAN/factura electrónica, cajeros, inventario, caja
-- y cartera (CxC/CxP). La edición "POS + Contabilidad" es el comportamiento actual.
--
-- Tabla single-row (id=1), mismo patrón que `configuracion_compras`.
--   contabilidad_activa = 1  → lleva contabilidad (comportamiento ACTUAL).
--   contabilidad_desde  = fecha de corte: solo se contabilizan documentos con fecha >= esta.
--                          NULL = sin corte (contabiliza siempre que activa=1).
--
-- IMPORTANTE (no romper nada): el default es activa=1 / desde=NULL, así TODOS los tenants
-- existentes siguen contabilizando exactamente igual. El modo POS se activa manualmente por empresa.
-- Idempotente: CREATE IF NOT EXISTS + INSERT IGNORE de la fila única.

CREATE TABLE IF NOT EXISTS configuracion_contabilidad (
    id                   INT         NOT NULL PRIMARY KEY,
    contabilidad_activa  TINYINT(1)  NOT NULL DEFAULT 1,
    contabilidad_desde   DATE        NULL,
    fecha_actualizacion  DATETIME    NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO configuracion_contabilidad (id, contabilidad_activa, contabilidad_desde, fecha_actualizacion)
VALUES (1, 1, NULL, NOW());
