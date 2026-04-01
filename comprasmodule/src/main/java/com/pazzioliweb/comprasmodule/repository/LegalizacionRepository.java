package com.pazzioliweb.comprasmodule.repository;

import com.pazzioliweb.comprasmodule.entity.Legalizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalizacionRepository extends JpaRepository<Legalizacion, Long> {
    @Query("SELECT MAX(l.id) FROM Legalizacion l")
    Long findMaxId();
    Page<Legalizacion> findByProveedorId(Long proveedorId, Pageable pageable);
}
