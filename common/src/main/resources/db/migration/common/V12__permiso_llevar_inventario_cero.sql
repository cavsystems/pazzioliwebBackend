-- Permiso puntual para el botón "Llevar bodega a 0" del módulo Inventarios > Movimientos
-- (carga automáticamente todas las existencias de una bodega como salida total, dejándola
-- en 0). Es una acción sensible -genera una salida masiva de TODO el stock de la bodega-
-- así que no basta con tener acceso al módulo Inventarios: se exige este permiso puntual
-- (o "Administrador"), igual que "Anular comprobantes" en tesorería/caja.
INSERT INTO permisos (nombre)
SELECT 'Llevar inventario a cero' FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM permisos WHERE nombre = 'Llevar inventario a cero');
