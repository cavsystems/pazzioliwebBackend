package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.entity.AsientoFallido;
import com.pazzioliweb.comprobantesmodule.repositori.AsientoFallidoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Endpoints para que el admin consulte y resuelva intentos fallidos de
 * generación de asiento contable. Cada fallo guarda módulo, documento
 * origen y motivo para que se pueda corregir manualmente.
 */
@RestController
@RequestMapping("/api/asientos-fallidos")
public class AsientoFallidoController {

    private final AsientoFallidoRepository repo;

    public AsientoFallidoController(AsientoFallidoRepository repo) {
        this.repo = repo;
    }

    /** Lista los fallos pendientes (no resueltos). */
    @GetMapping
    public ResponseEntity<List<AsientoFallido>> listarPendientes() {
        return ResponseEntity.ok(repo.findByResueltoFalseOrderByFechaIntentoDesc());
    }

    /** Lista todos los fallos por módulo (resueltos y pendientes). */
    @GetMapping("/modulo/{modulo}")
    public ResponseEntity<List<AsientoFallido>> listarPorModulo(@PathVariable String modulo) {
        return ResponseEntity.ok(repo.findByModuloOrderByFechaIntentoDesc(modulo));
    }

    /** Marca un fallo como resuelto (después de crear el asiento manual o decidir ignorar). */
    @PostMapping("/{id}/resolver")
    public ResponseEntity<AsientoFallido> resolver(@PathVariable Long id,
                                                    @RequestBody Map<String, String> body) {
        return repo.findById(id).map(f -> {
            f.setResuelto(true);
            f.setNotasResolucion(body.getOrDefault("notas", null));
            f.setFechaResolucion(LocalDateTime.now());
            return ResponseEntity.ok(repo.save(f));
        }).orElse(ResponseEntity.notFound().build());
    }
}
