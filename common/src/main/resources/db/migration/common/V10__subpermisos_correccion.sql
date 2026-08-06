-- Corrige subpermisos: el front (navbar.tsx `tieneSubpermiso`) exige codigo_accion con el
-- prefijo "ingresar_"/"ingresa_" para poder ocultar/mostrar los hijos del menú. Una migración
-- ad-hoc (db_migrations/2026_07_31_subpermisos.sql) insertó en su lugar una taxonomía nueva
-- (ver/crear/editar/eliminar...) que no calza con ese prefijo y deja los submenús sin mostrarse
-- para cualquier rol con subpermisos asignados. Se reemplaza esa taxonomía por el catálogo
-- ingresar_<Nombre> vigente (uno por cada hijo real de navbar.tsx), que es el que Luis David
-- (dueño de este módulo) tiene en su ambiente.
--
-- Los módulos 5/8/10/16/18/19 no llevan subpermisos: hoy sus entradas de navbar.tsx no tienen
-- `children`, así que no hay nada que ocultar/mostrar a ese nivel.
DELETE FROM subpermisos
WHERE permiso_padre_id IN (5,8,9,10,11,12,14,15,16,17,18,19)
  AND codigo_accion NOT REGEXP '^ingres[a-z]*_';

-- MySQL no soporta "CREATE INDEX ... IF NOT EXISTS" (probado en 8.0.45: error de sintaxis);
-- se usa el mismo patrón de information_schema + PREPARE/EXECUTE que el resto de V9/V8.
SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'subpermisos' AND INDEX_NAME = 'uq_subpermiso_padre_accion');
SET @sql := IF(@idx = 0, 'CREATE UNIQUE INDEX uq_subpermiso_padre_accion ON subpermisos (permiso_padre_id, codigo_accion)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

INSERT IGNORE INTO subpermisos (permiso_padre_id, codigo_accion, nombre) VALUES
(9,  'ingresar_Rol',                       'Roles'),
(9,  'ingresar_Usuarios',                  'Usuarios'),
(11, 'ingresar_Atributos',                 'Atributos'),
(11, 'ingresar_Productos',                 'Productos'),
(11, 'ingresar_Movimientos',               'Movimientos'),
(11, 'ingresar_Kardex',                    'Kardex'),
(12, 'ingresar_Compras',                   'Compras'),
(12, 'ingresar_Docomentos soporte',        'Documentos soporte'),
(12, 'ingresa_Notas de ajuste',            'Notas de ajuste'),
(14, 'ingresar_Vendedores',                'Vendedores'),
(14, 'ingresar_Facturacion',               'Facturación'),
(14, 'ingresar_Cotizaciones',              'Cotizaciones'),
(14, 'ingresar_Pedidos',                   'Pedidos'),
(14, 'ingresar_Devoluciones',              'Devoluciones'),
(14, 'ingresar_Despachos',                 'Despachos'),
(15, 'ingresar_Cajeros',                   'Cajeros'),
(15, 'ingresar_Recibos',                   'Recibos'),
(15, 'ingresar_Egresos',                   'Egresos'),
(15, 'ingresar_Cuade de caja',             'Cuadre de caja'),
(17, 'ingresa_Plan de cuentasr',           'Plan de cuentas'),
(17, 'ingresar_Comprobantes',              'Comprobantes'),
(17, 'ingresar_Acientos contables',        'Acientos contables'),
(17, 'ingresa_Reportes contables',         'Reportes contables'),
(17, 'ingresar_Caja y bancos',             'Caja y bancos'),
(17, 'ingresar_Documentos',                'Documentos'),
(17, 'ingresar_Informacion exógena',       'Información exógena'),
(17, 'ingresar_Conceptos abiertos',        'Conceptos abiertos'),
(17, 'ingresar_Metodos de pago',           'Metodos de pago'),
(17, 'ingresar_Retenciones',               'Retenciones'),
(17, 'ingresar_Certificados de retencion', 'Certificados  de retención');
