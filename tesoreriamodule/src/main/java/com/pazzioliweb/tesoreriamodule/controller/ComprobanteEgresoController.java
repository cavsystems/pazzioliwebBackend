package com.pazzioliweb.tesoreriamodule.controller;

import com.pazzioliweb.tesoreriamodule.dtos.ComprobanteEgresoResponseDTO;
import com.pazzioliweb.tesoreriamodule.dtos.CrearComprobanteEgresoDTO;
import com.pazzioliweb.tesoreriamodule.service.ComprobanteEgresoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comprobantes-egreso")
public class ComprobanteEgresoController {

    private final ComprobanteEgresoService service;

    public ComprobanteEgresoController(ComprobanteEgresoService service) {
        this.service = service;
    }

    /** Obtiene el siguiente consecutivo (último ID + 1) */
    @GetMapping("/ultimo-id")
    public ResponseEntity<Map<String, Integer>> ultimoId() {
        return ResponseEntity.ok(Map.of("siguienteConsecutivo", service.obtenerSiguienteConsecutivo()));
    }

    /** Crea un comprobante de egreso con sus detalles de CxP */
    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody CrearComprobanteEgresoDTO dto) {
        try {
            ComprobanteEgresoResponseDTO response = service.crear(dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Lista todos los comprobantes de egreso */
    @GetMapping
    public ResponseEntity<List<ComprobanteEgresoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    /** Lista todos los comprobantes de egreso (alias para el frontend) */
    @GetMapping("/listar")
    public ResponseEntity<List<ComprobanteEgresoResponseDTO>> listarAlias() {
        return ResponseEntity.ok(service.listarTodos());
    }

    /** Busca un comprobante por ID */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Anula un comprobante de egreso: revierte saldos de CxP y registra ANULACION en cajero. */
    @PutMapping("/{id}/anular")
    public ResponseEntity<?> anular(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            String motivo = body.get("motivo") == null ? null : body.get("motivo").toString();
            Integer usuarioId = body.get("usuarioId") == null ? null
                    : Integer.valueOf(body.get("usuarioId").toString());
            return ResponseEntity.ok(service.anular(id, motivo, usuarioId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

