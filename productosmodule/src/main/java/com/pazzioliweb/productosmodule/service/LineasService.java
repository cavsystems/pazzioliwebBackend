package com.pazzioliweb.productosmodule.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.productosmodule.entity.Lineas;
import java.util.Optional;

public interface LineasService {
	Lineas crear(Lineas l);
	
	Lineas actualizar(Integer id, Lineas l);
	
	void eliminar(Integer id);
	
	Lineas buscarPorId(Integer id);
	
	Optional<Lineas> buscarPorNombre(String nombre);
	
	Page<Lineas> listar(String descripcion,Pageable pageable);
	
}
