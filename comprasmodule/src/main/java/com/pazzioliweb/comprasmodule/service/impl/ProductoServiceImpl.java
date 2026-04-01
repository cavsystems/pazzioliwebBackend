package com.pazzioliweb.comprasmodule.service.impl;

import com.pazzioliweb.comprasmodule.client.ProductoClient;
import com.pazzioliweb.comprasmodule.dtos.ProductoActualizarCrearDTO;
import com.pazzioliweb.comprasmodule.dtos.ProductoRequestDTO;
import com.pazzioliweb.comprasmodule.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoClient productoClient;

    @Autowired
    public ProductoServiceImpl(ProductoClient productoClient) {
        this.productoClient = productoClient;
    }

    @Override
    public void actualizarOCrearProducto(ProductoActualizarCrearDTO productoDTO) {
        // Llamar al servicio de productos para crear o actualizar el producto
        productoClient.actualizarOCrearProducto(List.of(productoDTO));
    }

    @Override
    public void actualizarInventario(String codigoProducto, String codigoVariante, Integer cantidad, Integer bodegaId) {
        // Llamar al servicio de productos para actualizar el inventario
        productoClient.actualizarInventario(codigoProducto, codigoVariante, cantidad, bodegaId);
    }

    @Override
    public boolean existeProducto(String codigo) {
        // Verificar si el producto existe
        return productoClient.existeProducto(codigo);
    }
}
