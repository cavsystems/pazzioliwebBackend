package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.comprasmodule.dtos.ProductoActualizarCrearDTO;

import java.util.List;

public interface ProductoService {
    void actualizarOCrearProducto(ProductoActualizarCrearDTO productoDTO);
    void actualizarOCrearProductoBatch(List<ProductoActualizarCrearDTO> productosDTO);
    void actualizarInventario(String codigoProducto, String codigoVariante, Integer cantidad, Integer bodegaId);
    boolean existeProducto(String codigo);
}
