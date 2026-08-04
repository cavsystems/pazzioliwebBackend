package com.pazzioliweb.movimientosinventariomodule.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventarioDetalle;

public interface MovimientoInventarioDetalleRepository extends JpaRepository<MovimientoInventarioDetalle, Long> {
        List<MovimientoInventarioDetalle> findByMovimiento_MovimientoId(Long id);

        // Se agregan bodegaOrigen/bodegaDestino al FETCH: son @ManyToOne EAGER por
        // defecto (JPA) y sin fetch-join Hibernate las resolvía con 1-2 SELECT extra
        // POR DETALLE (movimientos con cientos de líneas = cientos de queries extra
        // solo para los nombres de bodega).
        @Query("SELECT d FROM MovimientoInventarioDetalle d " +
               "LEFT JOIN FETCH d.productoVariante pv LEFT JOIN FETCH pv.producto " +
               "LEFT JOIN FETCH d.bodegaOrigen LEFT JOIN FETCH d.bodegaDestino " +
               "WHERE d.movimiento.movimientoId = :movimientoId")
        List<MovimientoInventarioDetalle> findByMovimiento_MovimientoIdWithProducto(@Param("movimientoId") Long movimientoId);

        /**
         * Versión EN BLOQUE de la anterior: todos los detalles (con producto/variante/
         * bodegas ya resueltos) de VARIOS movimientos en una sola consulta. Se usa en
         * el listado paginado de movimientos ("Ver historial"): antes se hacía 1 consulta
         * de detalles POR movimiento de la página (N+1); ahora es 1 sola consulta para
         * toda la página, agrupada en memoria por movimientoId.
         */
        @Query("SELECT d FROM MovimientoInventarioDetalle d " +
               "LEFT JOIN FETCH d.productoVariante pv LEFT JOIN FETCH pv.producto " +
               "LEFT JOIN FETCH d.bodegaOrigen LEFT JOIN FETCH d.bodegaDestino " +
               "WHERE d.movimiento.movimientoId IN :movimientoIds")
        List<MovimientoInventarioDetalle> findByMovimiento_MovimientoIdInWithProducto(@Param("movimientoIds") Collection<Long> movimientoIds);

}
