-- ═══════════════════════════════════════════════════════════
--  V3 — Agregar columnas de ubicación a la tabla terceros
--       (departamento_id, ciudad_id, codigo_postal)
--       Posición: después de la columna direccion
-- ═══════════════════════════════════════════════════════════

ALTER TABLE terceros
    ADD COLUMN departamento_id INT NULL
        COMMENT 'FK al departamento del tercero'
        AFTER direccion,

    ADD COLUMN ciudad_id INT NULL
        COMMENT 'FK al municipio (ciudad) del tercero'
        AFTER departamento_id,

    ADD COLUMN codigo_postal VARCHAR(20) NULL
        COMMENT 'Código postal del tercero'
        AFTER ciudad_id;

-- ─── Llaves foráneas ────────────────────────────────────────
ALTER TABLE terceros
    ADD CONSTRAINT fk_terceros_departamento
        FOREIGN KEY (departamento_id)
        REFERENCES departamentos (codigo)
        ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE terceros
    ADD CONSTRAINT fk_terceros_ciudad
        FOREIGN KEY (ciudad_id)
        REFERENCES municipios (codigo)
        ON UPDATE CASCADE ON DELETE SET NULL;

