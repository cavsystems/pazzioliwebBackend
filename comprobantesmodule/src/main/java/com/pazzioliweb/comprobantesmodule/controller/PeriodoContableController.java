package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.entity.PeriodoContable;
import com.pazzioliweb.comprobantesmodule.service.CierreAnualService;
import com.pazzioliweb.comprobantesmodule.service.PeriodoContableService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private CierreAnualService cierreAnualService;

    public PeriodoContableController(PeriodoContableService service) {
        this.service = service;
    }

    /**
     * Ejecuta el cierre anual: cierra cuentas de resultado contra 3605 y traslada
     * el resultado a 3705 al 01-Ene del año siguiente. Requiere que los 12 meses
     * del año estén cerrados previamente.
     */
    @PostMapping("/cierre-anual")
    public ResponseEntity<CierreAnualService.ResultadoCierre> cierreAnual(@RequestBody Map<String, Object> body) {
        int anio = ((Number) body.get("anio")).intValue();
        String usuario = (String) body.getOrDefault("usuario", "sistema");
        return ResponseEntity.ok(cierreAnualService.cerrarAnio(anio, usuario));
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

    /**
     * Cierra un grupo de meses: bimestre (2), trimestre (3) o cuatrimestre (4)
     * a partir de mesInicio. Body: {anio, mesInicio, tamano, usuario, observaciones?}.
     */
    @PostMapping("/cerrar-grupo")
    public ResponseEntity<?> cerrarGrupo(@RequestBody Map<String, Object> body) {
        try {
            int anio = ((Number) body.get("anio")).intValue();
            int mesInicio = ((Number) body.get("mesInicio")).intValue();
            int tamano = ((Number) body.get("tamano")).intValue();
            String usuario = (String) body.getOrDefault("usuario", "sistema");
            String obs = (String) body.getOrDefault("observaciones", null);
            return ResponseEntity.ok(service.cerrarGrupo(anio, mesInicio, tamano, usuario, obs));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Reabre un periodo cerrado. Requiere motivo + contraseña del usuario admin (clave especial). */
    @PostMapping("/reabrir")
    public ResponseEntity<?> reabrir(@RequestBody Map<String, Object> body) {
        try {
            int anio = ((Number) body.get("anio")).intValue();
            int mes  = ((Number) body.get("mes")).intValue();
            String usuario = (String) body.getOrDefault("usuario", "sistema");
            String motivo = (String) body.getOrDefault("motivo", "Sin motivo");
            String password = (String) body.getOrDefault("password", null);
            return ResponseEntity.ok(service.reabrirValidado(anio, mes, usuario, motivo, password));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
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
