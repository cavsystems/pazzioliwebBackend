-- modoPOS — Fase 1 (template): tabla del flag de contabilidad para el schema PLANTILLA
-- (`_tenant_template`). Cada empresa nueva se CLONA del template (CREATE TABLE ... LIKE +
-- INSERT ... SELECT), por lo que hereda esta tabla.
--
-- Default: contabilidad_activa=1 / contabilidad_desde=NULL (suite completa) → toda empresa nueva
-- arranca con contabilidad activa; el modo POS se define en la creación/edición de la empresa.
-- Idempotente (CREATE TABLE IF NOT EXISTS + INSERT IGNORE) por si el template ya la tuviera.
CREATE TABLE IF NOT EXISTS configuracion_contabilidad (
    id                   INT         NOT NULL PRIMARY KEY,
    contabilidad_activa  TINYINT(1)  NOT NULL DEFAULT 1,
    contabilidad_desde   DATE        NULL,
    fecha_actualizacion  DATETIME    NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO configuracion_contabilidad (id, contabilidad_activa, contabilidad_desde, fecha_actualizacion)
VALUES (1, 1, NULL, NOW());
