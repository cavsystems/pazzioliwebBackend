package com.pazzioliweb.productosmodule.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.commonbacken.dtos.response.PaginationResponse;
import com.pazzioliweb.productosmodule.dtos.LineaProductosDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoUpdateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoActualizarCrearDTO;
import com.pazzioliweb.productosmodule.entity.Productos;
import com.pazzioliweb.productosmodule.service.ProductosService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
//@RequiredArgsConstructor
public class ProductosController {

    private final ProductosService productosService;
    
    public ProductosController(ProductosService productosService) {
        this.productosService = productosService;
    }
    
    // ------------------------------------------------------
    // LISTAR PAGINADO
    // ------------------------------------------------------
    @GetMapping("/listar")
    public ResponseEntity<PaginationResponse<ProductoResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "descripcion") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {
    	
    	Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Productos> resultado = productosService.listar(pageable);
        
        Page<ProductoResponseDTO> dtoPage = resultado.map(productosService::convertirAResponse);

        PaginationResponse<ProductoResponseDTO> response =
                new PaginationResponse<ProductoResponseDTO>(
                        dtoPage.getContent(),
                        dtoPage.getNumber(),
                        dtoPage.getTotalElements(),
                        dtoPage.getTotalPages()
                );

        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------
    // BUSCAR POR FILTRO
    // ------------------------------------------------------
    @GetMapping("/buscar")
    public ResponseEntity<PaginationResponse<Productos>> buscar(
            @RequestParam String termino,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productoId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {
    	
    	Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Productos> resultados = productosService.buscarPorFiltro(
                termino, pageable
        );

        PaginationResponse<Productos> response = new PaginationResponse<>(
                resultados.getContent(),
                resultados.getNumber(),
                resultados.getTotalElements(),
                resultados.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------
    // OBTENER POR ID
    // ------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtener(@PathVariable Integer id) {
        return productosService.buscarPorIdConRelaciones(id)
                .map(p -> ResponseEntity.ok(productosService.convertirAResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------------------------------------------
    // CREAR
    // ------------------------------------------------------
    @PostMapping("/crear-por-dto")
    public ResponseEntity<ProductoResponseDTO> crearPorDTO(@RequestBody ProductoCreateDTO dto) {
        Productos creado = productosService.guardarDesdeDTO(dto);
        ProductoResponseDTO response = productosService.convertirAResponse(creado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ------------------------------------------------------
    // ACTUALIZAR
    // ------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody ProductoUpdateDTO dto) {

        Productos actualizado = productosService.actualizarDesdeDTO(id, dto);

        ProductoResponseDTO response = productosService.convertirAResponse(actualizado);

        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------
    // ELIMINAR
    // ------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
    	productosService.eliminar(id);
        return ResponseEntity.noContent().build(); // 204 sin cuerpo
    }
    
    // ------------------------------------------------------
    // LISTA TOTAL INVENTARIO POR CADA LINEA, DE TODAS LAS BODEGAS
    // ------------------------------------------------------
    @GetMapping("/listarTotalInventarioPorLineaTodasBodegas")
    public ResponseEntity<PaginationResponse<LineaProductosDTO>> listarTotalInventarioPorLineasTodasBodegas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "descripcion") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {
    	
    	Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<LineaProductosDTO> resultados = productosService.listarTotalesPorLineaTodasBodegas(pageable);

        PaginationResponse<LineaProductosDTO> response = new PaginationResponse<>(
                resultados.getContent(),
                resultados.getNumber(),
                resultados.getTotalElements(),
                resultados.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------
    // LISTA TOTAL INVENTARIO POR CADA LINEA, DE UNA BODEGA ESPECIFICA
    // ------------------------------------------------------
    @GetMapping("/listarTotalInventarioPorLineaXBodega/{bodegaId}")
    public ResponseEntity<PaginationResponse<LineaProductosDTO>> listarTotalesPorLineaXBodegaId(
    		@PathVariable Integer bodegaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "descripcion") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {
    	
    	Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<LineaProductosDTO> resultados = productosService.listarTotalesPorLineaXBodegaId(bodegaId,pageable);

        PaginationResponse<LineaProductosDTO> response = new PaginationResponse<>(
                resultados.getContent(),
                resultados.getNumber(),
                resultados.getTotalElements(),
                resultados.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }
    
    // ------------------------------------------------------
    // ACTUALIZAR O CREAR PRODUCTO (desde compras)
    // ------------------------------------------------------
    @PostMapping("/actualizar-o-crear")
    public ResponseEntity<Void> actualizarOCrear(@RequestBody List<ProductoActualizarCrearDTO> dtos) {
        productosService.actualizarOCrearProducto(dtos);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/actualizar-inventario")
    public ResponseEntity<Void> actualizarInventario(
            @RequestParam("codigoProducto") String codigoProducto,
            @RequestParam("codigoVariante") String codigoVariante,
            @RequestParam("cantidad") Integer cantidad,
            @RequestParam("bodegaId") Integer bodegaId) {
        productosService.actualizarInventario(codigoProducto, codigoVariante, cantidad, bodegaId);
        return ResponseEntity.ok().build();
    }
    
}