package com.pazzioliweb.productosmodule.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.productosmodule.entity.TipoProducto;
import java.util.Optional;

public interface TipoProductoService {
	TipoProducto crear(TipoProducto t);
	
	TipoProducto actualizar(Integer id, TipoProducto t);
	
	void eliminar(Integer id);
	
	TipoProducto buscarPorId(Integer id);
	
	Optional<TipoProducto> buscarPorNombre(String nombre);
	
	Page<TipoProducto> listar(Pageable pageable);
}
