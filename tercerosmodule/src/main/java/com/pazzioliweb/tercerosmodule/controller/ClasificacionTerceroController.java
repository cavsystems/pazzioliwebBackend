package com.pazzioliweb.tercerosmodule.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.pazzioliweb.tercerosmodule.entity.ClasificacionTercero;
import com.pazzioliweb.tercerosmodule.service.ClasificacionTerceroService;

@RestController
@RequestMapping("/api/clasificacionesTerceros")
public class ClasificacionTerceroController {
	private final ClasificacionTerceroService service;

    @Autowired
    public ClasificacionTerceroController(ClasificacionTerceroService service) {
        this.service = service;
    }

    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ClasificacionTercero> pageResult = service.listar(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("currentPage", pageResult.getNumber());
        response.put("totalItems", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClasificacionTercero> obtener(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClasificacionTercero> crear(@RequestBody ClasificacionTercero clasificacion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(clasificacion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClasificacionTercero> actualizar(@PathVariable Integer id, @RequestBody ClasificacionTercero clasificacion) {
        return service.buscarPorId(id)
                .map(actual -> {
                    clasificacion.setClasificacionTerceroId(id);
                    return ResponseEntity.ok(service.guardar(clasificacion));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (service.buscarPorId(id).isPresent()) {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
