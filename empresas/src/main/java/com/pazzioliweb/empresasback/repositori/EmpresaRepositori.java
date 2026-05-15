package com.pazzioliweb.empresasback.repositori;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.empresasback.entity.Empresa;
import com.pazzioliweb.productosmodule.entity.Bodegas;

import java.util.Optional;

public interface EmpresaRepositori extends JpaRepository<Empresa, Long> {
	
 Optional<Empresa> findByCodigo(int id);
}
