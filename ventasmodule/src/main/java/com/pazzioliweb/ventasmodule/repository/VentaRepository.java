package com.pazzioliweb.ventasmodule.repository;

import com.pazzioliweb.ventasmodule.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long>, JpaSpecificationExecutor<Venta> {

    Optional<Venta> findByNumeroVenta(String numeroVenta);

    /**
     * ¿La venta tiene factura electrónica AUTORIZADA por la DIAN? Nativa para no
     * acoplar ventasmodule a facturacionmodule (la tabla vive en el mismo schema).
     * Se usa como guard de anularVenta: un documento autorizado solo se anula
     * fiscalmente con Nota Crédito (devolución), no borrando el asiento local.
     */
    @Query(value = "SELECT COUNT(*) FROM facturas f WHERE f.venta_id = :ventaId AND f.estado_dian = 'AUTORIZADA'",
           nativeQuery = true)
    long countFacturasAutorizadasByVentaId(@Param("ventaId") Long ventaId);

    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.items WHERE v.numeroVenta = :numeroVenta")
    Optional<Venta> findByNumeroVentaWithItems(@Param("numeroVenta") String numeroVenta);

    @Query("SELECT v FROM Venta v WHERE v.estado IN ('PENDIENTE', 'COMPLETADA')")
    List<Venta> findVentasPendientes();

    @Query("SELECT DISTINCT v FROM Venta v LEFT JOIN FETCH v.items WHERE v.estado IN ('PENDIENTE', 'COMPLETADA') AND v.cliente.terceroId = :clienteId")
    List<Venta> findVentasByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT SUM(v.totalVenta) FROM Venta v WHERE v.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND v.estado = 'COMPLETADA'")
    Optional<Double> getTotalVentasByFecha(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT SUM(d.cantidad) FROM Venta v JOIN v.items d WHERE d.codigoProducto = :codigoProducto AND v.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND v.estado = 'COMPLETADA'")
    Optional<Long> getCantidadVendidaByProducto(@Param("codigoProducto") String codigoProducto, @Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT SUM(v.totalVenta) FROM Venta v WHERE v.cajero.cajeroId = :cajeroId AND v.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND v.estado = 'COMPLETADA'")
    Optional<Double> getTotalVentasByCajero(@Param("cajeroId") Integer cajeroId, @Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT MAX(v.id) FROM Venta v")
    Long getUltimaVentaId();

    List<Venta> findByNumeroVentaAndEstado(String numeroVenta, String estado);
}


