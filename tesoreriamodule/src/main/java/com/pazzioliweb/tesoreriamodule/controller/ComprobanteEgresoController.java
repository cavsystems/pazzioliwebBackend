package com.pazzioliweb.tesoreriamodule.controller;

import com.pazzioliweb.tesoreriamodule.dtos.ComprobanteEgresoResponseDTO;
import com.pazzioliweb.tesoreriamodule.dtos.CrearComprobanteEgresoDTO;
import com.pazzioliweb.tesoreriamodule.service.ComprobanteEgresoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comprobantes-egreso")
public class ComprobanteEgresoController {

    private final ComprobanteEgresoService service;

    @org.springframework.beans.factory.annotation.Autowired
    private com.pazzioliweb.tesoreriamodule.service.EmailTesoreriaService emailService;

    @org.springframework.beans.factory.annotation.Autowired
    private com.pazzioliweb.commonbacken.services.WhatsappService whatsappService;

    public ComprobanteEgresoController(ComprobanteEgresoService service) {
        this.service = service;
    }

    /** Envía el comprobante de egreso por correo (con PDF adjunto). */
    @PostMapping("/{id}/enviar-email")
    public ResponseEntity<Map<String, Object>> enviarEmail(@PathVariable Long id, @RequestParam String correo) {
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        try {
            boolean enviado = emailService.enviarEgreso(service.buscarPorId(id), correo);
            resp.put("enviado", enviado);
            resp.put("mensaje", enviado ? "Comprobante enviado a " + correo
                    : "Servicio de email no configurado. Configure spring.mail.* en application.properties.");
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            resp.put("enviado", false);
            resp.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * Envía el comprobante de egreso por WhatsApp (Cloud API de Meta) con el PDF adjunto.
     * Si WhatsApp automático no está configurado responde {@code configurado:false} y el frontend
     * degrada al link wa.me.
     */
    @PostMapping("/{id}/enviar-whatsapp")
    public ResponseEntity<Map<String, Object>> enviarWhatsapp(@PathVariable Long id, @RequestParam String telefono) {
        Map<String, Object> resp = new java.util.HashMap<>();
        try {
            com.pazzioliweb.commonbacken.dtos.DocumentoAdjuntoDTO doc =
                    emailService.documentoEgreso(service.buscarPorId(id));
            com.pazzioliweb.commonbacken.services.WhatsappService.Resultado r =
                    whatsappService.enviarDocumento(telefono, doc.nombreArchivo(), doc.caption(), doc.pdf());
            resp.put("enviado", r.enviado());
            resp.put("configurado", r.configurado());
            resp.put("mensaje", r.mensaje());
            resp.put("texto", doc.caption());
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            resp.put("enviado", false);
            resp.put("configurado", whatsappService.isConfigurado());
            resp.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
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

    /** Lista todos los comprobantes de egreso (alias para el frontend), con filtro opcional de fecha */
    @GetMapping("/listar")
    public ResponseEntity<List<ComprobanteEgresoResponseDTO>> listarAlias(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta) {
        if (desde != null && !desde.isBlank() && hasta != null && !hasta.isBlank()) {
            return ResponseEntity.ok(service.listarPorFechas(LocalDate.parse(desde), LocalDate.parse(hasta)));
        }
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

