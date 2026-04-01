package com.pazzioliweb.productosmodule.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.productosmodule.dtos.LineaProductosDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoUpdateDTO;
import com.pazzioliweb.productosmodule.entity.Productos;
import com.pazzioliweb.productosmodule.dtos.ProductoActualizarCrearDTO;

public interface ProductosService {

    //Productos guardar(Productos producto);

    Optional<Productos> buscarPorId(Integer id);

    Optional<Productos> buscarPorIdConRelaciones(Integer id);

    Page<Productos> listar(Pageable pageable);

    Page<Productos> buscarPorFiltro(String busqueda, Pageable pageable);
    
    void eliminar(Integer id);
    
    Productos guardarDesdeDTO(ProductoCreateDTO dto);
    
    Productos actualizarDesdeDTO(Integer id, ProductoUpdateDTO dto);
    
    ProductoResponseDTO convertirAResponse(Productos peoductos);
    
    Page<LineaProductosDTO> listarTotalesPorLineaTodasBodegas(Pageable pageable);
    
    Page<LineaProductosDTO> listarTotalesPorLineaXBodegaId(Integer bodegaId, Pageable pageable);
    
    void actualizarOCrearProducto(List<ProductoActualizarCrearDTO> dtos);

    void actualizarInventario(String codigoProducto, String codigoVariante, Integer cantidad, Integer bodegaId);
}
