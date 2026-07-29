package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.comprasmodule.dtos.ProductoActualizarCrearDTO;

import java.util.List;

public interface ProductoService {
    void actualizarOCrearProducto(ProductoActualizarCrearDTO productoDTO);

    /**
     * Crea/actualiza productos EN LOTES (una llamada al módulo de productos por
     * cada N productos, no una por producto). Indispensable para importaciones
     * masivas desde plantilla: con 3000 productos el patrón de una llamada HTTP
     * por producto tardaba minutos y colgaba la compra directa/legalización.
     */
    void actualizarOCrearProductos(List<ProductoActualizarCrearDTO> productos);

    void actualizarInventario(String codigoProducto, String codigoVariante, Integer cantidad, Integer bodegaId);
    boolean existeProducto(String codigo);
}
