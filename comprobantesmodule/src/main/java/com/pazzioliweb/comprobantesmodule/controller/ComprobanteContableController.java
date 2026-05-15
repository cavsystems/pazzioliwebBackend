package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.dtos.ComprobanteContableCreateDTO;
import com.pazzioliweb.comprobantesmodule.dtos.ComprobanteContableDTO;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.service.ComprobanteContableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comprobantes-contables")
public class ComprobanteContableController {

    private final ComprobanteContableService service;

    public ComprobanteContableController(ComprobanteContableService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ComprobanteContableDTO>> listar(
            @RequestParam(required = false) Boolean soloActivos,
            @RequestParam(required = false) Integer cajeroId) {
        if (cajeroId != null) return ResponseEntity.ok(service.listarPorCajero(cajeroId));
        if (Boolean.TRUE.equals(soloActivos)) return ResponseEntity.ok(service.listarActivos());
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComprobanteContableDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<ComprobanteContableDTO> crear(@RequestBody ComprobanteContableCreateDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComprobanteContableDTO> actualizar(@PathVariable Long id,
                                                             @RequestBody ComprobanteContableCreateDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Lista de tipos de movimiento para los selectores del frontend. */
    @GetMapping("/tipos")
    public ResponseEntity<List<Map<String, String>>> tipos() {
        return ResponseEntity.ok(
            Arrays.stream(TipoMovimientoComprobante.values())
                .map(t -> Map.of("codigo", t.name(), "descripcion", t.getDescripcion()))
                .collect(Collectors.toList())
        );
    }
}
