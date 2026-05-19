package com.pazzioliweb.metodospagomodule.repositori;

import com.pazzioliweb.metodospagomodule.entity.CuentaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuentaBancariaRepository extends JpaRepository<CuentaBancaria, Long> {
    List<CuentaBancaria> findByActivoTrueOrderByBancoAscNombreAsc();
    List<CuentaBancaria> findByBancoIgnoreCase(String banco);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
