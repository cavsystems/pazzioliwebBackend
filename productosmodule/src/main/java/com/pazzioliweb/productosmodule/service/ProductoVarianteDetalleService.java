package com.pazzioliweb.productosmodule.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.productosmodule.dtos.ProductoVarianteDetalleCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteDetalleResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteDetalleUpdateDTO;
import com.pazzioliweb.productosmodule.entity.ProductoVarianteDetalle;

public interface ProductoVarianteDetalleService {
	List<ProductoVarianteDetalleResponseDTO> crear(List<ProductoVarianteDetalleCreateDTO> detalle);

	ProductoVarianteDetalleResponseDTO actualizarDesdeDTO(Long id, ProductoVarianteDetalleUpdateDTO detalle);

    void eliminar(Long id);

    ProductoVarianteDetalle buscarPorId(Long id);

    Page<ProductoVarianteDetalleResponseDTO> listar(Pageable pageable);

    Page<ProductoVarianteDetalleResponseDTO> listarPorVariante(Long varianteId, Pageable pageable);
    
}
