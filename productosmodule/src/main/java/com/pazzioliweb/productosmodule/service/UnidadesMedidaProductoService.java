package com.pazzioliweb.productosmodule.service;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.productosmodule.dtos.UnidadMedidaProductoCreateDTO;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaProductoResponseDTO;

public interface UnidadesMedidaProductoService {

	UnidadMedidaProductoResponseDTO crear(UnidadMedidaProductoCreateDTO dto);

    UnidadMedidaProductoResponseDTO obtenerPorId(Integer productoId, Integer unidadMedidaId);

    Page<UnidadMedidaProductoResponseDTO> listar(Pageable pageable);

    Page<UnidadMedidaProductoResponseDTO> listarPorProducto(Integer productoId, Pageable pageable);

    void eliminar(Integer productoId, Integer unidadMedidaId);
}
