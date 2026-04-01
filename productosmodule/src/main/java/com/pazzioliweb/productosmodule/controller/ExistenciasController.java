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
import com.pazzioliweb.productosmodule.dtos.ExistenciasBodegaDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasUpdateDTO;
import com.pazzioliweb.productosmodule.service.ExistenciasService;

@RestController
@RequestMapping("/api/existencias")
public class ExistenciasController {
    private final ExistenciasService service;

    public ExistenciasController(ExistenciasService service) {
        this.service = service;
    }

    @PostMapping("/crear-por-dto")
    public ResponseEntity<List<ExistenciasResponseDTO>> crear(@RequestBody List<ExistenciasCreateDTO> dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExistenciasResponseDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody ExistenciasUpdateDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExistenciasResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/listar")
    public ResponseEntity<PaginationResponse<ExistenciasResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "existenciaId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ExistenciasResponseDTO> resultado = service.listar(pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }

    @GetMapping("/variante/{varianteId}")
    public ResponseEntity<PaginationResponse<ExistenciasResponseDTO>> listarPorVariante(
            @PathVariable Long varianteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "existenciaId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ExistenciasResponseDTO> resultado =
                service.listarPorVariante(varianteId, pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }

    @GetMapping("/bodega/{bodegaId}")
    public ResponseEntity<PaginationResponse<ExistenciasResponseDTO>> listarPorBodega(
            @PathVariable Integer bodegaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "existenciaId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ExistenciasResponseDTO> resultado =
                service.listarPorBodega(bodegaId, pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }
    @GetMapping("/variante-bodega/{varianteId}")
    public ResponseEntity<PaginationResponse<ExistenciasBodegaDTO>> listarConBodegaPorVariante(
            @PathVariable Long varianteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "existenciaId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ExistenciasBodegaDTO> resultado =
                service.listarExistenciasConNombreBodegaPorVariante(varianteId, pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }
}
