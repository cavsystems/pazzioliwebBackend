package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.dtos.CentroCostoDTO;
import com.pazzioliweb.comprobantesmodule.service.CentroCostoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/centros-costo")
public class CentroCostoController {

    private final CentroCostoService service;

    public CentroCostoController(CentroCostoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CentroCostoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CentroCostoDTO dto) {
        try { return ResponseEntity.ok(service.crear(dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<?> actualizar(@PathVariable Integer codigo, @RequestBody CentroCostoDTO dto) {
        try { return ResponseEntity.ok(service.actualizar(codigo, dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/{codigo}/inactivar")
    public ResponseEntity<?> inactivar(@PathVariable Integer codigo) {
        try { return ResponseEntity.ok(service.inactivar(codigo)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/{codigo}/activar")
    public ResponseEntity<?> activar(@PathVariable Integer codigo) {
        try { return ResponseEntity.ok(service.activar(codigo)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }
}
