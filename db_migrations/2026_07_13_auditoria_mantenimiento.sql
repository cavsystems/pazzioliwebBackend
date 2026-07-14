-- Auditoría de las herramientas de mantenimiento de documentos contables.
-- Ejecutar manualmente en la base de datos de CADA tenant (menos 'administrador').

CREATE TABLE IF NOT EXISTS auditoria_mantenimiento (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    operacion  VARCHAR(40)  NOT NULL,
    detalle    VARCHAR(500),
    usuario    VARCHAR(100),
    fecha_hora DATETIME     NOT NULL
);
