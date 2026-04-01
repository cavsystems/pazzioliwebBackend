package com.pazzioliweb.productosmodule.repositori;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.productosmodule.entity.Usuariobodega;
import com.pazzioliweb.usuariosbacken.entity.Tipopersona;

public interface UsuariobodegaRepository  extends JpaRepository<Usuariobodega, Long>{
	 @Transactional
	    @Modifying
	    void deleteByIdUsuariobodega(int codigo);
}
