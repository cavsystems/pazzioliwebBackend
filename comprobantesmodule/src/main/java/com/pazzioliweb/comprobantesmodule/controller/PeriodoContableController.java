package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.entity.PeriodoContable;
import com.pazzioliweb.comprobantesmodule.service.PeriodoContableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/periodos-contables")
@CrossOrigin(origins = "*")
public class PeriodoContableController {

    private final PeriodoContableService service;

    public PeriodoContableController(PeriodoContableService service) {
        this.service = service;
    }

    /** Lista todos los periodos, o de un año específico. */
    @GetMapping
    public ResponseEntity<List<PeriodoContable>> listar(@RequestParam(required = false) Integer anio) {
        if (anio != null) {
            return ResponseEntity.ok(service.asegurarPeriodosDelAnio(anio));
        }
        return ResponseEntity.ok(service.listarTodos());
    }

    /** Asegura las 12 filas del año actual (útil al primer arranque del módulo). */
    @PostMapping("/inicializar")
    public ResponseEntity<List<PeriodoContable>> inicializar(@RequestParam(required = false) Integer anio) {
        int a = anio != null ? anio : LocalDate.now().getYear();
        return ResponseEntity.ok(service.asegurarPeriodosDelAnio(a));
    }

    /** Cierra un periodo (mes). Requiere usuario y opcionalmente observaciones. */
    @PostMapping("/cerrar")
    public ResponseEntity<PeriodoContable> cerrar(@RequestBody Map<String, Object> body) {
        int anio = ((Number) body.get("anio")).intValue();
        int mes  = ((Number) body.get("mes")).intValue();
        String usuario = (String) body.getOrDefault("usuario", "sistema");
        String obs = (String) body.getOrDefault("observaciones", null);
        return ResponseEntity.ok(service.cerrar(anio, mes, usuario, obs));
    }

    /** Reabre un periodo cerrado (requiere motivo). */
    @PostMapping("/reabrir")
    public ResponseEntity<PeriodoContable> reabrir(@RequestBody Map<String, Object> body) {
        int anio = ((Number) body.get("anio")).intValue();
        int mes  = ((Number) body.get("mes")).intValue();
        String usuario = (String) body.getOrDefault("usuario", "sistema");
        String motivo = (String) body.getOrDefault("motivo", "Sin motivo");
        return ResponseEntity.ok(service.reabrir(anio, mes, usuario, motivo));
    }

    /** Consulta puntual: ¿está cerrado un periodo X? */
    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> estado(@RequestParam int anio, @RequestParam int mes) {
        boolean cerrado = service.estaCerrado(LocalDate.of(anio, mes, 1));
        return ResponseEntity.ok(Map.of(
                "anio", anio,
                "mes", mes,
                "cerrado", cerrado,
                "estado", cerrado ? "CERRADO" : "ABIERTO"
        ));
    }
}
