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

        @Query(value = "SELECT fecha_creacion, numerofactura , tipo_movimiento as movimiento,tipoentrada as tipo_movimiento , tipo, descripcion AS Producto, " +
                       "entrada, salida, costo_promedio, total_costo, saldo " +
                       "FROM (" +
                       "SELECT " +
                       "  m.tipo_movimiento as tipoentrada, " +
                       "  co.prefijo, " +
                       "  m.consecutivo, " +
                       "  co.tipo_movimiento AS tipo, " +
                       "  CONCAT(TRIM(pod.descripcion), '  ', IFNULL(TRIM(po.referencia_variantes), '')) AS descripcion, " +
                       "  k.entrada, " +
                       "  k.salida, " +
                       "  k.saldo, " +
                       "  k.costo_unitario, " +
                       "  k.costo_promedio, " +
                       "  k.total_costo, " +
                       "  k.fecha_creacion, " +
                       "  CONCAT(co.prefijo, '-', m.consecutivo) AS numerofactura, " +
                       "  mov.movimiento_cajero_id, " +
                       "  mov.detalle_cajero_id, " +
                       "  mov.cajero_id, " +
                       "  CASE " +
                       "    WHEN co.prefijo = 'VC' THEN 'CUENTA_POR_COBRAR' " +
                       "    WHEN co.tipo_movimiento = 'CC' AND mov.tipo_movimiento IS NULL THEN 'COMPRA' " +
                       "    WHEN co.tipo_movimiento = 'CR' AND mov.tipo_movimiento IS NULL THEN 'COMPRA' " +
                       "    WHEN co.tipo_movimiento = 'EI' AND mov.tipo_movimiento IS NULL THEN 'ENTRADA' " +
                       "    ELSE mov.tipo_movimiento " +
                       "  END AS tipo_movimiento, " +
                       "  mov.numero_comprobante, " +
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
                       ") t " +
                       "WHERE rn = 1 " +
                       "AND (:desde IS NULL OR DATE(t.fecha_creacion) >= :desde) " +
                       "AND (:hasta IS NULL OR DATE(t.fecha_creacion) <= :hasta) " +
                       "ORDER BY fecha_creacion", nativeQuery = true)
        List<Object[]> getKardexReportRaw(@Param("desde") String desde, @Param("hasta") String hasta);

}
