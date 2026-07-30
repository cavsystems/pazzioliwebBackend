-- Índices optimizados para el query de kardex-reporte
-- Optimiza el endpoint: GET /api/inventario/movimientos/kardex-reporte

-- Índice compuesto para filtros principales del kardex
-- fecha_creacion (filtro rango + order by), producto_variante_id (filtro), bodega_id (filtro + join)
CREATE INDEX IF NOT EXISTS idx_kardex_fecha_variante_bodega 
ON kardex (fecha_creacion, producto_variante_id, bodega_id);

-- Índice para búsqueda por variante de producto (filtro más frecuente)
CREATE INDEX IF NOT EXISTS idx_kardex_producto_variante 
ON kardex (producto_variante_id);

-- Índice para búsqueda por bodega (filtro + join)
CREATE INDEX IF NOT EXISTS idx_kardex_bodega 
ON kardex (bodega_id);

-- Índice para join con movimientos_inventario
CREATE INDEX IF NOT EXISTS idx_kardex_movimiento 
ON kardex (movimiento_inventario_id);

-- Índice para ordenamiento por fecha de emisión (usado en ORDER BY)
CREATE INDEX IF NOT EXISTS idx_kardex_fecha_emision 
ON kardex (fecha_emision);

-- Índice para filtro por tipo de movimiento (cuando se usa)
CREATE INDEX IF NOT EXISTS idx_kardex_tipo 
ON kardex (tipo);

-- Índice compuesto para movimientos_inventario (join con kardex)
CREATE INDEX IF NOT EXISTS idx_movimientos_inventario_comprobante 
ON movimientos_inventario (comprobante_id, consecutivo);

-- Índice para producto_variantes (join principal)
CREATE INDEX IF NOT EXISTS idx_producto_variantes_producto 
ON producto_variantes (producto_id);

-- Índice para bodegas (join)
CREATE INDEX IF NOT EXISTS idx_bodegas_codigo 
ON bodegas (codigo);
