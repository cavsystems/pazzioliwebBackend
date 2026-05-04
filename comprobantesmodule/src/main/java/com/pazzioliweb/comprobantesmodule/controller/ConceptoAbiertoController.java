package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.dtos.ConceptoAbiertoDTO;
import com.pazzioliweb.comprobantesmodule.service.ConceptoAbiertoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conceptos-abiertos")
public class ConceptoAbiertoController {

    private final ConceptoAbiertoService service;

    public ConceptoAbiertoController(ConceptoAbiertoService service) {
        this.service = service;
    }

    /** Lista todos. Útil para pantalla de administración. */
    @GetMapping
    public ResponseEntity<List<ConceptoAbiertoDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    /** Lista los activos cuyo tipo coincida con el solicitado o sean AMBOS. */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> listarPorTipo(@PathVariable String tipo) {
        try { return ResponseEntity.ok(service.listarPorTipo(tipo)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.buscarPorId(id)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ConceptoAbiertoDTO dto) {
        try { return ResponseEntity.ok(service.crear(dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ConceptoAbiertoDTO dto) {
        try { return ResponseEntity.ok(service.actualizar(id, dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try { service.eliminar(id); return ResponseEntity.noContent().build(); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }
}
