package com.pazzioliweb.comprasmodule.repository;

import com.pazzioliweb.comprasmodule.entity.DevolucionCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DevolucionCompraRepository extends JpaRepository<DevolucionCompra, Long> {

    List<DevolucionCompra> findByOrdenCompra_IdOrderByIdDesc(Long ordenCompraId);

    long countByOrdenCompra_Id(Long ordenCompraId);

    /** Total de unidades ya devueltas (en devoluciones REGISTRADAS) de un detalle de compra. */
    @Query("""
           SELECT COALESCE(SUM(d.cantidadDevuelta), 0)
           FROM DetalleDevolucionCompra d
           WHERE d.detalleOrdenCompra.id = :detalleId
             AND d.devolucionCompra.estado = 'REGISTRADA'
           """)
    Integer totalDevueltoPorDetalle(@Param("detalleId") Long detalleId);
}
