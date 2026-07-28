package com.pazzioliweb.comprasmodule.repository;

import com.pazzioliweb.comprasmodule.entity.Legalizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LegalizacionRepository extends JpaRepository<Legalizacion, Long> {
    @Query("SELECT MAX(l.id) FROM Legalizacion l")
    Long findMaxId();
    Page<Legalizacion> findByProveedorId(Long proveedorId, Pageable pageable);
    
    @Query("SELECT l FROM Legalizacion l LEFT JOIN FETCH l.ordenCompra oc LEFT JOIN FETCH oc.proveedor LEFT JOIN FETCH oc.items LEFT JOIN FETCH l.comprobante")
    List<Legalizacion> findAllWithRelations();
    
    @Query("SELECT l FROM Legalizacion l LEFT JOIN FETCH l.ordenCompra oc LEFT JOIN FETCH oc.proveedor LEFT JOIN FETCH oc.items LEFT JOIN FETCH l.comprobante WHERE l.proveedorId = :proveedorId")
    Page<Legalizacion> findByProveedorIdWithRelations(Long proveedorId, Pageable pageable);
}
