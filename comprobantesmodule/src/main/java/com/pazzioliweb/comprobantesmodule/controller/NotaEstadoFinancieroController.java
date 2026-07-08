package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.dtos.NotaEstadoFinancieroDTO;
import com.pazzioliweb.comprobantesmodule.service.NotaEstadoFinancieroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notas-eef")
@CrossOrigin(origins = "*")
public class NotaEstadoFinancieroController {

    private final NotaEstadoFinancieroService service;

    public NotaEstadoFinancieroController(NotaEstadoFinancieroService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<NotaEstadoFinancieroDTO>> listar(@RequestParam(required = false) Integer anio) {
        return ResponseEntity.ok(service.listar(anio));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody NotaEstadoFinancieroDTO dto) {
        try { return ResponseEntity.ok(service.crear(dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody NotaEstadoFinancieroDTO dto) {
        try { return ResponseEntity.ok(service.actualizar(id, dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try { service.eliminar(id); return ResponseEntity.noContent().build(); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }
}
