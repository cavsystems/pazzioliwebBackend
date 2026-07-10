-- ═══════════════════════════════════════════════════════════════════════
-- Notas a los estados financieros
-- Aplicar en CADA schema de tenant (cavsystems, etc.). NO en 'administrador'.
-- Tabla nueva, aditiva.
-- ═══════════════════════════════════════════════════════════════════════
USE cavsystems;

CREATE TABLE IF NOT EXISTS notas_estados_financieros (
    id             INT NOT NULL AUTO_INCREMENT,
    anio           INT NOT NULL,
    numero         INT NOT NULL DEFAULT 1,
    titulo         VARCHAR(200) NOT NULL,
    contenido      TEXT,
    estado         VARCHAR(15) NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_notas_anio (anio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
