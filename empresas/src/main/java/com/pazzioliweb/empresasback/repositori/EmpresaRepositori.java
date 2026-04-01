package com.pazzioliweb.empresasback.repositori;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.empresasback.entity.Empresa;
import com.pazzioliweb.productosmodule.entity.Bodegas;

public interface EmpresaRepositori extends JpaRepository<Empresa, Long> {
	

}
