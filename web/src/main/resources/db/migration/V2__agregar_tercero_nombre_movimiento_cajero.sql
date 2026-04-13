-- ═══════════════════════════════════════════════════════════
--  V2 — Cambios requeridos por el Informe Diario de Ventas
-- ═══════════════════════════════════════════════════════════

-- 1. Agregar columna tercero_nombre a movimiento_cajero
ALTER TABLE movimiento_cajero
    ADD COLUMN IF NOT EXISTS tercero_nombre VARCHAR(200) NULL
        COMMENT 'Nombre del tercero (proveedor en EGRESO, cliente en ABONO)'
        AFTER descripcion;

-- 2. Crear tabla devoluciones_venta si no existe
CREATE TABLE IF NOT EXISTS devoluciones_venta (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    venta_id            BIGINT          NOT NULL,
    cajero_id           INT             NULL,
    numero_devolucion   VARCHAR(255)    NOT NULL,
    motivo              VARCHAR(255)    NOT NULL,
    observaciones       TEXT            NULL,
    estado              VARCHAR(50)     NOT NULL DEFAULT 'REGISTRADA',
    total_devuelto      DECIMAL(19, 2)  NOT NULL DEFAULT 0.00,
    iva_devuelto        DECIMAL(19, 2)  NOT NULL DEFAULT 0.00,
    total_neto          DECIMAL(19, 2)  NOT NULL DEFAULT 0.00,
    usuario_creacion    VARCHAR(255)    NOT NULL,
    fecha_creacion      DATE            NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_numero_devolucion (numero_devolucion),
    CONSTRAINT fk_dev_venta   FOREIGN KEY (venta_id)  REFERENCES ventas(id),
    CONSTRAINT fk_dev_cajero  FOREIGN KEY (cajero_id) REFERENCES cajeros(cajero_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='Encabezado de devoluciones de ventas';

-- 3. Crear tabla detalle_devoluciones si no existe
CREATE TABLE IF NOT EXISTS detalle_devoluciones (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    devolucion_id           BIGINT          NOT NULL,
    codigo_producto         VARCHAR(255)    NOT NULL,
    descripcion_producto    VARCHAR(500)    NOT NULL,
    cantidad                INT             NOT NULL,
    precio_unitario         DECIMAL(19, 2)  NOT NULL,
    iva                     DECIMAL(19, 2)  NOT NULL DEFAULT 0.00,
    total                   DECIMAL(19, 2)  NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id),
    CONSTRAINT fk_detdev_devolucion FOREIGN KEY (devolucion_id) REFERENCES devoluciones_venta(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='Detalle de ítems devueltos';
