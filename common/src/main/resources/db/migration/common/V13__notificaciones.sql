-- Tabla de notificaciones de la campana del navbar (ver com.pazzioliweb.commonbacken.entity.Notificacion).
-- Sin distinción por usuario: visibles para todos los usuarios de la empresa/tenant.
CREATE TABLE IF NOT EXISTS notificaciones (
    id             BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tipo           VARCHAR(50)  NOT NULL,
    titulo         VARCHAR(150) NOT NULL,
    mensaje        VARCHAR(500) NOT NULL,
    entidad_tipo   VARCHAR(30)  NULL,
    entidad_id     BIGINT       NULL,
    leida          TINYINT(1)   NOT NULL DEFAULT 0,
    fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_leida    DATETIME     NULL,
    INDEX idx_notificaciones_leida (leida),
    INDEX idx_notificaciones_tipo_fecha (tipo, fecha_creacion)
);
