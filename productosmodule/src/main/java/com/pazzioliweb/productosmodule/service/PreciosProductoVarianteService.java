package com.pazzioliweb.productosmodule.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteCreateDTO;
import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteDTO;
import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteResponseDTO;
import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteUpdateDTO;

public interface PreciosProductoVarianteService {
	
	List<PreciosProductoVarianteResponseDTO> crear(List<PreciosProductoVarianteCreateDTO> dto);

    Optional<PreciosProductoVarianteResponseDTO> obtenerPorId(Long id);

    Page<PreciosProductoVarianteResponseDTO> listar(Pageable pageable);

    List<PreciosProductoVarianteResponseDTO> actualizar(List<PreciosProductoVarianteUpdateDTO> dto);

    boolean eliminar(Long id);
    
    Page<PreciosProductoVarianteDTO> listarPreciosVariantesProducto(Integer varianteId, Pageable pageable);

    Page<PreciosProductoVarianteDTO> listarPreciosVariantesProductos(List<Integer> varianteIds, Pageable pageable);
}
