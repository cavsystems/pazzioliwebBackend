package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.entity.ActivoFijo;
import com.pazzioliweb.comprobantesmodule.service.ActivoFijoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activos-fijos")
@CrossOrigin(origins = "*")
public class ActivoFijoController {

    private final ActivoFijoService service;

    public ActivoFijoController(ActivoFijoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ActivoFijo>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtener(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ActivoFijo activo) {
        try {
            return ResponseEntity.ok(service.crear(activo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ActivoFijo activo) {
        try {
            return ResponseEntity.ok(service.actualizar(id, activo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Activo eliminado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Baja/retiro del activo. Body opcional: { fecha, cuentaContrapartidaId }. */
    @PostMapping("/{id}/baja")
    public ResponseEntity<?> darDeBaja(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        try {
            java.time.LocalDate fecha = (body != null && body.get("fecha") != null)
                    ? java.time.LocalDate.parse(body.get("fecha").toString()) : null;
            Integer contrapartida = (body != null && body.get("cuentaContrapartidaId") != null
                    && !body.get("cuentaContrapartidaId").toString().isBlank())
                    ? Integer.parseInt(body.get("cuentaContrapartidaId").toString()) : null;
            return ResponseEntity.ok(service.darDeBaja(id, fecha, contrapartida));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Corre la depreciación del periodo y genera el asiento (idempotente por mes salvo regenerar=true). */
    @PostMapping("/correr-depreciacion")
    public ResponseEntity<?> correrDepreciacion(@RequestParam int anio, @RequestParam int mes,
                                                @RequestParam(defaultValue = "false") boolean regenerar) {
        try {
            ActivoFijoService.ResultadoDepreciacion r = service.correrDepreciacion(anio, mes, regenerar);
            return ResponseEntity.ok(Map.of(
                    "numeroAsiento", r.numeroAsiento,
                    "totalDepreciado", r.totalDepreciado,
                    "activosDepreciados", r.activosDepreciados,
                    "periodo", r.periodo,
                    "mensaje", "Depreciación registrada correctamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
