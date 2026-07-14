-- Módulo de activos fijos y depreciación (línea recta).
-- Ejecutar manualmente en la base de datos de CADA tenant (menos 'administrador').

CREATE TABLE IF NOT EXISTS activos_fijos (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre                    VARCHAR(200)  NOT NULL,
    codigo                    VARCHAR(60),
    fecha_adquisicion         DATE          NOT NULL,
    fecha_inicio_depreciacion DATE          NOT NULL,
    valor_adquisicion         DECIMAL(18,2) NOT NULL DEFAULT 0,
    valor_residual            DECIMAL(18,2) NOT NULL DEFAULT 0,
    vida_util_meses           INT           NOT NULL,
    metodo                    VARCHAR(20)   NOT NULL DEFAULT 'LINEA_RECTA',
    depreciacion_acumulada    DECIMAL(18,2) NOT NULL DEFAULT 0,
    meses_depreciados         INT           NOT NULL DEFAULT 0,
    cuenta_activo_id          INT,
    cuenta_depreciacion_id    INT,
    cuenta_gasto_id           INT,
    centro_costo_id           INT,
    estado                    VARCHAR(20)   NOT NULL DEFAULT 'ACTIVO',
    observaciones             VARCHAR(500),
    fecha_creacion            DATE          NOT NULL
);

CREATE TABLE IF NOT EXISTS depreciacion_detalle (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    activo_id      BIGINT        NOT NULL,
    anio           INT           NOT NULL,
    mes            INT           NOT NULL,
    cuota          DECIMAL(18,2) NOT NULL DEFAULT 0,
    numero_asiento VARCHAR(40),
    fecha_creacion DATE          NOT NULL,
    INDEX idx_dep_periodo (anio, mes)
);
