package com.pazzioliweb.comprasmodule.service.impl;

import com.pazzioliweb.comprasmodule.client.ProductoClient;
import com.pazzioliweb.comprasmodule.dtos.ProductoActualizarCrearDTO;
import com.pazzioliweb.comprasmodule.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoServiceImpl.class);

    /**
     * Tamaño de lote por llamada al módulo de productos. Mantiene cada request
     * por debajo del read-timeout de Feign (60s) aun con productos con variantes.
     */
    private static final int TAMANO_LOTE = 200;

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
    public void actualizarOCrearProductos(List<ProductoActualizarCrearDTO> productos) {
        if (productos == null || productos.isEmpty()) return;
        int total = productos.size();
        for (int i = 0; i < total; i += TAMANO_LOTE) {
            List<ProductoActualizarCrearDTO> lote = productos.subList(i, Math.min(i + TAMANO_LOTE, total));
            productoClient.actualizarOCrearProducto(lote);
            if (total > TAMANO_LOTE) {
                log.info("[Productos] Lote procesado: {}/{} productos", Math.min(i + TAMANO_LOTE, total), total);
            }
        }
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
