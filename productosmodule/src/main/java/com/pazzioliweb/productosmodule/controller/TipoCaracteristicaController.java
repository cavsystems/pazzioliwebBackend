package com.pazzioliweb.productosmodule.controller;

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
import com.pazzioliweb.productosmodule.entity.TipoCaracteristica;
import com.pazzioliweb.productosmodule.service.TipoCaracteristicaService;

@RestController
@RequestMapping("/api/tipos-caracteristica")
public class TipoCaracteristicaController {
	private final TipoCaracteristicaService service;

    public TipoCaracteristicaController(TipoCaracteristicaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TipoCaracteristica> crear(@RequestBody TipoCaracteristica tipo) {
        return ResponseEntity.ok(service.crear(tipo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoCaracteristica> actualizar(
            @PathVariable Long id,
            @RequestBody TipoCaracteristica tipo
    ) {
        return ResponseEntity.ok(service.actualizar(id, tipo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoCaracteristica> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/listar")
    public ResponseEntity<PaginationResponse<TipoCaracteristica>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "tipoCaracteristicaId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "") String descripcion
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TipoCaracteristica> resultado = service.listar( descripcion,pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }
}
