package com.pazzioliweb.movimientosinventariomodule.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.movimientosinventariomodule.dtos.KardexReportDto;
import com.pazzioliweb.movimientosinventariomodule.entity.Kardex;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.entity.Bodegas;

public interface KardexRepository extends JpaRepository<Kardex, Long> {

        List<Kardex> findByMovimiento_MovimientoId(Long id);

        Optional<Kardex> findTopByProductoVarianteAndBodegaOrderByFechaCreacionDesc(ProductoVariante variante, Bodegas bodega);

        @Query(value = "SELECT fecha_creacion, numerofactura, tipo_movimiento AS movimiento, tipoentrada AS tipo_movimiento, tipo, " +
                       "descripcion AS Producto, entrada, salida, costo_promedio, total_costo, saldo, nombrebodega " +
                       "FROM (" +
                       "SELECT " +
                       "  m.tipo_movimiento AS tipoentrada, " +
                       "  co.prefijo, " +
                       "  m.consecutivo, " +
                       "  co.tipo_movimiento AS tipo, " +
                       "  CONCAT(TRIM(pod.descripcion), '  ', IFNULL(TRIM(po.referencia_variantes), '')) AS descripcion, " +
                       "  k.entrada, " +
                       "  k.salida, " +
                       "  k.saldo, " +
                       "  k.costo_unitario, " +
                       "  k.fecha_creacion, " +
                       "  CONCAT(co.prefijo, '-', m.consecutivo) AS numerofactura, " +
                       "  mov.movimiento_cajero_id, " +
                       "  mov.detalle_cajero_id, " +
                       "  b.nombre AS nombrebodega, " +
                       "  mov.cajero_id, " +
                       "  CASE " +
                       "    WHEN co.prefijo = 'VC' THEN 'CUENTA_POR_COBRAR' " +
                       "    WHEN co.tipo_movimiento = 'CC' AND mov.tipo_movimiento IS NULL THEN 'COMPRA' " +
                       "    WHEN co.tipo_movimiento = 'CR' AND mov.tipo_movimiento IS NULL THEN 'COMPRA' " +
                       "    WHEN co.tipo_movimiento = 'EI' AND mov.tipo_movimiento IS NULL THEN 'ENTRADA' " +
                       "    WHEN co.tipo_movimiento = 'SI' AND mov.tipo_movimiento IS NULL THEN 'SALIDA' " +
                       "    WHEN co.tipo_movimiento = 'TI' AND mov.tipo_movimiento IS NULL THEN 'TRASLADO' " +
                       "    ELSE mov.tipo_movimiento " +
                       "  END AS tipo_movimiento, " +
                       "  mov.numero_comprobante, " +
                       "  CASE " +
                       "    WHEN ve.id IS NOT NULL THEN dev.precio_unitario " +
                       "    WHEN orden.id IS NOT NULL THEN detorden.precio_unitario " +
                       "    WHEN devv.id IS NOT NULL THEN devvt.precio_unitario " +
                       "    ELSE k.costo_promedio " +
                       "  END AS costo_promedio, " +
                       "  CASE " +
                       "    WHEN ve.id IS NOT NULL THEN dev.total " +
                       "    WHEN orden.id IS NOT NULL THEN detorden.total " +
                       "    WHEN devv.id IS NOT NULL THEN devvt.total_linea " +
                       "    ELSE k.total_costo " +
                       "  END AS total_costo, " +
                       "  ROW_NUMBER() OVER ( " +
                       "      PARTITION BY k.kardex_id " +
                       "      ORDER BY CASE WHEN mov.tipo_movimiento = 'CUENTA_POR_COBRAR' THEN 0 ELSE 1 END " +
                       "  ) AS rn " +
                       "FROM kardex k " +
                       "JOIN movimientos_inventario m ON k.movimiento_inventario_id = m.movimiento_inventario_id " +
                       "JOIN comprobantes_contables co ON co.id = m.comprobante_id " +
                       "JOIN producto_variantes po ON po.producto_variantes_id = k.producto_variante_id " +
                       "JOIN productos pod ON pod.producto_id = po.producto_id " +
                       "LEFT JOIN movimiento_cajero mov ON mov.numero_comprobante = CONCAT(co.prefijo, '-', m.consecutivo) " +
                       "LEFT JOIN ventas ve ON ve.comprobante_id = m.comprobante_id AND ve.consecutivo_comprobante = m.consecutivo " +
                       "LEFT JOIN detalles_venta dev ON dev.venta_id = ve.id " +
                       "LEFT JOIN ordenes_compra orden ON orden.comprobante_id = m.comprobante_id AND orden.consecutivo_comprobante = m.consecutivo " +
                       "LEFT JOIN detalles_orden_compra detorden ON detorden.orden_compra_id = orden.id " +
                       "LEFT JOIN devoluciones_venta devv ON devv.comprobante_id = m.comprobante_id AND devv.consecutivo_comprobante = m.consecutivo " +
                       "LEFT JOIN detalles_devolucion_venta devvt ON devvt.devolucion_id = devv.id " +
                       "JOIN bodegas b ON b.codigo = k.bodega_id " +
                       ") t " +
                       "WHERE rn = 1 " +
                       "AND (:desde IS NULL OR DATE(t.fecha_creacion) >= :desde) " +
                       "AND (:hasta IS NULL OR DATE(t.fecha_creacion) <= :hasta) " +
                       "ORDER BY fecha_creacion", nativeQuery = true)
        List<Object[]> getKardexReportRaw(@Param("desde") String desde, @Param("hasta") String hasta);

}
