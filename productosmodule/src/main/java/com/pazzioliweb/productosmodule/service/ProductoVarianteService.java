package com.pazzioliweb.productosmodule.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.productosmodule.dtos.ProductoInventarioDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteConDetallesDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteUpdateDTO;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;

import java.util.List;

public interface ProductoVarianteService {
	ProductoVarianteResponseDTO crear(ProductoVarianteCreateDTO dto);

	ProductoVarianteResponseDTO actualizar(Long id, ProductoVarianteUpdateDTO dto);

    void eliminar(Long id);

    ProductoVariante buscarPorId(Long id);

    Page<ProductoVarianteResponseDTO> listar(Pageable pageable);

    Page<ProductoVarianteResponseDTO> listarPorProducto(Integer productoId, Pageable pageable);
    
    Page<ProductoInventarioDTO> listarInventarioBasico(String consultarentrada,int bodega,int estadova,String estadoproduct,String productodes,Pageable pageable);
    
    Page<ProductoVarianteConDetallesDTO> listarConDetallesPorProducto(Integer productoId, Pageable pageable);

    Page<ProductoVarianteConDetallesDTO> listarConDetallesPorCodigos(List<String> codigos, Pageable pageable);

    List<ProductoInventarioDTO> listarInventarioBasicoPorDescripciones(List<String> descripciones, int estadova, String estadoproduct);

}
