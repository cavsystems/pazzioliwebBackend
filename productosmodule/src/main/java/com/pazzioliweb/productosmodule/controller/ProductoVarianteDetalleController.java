package com.pazzioliweb.productosmodule.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteDetalleCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteDetalleResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteDetalleUpdateDTO;
import com.pazzioliweb.productosmodule.entity.ProductoVarianteDetalle;
import com.pazzioliweb.productosmodule.service.ProductoVarianteDetalleService;

@RestController
@RequestMapping("/api/variante-detalle")
public class ProductoVarianteDetalleController {
	private final ProductoVarianteDetalleService detalleService;

    public ProductoVarianteDetalleController(ProductoVarianteDetalleService detalleService) {
        this.detalleService = detalleService;
    }

    // ------------------------------------
    // CRUD
    // ------------------------------------

    @PostMapping("/crear-por-dto")
    public ResponseEntity<List<ProductoVarianteDetalleResponseDTO>>crear(
    		@RequestBody List<ProductoVarianteDetalleCreateDTO> dto) {
        return ResponseEntity.ok(detalleService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoVarianteDetalleResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody ProductoVarianteDetalleUpdateDTO dto) {
        return ResponseEntity.ok(detalleService.actualizarDesdeDTO(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detalleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoVarianteDetalle> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(detalleService.buscarPorId(id));
    }

    // ------------------------------------
    // LISTADO GENERAL (ESTÁNDAR)
    // ------------------------------------

    @GetMapping("/listar")
    public ResponseEntity<PaginationResponse<ProductoVarianteDetalleResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productoVariantesDetalleId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductoVarianteDetalleResponseDTO> resultado =
                detalleService.listar(pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }

    // ------------------------------------
    // LISTADO POR VARIANTE
    // ------------------------------------

    @GetMapping("/variante/{varianteId}")
    public ResponseEntity<PaginationResponse<ProductoVarianteDetalleResponseDTO>> listarPorVariante(
            @PathVariable Long varianteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productoVariantesDetalleId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductoVarianteDetalleResponseDTO> resultado =
                detalleService.listarPorVariante(varianteId, pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }
}
