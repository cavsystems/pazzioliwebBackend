-- ═══════════════════════════════════════════════════════════════════════
-- Tipos de comprobante MANUAL (notas de contabilidad, depreciaciones, etc.)
-- Aplicar en CADA schema de tenant (cavsystems, etc.). NO en 'administrador'.
--
-- Catálogo SEPARADO del enum transaccional (TipoMovimientoComprobante). Estos
-- tipos son para asientos contables manuales; no tienen cajero, DIAN ni
-- inventario. No afectan ningún flujo existente (tabla nueva, aditiva).
-- ═══════════════════════════════════════════════════════════════════════
USE cavsystems;

CREATE TABLE IF NOT EXISTS tipos_comprobante_manual (
    id                    INT NOT NULL AUTO_INCREMENT,
    codigo                VARCHAR(10)  NOT NULL,
    nombre                VARCHAR(100) NOT NULL,
    prefijo               VARCHAR(20)  NULL,
    siguiente_consecutivo INT NOT NULL DEFAULT 1,
    estado                VARCHAR(15)  NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tipo_comprobante_manual_codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Ejemplos comunes (idempotente: no duplica si ya existen).
INSERT IGNORE INTO tipos_comprobante_manual (codigo, nombre, prefijo, siguiente_consecutivo, estado)
VALUES
    ('NC',  'Nota de contabilidad', 'NC',  1, 'ACTIVO'),
    ('DEP', 'Depreciaciones',       'DEP', 1, 'ACTIVO');
