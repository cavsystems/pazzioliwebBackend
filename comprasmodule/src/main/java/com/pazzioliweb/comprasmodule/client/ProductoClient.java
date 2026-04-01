package com.pazzioliweb.comprasmodule.client;

import com.pazzioliweb.comprasmodule.dtos.ProductoRequestDTO;
import com.pazzioliweb.comprasmodule.dtos.ProductoActualizarCrearDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "productos-module", url = "${productos.module.url}", configuration = FeignConfig.class)
public interface ProductoClient {

    @PostMapping("/api/productos/actualizar-o-crear")
    void actualizarOCrearProducto(@RequestBody List<ProductoActualizarCrearDTO> dtos);

    @PostMapping("/api/productos/actualizar-inventario")
    void actualizarInventario(
            @RequestParam("codigoProducto") String codigoProducto,
            @RequestParam("codigoVariante") String codigoVariante,
            @RequestParam("cantidad") Integer cantidad,
            @RequestParam("bodegaId") Integer bodegaId);

    @GetMapping("/api/productos/existe/{codigo}")
    boolean existeProducto(@PathVariable("codigo") String codigo);

    @GetMapping("/api/grupos/id/{nombre}")
    Integer getGrupoId(@PathVariable("nombre") String nombre);

    @GetMapping("/api/lineas/id/{nombre}")
    Integer getLineaId(@PathVariable("nombre") String nombre);

    @GetMapping("/api/tipo-producto/id/{nombre}")
    Integer getTipoProductoId(@PathVariable("nombre") String nombre);

    @GetMapping("/api/unidadesMedida/id/{codigo}")
    Integer getUnidadMedidaId(@PathVariable("codigo") String codigo);

}
