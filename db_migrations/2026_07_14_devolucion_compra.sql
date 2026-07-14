-- Devolución de compra (nota débito). Aplicar por cada schema/tenant.
CREATE TABLE IF NOT EXISTS devoluciones_compra (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    orden_compra_id     BIGINT       NOT NULL,
    numero_devolucion   VARCHAR(100) NOT NULL,
    motivo              VARCHAR(300)     NULL,
    observaciones       TEXT             NULL,
    estado              VARCHAR(20)  NOT NULL DEFAULT 'REGISTRADA',
    total_devuelto      DECIMAL(18,2) NOT NULL DEFAULT 0,
    iva_devuelto        DECIMAL(18,2) NOT NULL DEFAULT 0,
    retencion_devuelta  DECIMAL(18,2) NOT NULL DEFAULT 0,
    total_neto          DECIMAL(18,2) NOT NULL DEFAULT 0,
    nit_proveedor       VARCHAR(50)      NULL,
    nombre_proveedor    VARCHAR(200)     NULL,
    usuario_creacion    VARCHAR(100) NOT NULL,
    fecha_creacion      DATE         NOT NULL,
    motivo_anulacion    VARCHAR(300)     NULL,
    fecha_anulacion     DATE             NULL,
    usuario_anulacion   VARCHAR(100)     NULL,
    PRIMARY KEY (id),
    KEY idx_devcompra_orden (orden_compra_id),
    CONSTRAINT fk_devcompra_orden FOREIGN KEY (orden_compra_id) REFERENCES ordenes_compra (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS detalles_devolucion_compra (
    id                       BIGINT        NOT NULL AUTO_INCREMENT,
    devolucion_compra_id     BIGINT        NOT NULL,
    detalle_orden_compra_id  BIGINT        NOT NULL,
    cantidad_devuelta        INT           NOT NULL,
    costo_unitario           DECIMAL(18,2) NOT NULL DEFAULT 0,
    iva_linea                DECIMAL(18,2) NOT NULL DEFAULT 0,
    total_linea              DECIMAL(18,2) NOT NULL DEFAULT 0,
    motivo                   VARCHAR(300)      NULL,
    PRIMARY KEY (id),
    KEY idx_detdevcompra_dev (devolucion_compra_id),
    KEY idx_detdevcompra_det (detalle_orden_compra_id),
    CONSTRAINT fk_detdevcompra_dev FOREIGN KEY (devolucion_compra_id) REFERENCES devoluciones_compra (id),
    CONSTRAINT fk_detdevcompra_det FOREIGN KEY (detalle_orden_compra_id) REFERENCES detalles_orden_compra (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
