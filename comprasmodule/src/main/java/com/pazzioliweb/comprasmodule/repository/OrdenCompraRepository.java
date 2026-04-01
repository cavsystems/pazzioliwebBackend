package com.pazzioliweb.comprasmodule.repository;

import com.pazzioliweb.comprasmodule.dtos.EstadisticasProveedorDTO;
import com.pazzioliweb.comprasmodule.dtos.EstadisticasPorEstadoDTO;
import com.pazzioliweb.comprasmodule.entity.OrdenCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

    Optional<OrdenCompra> findByNumeroOrden(String numeroOrden);
    Page<OrdenCompra> findByNumeroOrdenStartingWithIgnoreCase(String numeroOrden, Pageable pageable);
    @Query("SELECT o FROM OrdenCompra o WHERE " +
            "(:estado IS NULL OR o.estado = :estado) AND " +
            "(:fechaDesde IS NULL OR o.fechaEmision >= :fechaDesde) AND " +
            "(:fechaHasta IS NULL OR o.fechaEmision <= :fechaHasta) AND " +
            "(:proveedorId IS NULL OR o.proveedor.terceroId = :proveedorId)")
    Page<OrdenCompra> buscarConFiltros(
            @Param("estado") String estado,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta,
            @Param("proveedorId") Integer proveedorId,
            Pageable pageable
    );

    @Query("SELECT o FROM OrdenCompra o WHERE " +
            "(:estado IS NULL OR o.estado = :estado) AND " +
            "(:fechaDesde IS NULL OR o.fechaEmision >= :fechaDesde) AND " +
            "(:fechaHasta IS NULL OR o.fechaEmision <= :fechaHasta)")
    Page<OrdenCompra> buscarConFiltrossinpro(
            @Param("estado") String estado,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta,

            Pageable pageable
    );

    @Query("SELECT o FROM OrdenCompra o WHERE o.estado IN ('PENDIENTE', 'RECIBIDA_PARCIAL')")
    List<OrdenCompra> findOrdenesPendientes();

    @Query("SELECT DISTINCT o FROM OrdenCompra o LEFT JOIN FETCH o.items WHERE o.estado = 'PENDIENTE' AND o.proveedor.terceroId = :proveedorId")
    List<OrdenCompra> findOrdenesPendientesByProveedorId(@Param("proveedorId") Integer proveedorId);

    @Query("SELECT o FROM OrdenCompra o LEFT JOIN FETCH o.items WHERE o.numeroOrden = :numeroOrden")
    Optional<OrdenCompra> findByNumeroOrdenWithItems(@Param("numeroOrden") String numeroOrden);

    @Query("SELECT COALESCE(SUM(o.totalOrdenCompra), 0) FROM OrdenCompra o WHERE " +
            "o.bodega.codigo = :bodegaId AND o.fechaEmision BETWEEN :fechaInicio AND :fechaFin")
    Double sumTotalByPeriodo(
            @Param("bodegaId") Integer bodegaId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT COUNT(o) FROM OrdenCompra o WHERE " +
            "o.bodega.codigo = :bodegaId AND o.fechaEmision BETWEEN :fechaInicio AND :fechaFin")
    Long countByPeriodo(
            @Param("bodegaId") Integer bodegaId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT NEW com.pazzioliweb.comprasmodule.dtos.EstadisticasPorEstadoDTO(o.estado, COUNT(o)) " +
            "FROM OrdenCompra o WHERE o.bodega.codigo = :bodegaId AND o.fechaEmision BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY o.estado")
    List<EstadisticasPorEstadoDTO> countByEstadoAndPeriodo(
            @Param("bodegaId") Integer bodegaId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT NEW com.pazzioliweb.comprasmodule.dtos.EstadisticasProveedorDTO(" +
            "p.terceroId, p.razonSocial, COUNT(o), COALESCE(SUM(o.totalOrdenCompra), 0)) " +
            "FROM OrdenCompra o JOIN o.proveedor p " +
            "WHERE o.bodega.codigo = :bodegaId AND o.fechaEmision BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY p.terceroId, p.razonSocial " +
            "ORDER BY SUM(o.totalOrdenCompra) DESC")
    List<EstadisticasProveedorDTO> findTopProveedores(
            @Param("bodegaId") Integer bodegaId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            Pageable pageable
    );

    default List<EstadisticasProveedorDTO> findTopProveedores(
            Integer bodegaId, LocalDate fechaInicio, LocalDate fechaFin, int limit) {
        return findTopProveedores(
                bodegaId,
                fechaInicio,
                fechaFin,
                PageRequest.of(0, limit)
        );
    }

    @Query("SELECT o FROM OrdenCompra o WHERE " +
            "(:bodegaId IS NULL OR o.bodega.codigo = :bodegaId) AND " +
            "(:proveedorId IS NULL OR o.proveedor.terceroId = :proveedorId) AND " +
            "o.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND " +
            "(:estado IS NULL OR o.estado = :estado)")
    Page<OrdenCompra> findHistorialCompras(
            @Param("bodegaId") Integer bodegaId,
            @Param("proveedorId") Integer proveedorId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("estado") String estado,
            Pageable pageable
    );

    @Query("SELECT MAX(o.id) FROM OrdenCompra o")
    Optional<Long> findMaxId();
}
