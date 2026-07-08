package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.dtos.TipoComprobanteManualDTO;
import com.pazzioliweb.comprobantesmodule.service.TipoComprobanteManualService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tipos-comprobante-manual")
public class TipoComprobanteManualController {

    private final TipoComprobanteManualService service;

    public TipoComprobanteManualController(TipoComprobanteManualService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TipoComprobanteManualDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody TipoComprobanteManualDTO dto) {
        try { return ResponseEntity.ok(service.crear(dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody TipoComprobanteManualDTO dto) {
        try { return ResponseEntity.ok(service.actualizar(id, dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/{id}/inactivar")
    public ResponseEntity<?> inactivar(@PathVariable Integer id) {
        try { return ResponseEntity.ok(service.inactivar(id)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<?> activar(@PathVariable Integer id) {
        try { return ResponseEntity.ok(service.activar(id)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }
}
