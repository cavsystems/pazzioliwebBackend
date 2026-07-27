package com.pazzioliweb.movimientosinventariomodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventarioDetalle;

public interface MovimientoInventarioDetalleRepository extends JpaRepository<MovimientoInventarioDetalle, Long> {
        List<MovimientoInventarioDetalle> findByMovimiento_MovimientoId(Long id);

        @Query("SELECT d FROM MovimientoInventarioDetalle d LEFT JOIN FETCH d.productoVariante pv LEFT JOIN FETCH pv.producto WHERE d.movimiento.movimientoId = :movimientoId")
        List<MovimientoInventarioDetalle> findByMovimiento_MovimientoIdWithProducto(@Param("movimientoId") Long movimientoId);

}
