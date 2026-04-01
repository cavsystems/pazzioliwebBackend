package com.pazzioliweb.productosmodule.repositori;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.entity.Usuariobodega;

public interface UsuariBodegaRepository  extends JpaRepository<Usuariobodega, Long> {
	

}
