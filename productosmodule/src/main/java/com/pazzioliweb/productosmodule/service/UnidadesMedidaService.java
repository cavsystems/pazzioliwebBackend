package com.pazzioliweb.productosmodule.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.productosmodule.dtos.UnidadMedidaCreateDTO;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaResponseDTO;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaUpdateDTO;
import java.util.Optional;

public interface UnidadesMedidaService {

	List<UnidadMedidaResponseDTO> crear(List<UnidadMedidaCreateDTO> dtos);
	
	UnidadMedidaResponseDTO actualizar(Integer id, UnidadMedidaUpdateDTO dto);
	
	UnidadMedidaResponseDTO obtenerPorId(Integer id);
	
	Page<UnidadMedidaResponseDTO> listar(String descripcion,Pageable pageable);
	
	Optional<UnidadMedidaResponseDTO> buscarPorCodigo(String codigo);
	
	void eliminar(Integer id);
}
