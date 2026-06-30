package com.pazzioliweb.comprasmodule.repository;

import com.pazzioliweb.comprasmodule.entity.ManifestoImportacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ManifestoImportacionRepository extends JpaRepository<ManifestoImportacion, Long> {

    List<ManifestoImportacion> findByOrdenCompra_Id(Long ordenCompraId);

    Optional<ManifestoImportacion> findByNumeroDeclaracion(String numeroDeclaracion);

    @Query(value = """
        SELECT DISTINCT m.* FROM manifestos_importacion m
        JOIN ordenes_compra oc ON oc.id = m.orden_compra_id
        JOIN detalles_orden_compra doc ON doc.orden_compra_id = oc.id
        JOIN detalles_venta dv ON dv.codigo_barras = doc.codigo_barras
        WHERE dv.venta_id = :ventaId
        AND m.estado = 'ACTIVO'
    """, nativeQuery = true)
    List<ManifestoImportacion> findByVentaId(@Param("ventaId") Long ventaId);
}
