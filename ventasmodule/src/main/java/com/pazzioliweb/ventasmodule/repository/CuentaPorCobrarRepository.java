package com.pazzioliweb.ventasmodule.repository;

import com.pazzioliweb.ventasmodule.entity.CuentaPorCobrar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaPorCobrarRepository extends JpaRepository<CuentaPorCobrar, Long> {

    List<CuentaPorCobrar> findByEstado(String estado);

    List<CuentaPorCobrar> findByCliente_TerceroId(Integer clienteId);

    List<CuentaPorCobrar> findByNumeroVenta(String numeroVenta);

    List<CuentaPorCobrar> findByEstadoIn(List<String> estados);
}

