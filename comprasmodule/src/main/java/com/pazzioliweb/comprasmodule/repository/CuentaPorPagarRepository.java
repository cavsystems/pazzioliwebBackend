package com.pazzioliweb.comprasmodule.repository;

import com.pazzioliweb.comprasmodule.entity.CuentaPorPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaPorPagarRepository extends JpaRepository<CuentaPorPagar, Long> {
    List<CuentaPorPagar> findByNumeroFactura(String numeroFactura);
    List<CuentaPorPagar> findByProveedor_TerceroIdAndEstadoIn(Integer proveedorId, List<String> estados);
}
