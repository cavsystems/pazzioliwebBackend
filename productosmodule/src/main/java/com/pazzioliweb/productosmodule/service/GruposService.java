package com.pazzioliweb.productosmodule.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.productosmodule.entity.Grupos;
import java.util.Optional;

public interface GruposService {
	Grupos crear(Grupos g);
	
	Grupos actualizar(Integer id, Grupos g);
	
	void eliminar(Integer id);
	
	Grupos buscarPorId(Integer id);
	
	Optional<Grupos> buscarPorNombre(String nombre);
	
	Page<Grupos> listar(String descripcion,	Pageable pageable);
}
