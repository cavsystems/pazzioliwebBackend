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
import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteCreateDTO;
import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteDTO;
import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteResponseDTO;
import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteUpdateDTO;
import com.pazzioliweb.productosmodule.service.PreciosProductoVarianteService;

@RestController
@RequestMapping("/api/precios-producto-variante")
public class PreciosProductoVarianteController {

    private final PreciosProductoVarianteService service;

    public PreciosProductoVarianteController(PreciosProductoVarianteService service) {
        this.service = service;
    }

    @GetMapping("/listar")
    public ResponseEntity<PaginationResponse<PreciosProductoVarianteResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "preciosProductoId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PreciosProductoVarianteResponseDTO> resultado = service.listar(pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreciosProductoVarianteResponseDTO> obtener(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/crear-por-dto")
    public ResponseEntity<List<PreciosProductoVarianteResponseDTO>> crear(
            @RequestBody List<PreciosProductoVarianteCreateDTO> dtos) {

        List<PreciosProductoVarianteResponseDTO> resultados = service.crear(dtos);
        return ResponseEntity.ok(resultados);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<List<PreciosProductoVarianteResponseDTO>> actualizar(
            @RequestBody List<PreciosProductoVarianteUpdateDTO> dtos
    		) {
    		    return ResponseEntity.ok(service.actualizar(dtos));
    		}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = service.eliminar(id);
        return eliminado ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/variante/{varianteId}")
    public ResponseEntity<PaginationResponse<PreciosProductoVarianteDTO>> listarPreciosVariantes(
            @PathVariable Integer varianteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "precioId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PreciosProductoVarianteDTO> resultado =
        		service.listarPreciosVariantesProducto(varianteId, pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }

    @GetMapping("/variantes")
    public ResponseEntity<PaginationResponse<PreciosProductoVarianteDTO>> listarPreciosVariantesMultiples(
            @RequestParam List<Integer> varianteIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "precioId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PreciosProductoVarianteDTO> resultado =
                service.listarPreciosVariantesProductos(varianteIds, pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }
}
