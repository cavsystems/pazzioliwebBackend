package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.dtos.CuentaContableDTO;
import com.pazzioliweb.comprobantesmodule.service.CuentaContableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas-contables")
public class CuentaContableController {

    private final CuentaContableService service;

    public CuentaContableController(CuentaContableService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CuentaContableDTO>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<CuentaContableDTO>> listarActivas() {
        return ResponseEntity.ok(service.listarActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        try { return ResponseEntity.ok(service.buscarPorId(id)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CuentaContableDTO dto) {
        try { return ResponseEntity.ok(service.crear(dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody CuentaContableDTO dto) {
        try { return ResponseEntity.ok(service.actualizar(id, dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try { service.eliminar(id); return ResponseEntity.noContent().build(); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }
}
