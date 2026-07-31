-- ══════════════════════════════════════════════════════════════════
-- Sub-permisos por módulo y asignación a roles
-- Aplicar POR TENANT:
--     mysql -u root -p <tenant> < 2026_07_31_subpermisos.sql
-- ══════════════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS subpermisos (
    codigo            INT AUTO_INCREMENT PRIMARY KEY,
    permiso_padre_id  INT          NOT NULL,
    codigo_accion     VARCHAR(50)  NOT NULL,
    nombre            VARCHAR(100) NOT NULL,
    activo            TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT fk_subpermiso_padre FOREIGN KEY (permiso_padre_id)
        REFERENCES permisos (codigo) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS subpermisos_roles (
    codigo              INT AUTO_INCREMENT PRIMARY KEY,
    codigorol           INT         NOT NULL,
    codigosubpermiso    INT         NOT NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    codigousuariocreado INT                  DEFAULT 0,
    fechacreado         DATETIME             DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_subpermisrol_rol  FOREIGN KEY (codigorol)
        REFERENCES roles (codigo) ON DELETE CASCADE,
    CONSTRAINT fk_subpermisrol_sub  FOREIGN KEY (codigosubpermiso)
        REFERENCES subpermisos (codigo) ON DELETE CASCADE
);

-- Módulo 5 – Empresas
INSERT INTO subpermisos (permiso_padre_id, codigo_accion, nombre) VALUES
(5, 'ver',                  'Ver empresas'),
(5, 'crear',                'Crear empresa'),
(5, 'editar',               'Editar empresa'),
(5, 'eliminar',             'Eliminar empresa'),
(5, 'cambiar_empresa',      'Cambiar empresa activa');

-- Módulo 8 – Mi Panel
INSERT INTO subpermisos (permiso_padre_id, codigo_accion, nombre) VALUES
(8, 'ver',                  'Ver mi panel'),
(8, 'configurar_widgets',   'Configurar widgets');

-- Módulo 9 – Usuarios
INSERT INTO subpermisos (permiso_padre_id, codigo_accion, nombre) VALUES
(9, 'ver',                  'Ver usuarios'),
(9, 'crear',                'Crear usuario'),
(9, 'editar',               'Editar usuario'),
(9, 'eliminar',             'Eliminar usuario'),
(9, 'asignar_roles',        'Asignar roles'),
(9, 'cambiar_clave',        'Cambiar contraseña'),
(9, 'activar_desactivar',   'Activar / desactivar usuario');

-- Módulo 10 – Terceros
INSERT INTO subpermisos (permiso_padre_id, codigo_accion, nombre) VALUES
(10, 'ver',                 'Ver terceros'),
(10, 'crear',               'Crear tercero'),
(10, 'editar',              'Editar tercero'),
(10, 'eliminar',            'Eliminar tercero'),
(10, 'ver_extracto',        'Ver extracto'),
(10, 'exportar',            'Exportar terceros');

-- Módulo 11 – Inventarios
INSERT INTO subpermisos (permiso_padre_id, codigo_accion, nombre) VALUES
(11, 'ver',                 'Ver inventario'),
(11, 'crear',               'Crear producto'),
(11, 'editar',              'Editar producto'),
(11, 'eliminar',            'Eliminar producto'),
(11, 'ajustar_stock',       'Ajustar stock'),
(11, 'ver_costos',          'Ver costos'),
(11, 'transferir',          'Transferir entre bodegas'),
(11, 'exportar',            'Exportar inventario');

-- Módulo 12 – Compras
INSERT INTO subpermisos (permiso_padre_id, codigo_accion, nombre) VALUES
(12, 'ver',                 'Ver compras'),
(12, 'crear',               'Crear compra'),
(12, 'editar',              'Editar compra'),
(12, 'eliminar',            'Eliminar compra'),
(12, 'aprobar',             'Aprobar compra'),
(12, 'anular',              'Anular compra'),
(12, 'ver_costos',          'Ver costos de compra'),
(12, 'exportar',            'Exportar compras');

-- Módulo 14 – Ventas
INSERT INTO subpermisos (permiso_padre_id, codigo_accion, nombre) VALUES
(14, 'ver',                 'Ver ventas'),
(14, 'crear',               'Crear venta'),
(14, 'editar',              'Editar venta'),
(14, 'eliminar',            'Eliminar venta'),
(14, 'aprobar',             'Aprobar venta'),
(14, 'anular',              'Anular venta'),
(14, 'aplicar_descuento',   'Aplicar descuento'),
(14, 'exportar',            'Exportar ventas');

-- Módulo 15 – Caja
INSERT INTO subpermisos (permiso_padre_id, codigo_accion, nombre) VALUES
(15, 'ver',                 'Ver caja'),
(15, 'apertura',            'Realizar apertura de caja'),
(15, 'cierre',              'Realizar cierre de caja'),
(15, 'registrar_ingreso',   'Registrar ingreso'),
(15, 'registrar_egreso',    'Registrar egreso'),
(15, 'anular_movimiento',   'Anular movimiento'),
(15, 'ver_saldo',           'Ver saldo de caja');

-- Módulo 16 – Nómina
INSERT INTO subpermisos (permiso_padre_id, codigo_accion, nombre) VALUES
(16, 'ver',                 'Ver nómina'),
(16, 'crear',               'Crear nómina'),
(16, 'editar',              'Editar nómina'),
(16, 'eliminar',            'Eliminar nómina'),
(16, 'procesar',            'Procesar nómina'),
(16, 'aprobar',             'Aprobar nómina'),
(16, 'exportar',            'Exportar nómina');

-- Módulo 17 – Contabilidad
INSERT INTO subpermisos (permiso_padre_id, codigo_accion, nombre) VALUES
(17, 'ver',                 'Ver contabilidad'),
(17, 'crear_asiento',       'Crear asiento contable'),
(17, 'editar',              'Editar asiento'),
(17, 'anular',              'Anular asiento'),
(17, 'cerrar_periodo',      'Cerrar período contable'),
(17, 'exportar',            'Exportar contabilidad');

-- Módulo 18 – Reportes
INSERT INTO subpermisos (permiso_padre_id, codigo_accion, nombre) VALUES
(18, 'ver_financieros',     'Ver reportes financieros'),
(18, 'ver_ventas',          'Ver reportes de ventas'),
(18, 'ver_compras',         'Ver reportes de compras'),
(18, 'ver_inventario',      'Ver reportes de inventario'),
(18, 'ver_nomina',          'Ver reportes de nómina'),
(18, 'exportar',            'Exportar reportes');

-- Módulo 19 – Parámetros
INSERT INTO subpermisos (permiso_padre_id, codigo_accion, nombre) VALUES
(19, 'ver',                 'Ver parámetros'),
(19, 'editar_generales',    'Editar parámetros generales'),
(19, 'editar_numeraciones', 'Editar numeraciones'),
(19, 'editar_impuestos',    'Editar impuestos'),
(19, 'editar_bodegas',      'Editar bodegas');
