package com.pazzioliweb.tesoreriamodule.controller;

import com.pazzioliweb.tesoreriamodule.dtos.CrearReciboCajaDTO;
import com.pazzioliweb.tesoreriamodule.dtos.ReciboCajaResponseDTO;
import com.pazzioliweb.tesoreriamodule.service.ReciboCajaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recibos-caja")
public class ReciboCajaController {

    private final ReciboCajaService service;

    @org.springframework.beans.factory.annotation.Autowired
    private com.pazzioliweb.tesoreriamodule.service.EmailTesoreriaService emailService;

    public ReciboCajaController(ReciboCajaService service) {
        this.service = service;
    }

    /** Envía el recibo de caja por correo (con PDF adjunto). */
    @PostMapping("/{id}/enviar-email")
    public ResponseEntity<Map<String, Object>> enviarEmail(@PathVariable Long id, @RequestParam String correo) {
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        try {
            boolean enviado = emailService.enviarRecibo(service.buscarPorId(id), correo);
            resp.put("enviado", enviado);
            resp.put("mensaje", enviado ? "Recibo enviado a " + correo
                    : "Servicio de email no configurado. Configure spring.mail.* en application.properties.");
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            resp.put("enviado", false);
            resp.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /** Obtiene el siguiente consecutivo */
    @GetMapping("/ultimo-id")
    public ResponseEntity<Map<String, Integer>> ultimoId() {
        return ResponseEntity.ok(Map.of("siguienteConsecutivo", service.obtenerSiguienteConsecutivo()));
    }

    /** Crea un recibo de caja con sus detalles de CxC */
    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody CrearReciboCajaDTO dto) {
        try {
            ReciboCajaResponseDTO response = service.crear(dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Lista todos los recibos de caja */
    @GetMapping
    public ResponseEntity<List<ReciboCajaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    /** Lista recibos de caja, con filtro opcional de fecha (desde/hasta por fechaRecibo). */
    @GetMapping("/listar")
    public ResponseEntity<List<ReciboCajaResponseDTO>> listarAlias(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta) {
        if (desde != null && !desde.isBlank() && hasta != null && !hasta.isBlank()) {
            return ResponseEntity.ok(service.listarPorFechas(LocalDate.parse(desde), LocalDate.parse(hasta)));
        }
        return ResponseEntity.ok(service.listarTodos());
    }

    /** Busca un recibo por ID */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Anula un recibo: revierte saldos de CxC y registra movimiento ANULACION en cajero. */
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

