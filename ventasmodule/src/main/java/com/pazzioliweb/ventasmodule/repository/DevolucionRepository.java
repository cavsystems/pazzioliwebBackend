package com.pazzioliweb.ventasmodule.repository;

import com.pazzioliweb.ventasmodule.entity.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {

    List<Devolucion> findByVenta_Id(Long ventaId);

    Optional<Devolucion> findByNumeroDevolucion(String numeroDevolucion);

    /** Cuenta devoluciones de una venta para generar el número correlativo */
    long countByVenta_Id(Long ventaId);

    /** Todas las devoluciones de un cajero específico */
    List<Devolucion> findByCajero_CajeroId(Integer cajeroId);

    /** Devoluciones de un cajero en un rango de fechas */
    @Query("""
           SELECT d FROM Devolucion d
           WHERE d.cajero.cajeroId = :cajeroId
             AND d.fechaCreacion BETWEEN :fechaInicio AND :fechaFin
           ORDER BY d.fechaCreacion DESC, d.id DESC
           """)
    List<Devolucion> findByCajeroAndFechas(
            @Param("cajeroId")     Integer   cajeroId,
            @Param("fechaInicio")  LocalDate fechaInicio,
            @Param("fechaFin")     LocalDate fechaFin);

    /** Devoluciones en un rango de fechas (todos los cajeros) */
    @Query("""
           SELECT d FROM Devolucion d
           WHERE d.fechaCreacion BETWEEN :fechaInicio AND :fechaFin
           ORDER BY d.fechaCreacion DESC, d.id DESC
           """)
    List<Devolucion> findByFechas(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin")    LocalDate fechaFin);

    /** Total de cantidades ya devueltas de un DetalleVenta específico */
    @Query("""
           SELECT COALESCE(SUM(d.cantidadDevuelta), 0)
           FROM DetalleDevolucion d
           WHERE d.detalleVenta.id = :detalleVentaId
             AND d.devolucion.estado = 'REGISTRADA'
           """)
    Integer totalDevueltoPorDetalle(@Param("detalleVentaId") Long detalleVentaId);
}

