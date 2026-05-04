package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaContableRepository extends JpaRepository<CuentaContable, Integer> {
    List<CuentaContable> findByEstadoOrderByCodigoAsc(String estado);
    Optional<CuentaContable> findByCodigo(String codigo);
}
