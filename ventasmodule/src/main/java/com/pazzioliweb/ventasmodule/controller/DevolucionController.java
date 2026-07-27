package com.pazzioliweb.ventasmodule.controller;

import com.pazzioliweb.commonbacken.dtos.DocumentoAdjuntoDTO;
import com.pazzioliweb.commonbacken.services.WhatsappService;
import com.pazzioliweb.ventasmodule.dtos.DevolucionDTO;
import com.pazzioliweb.ventasmodule.dtos.DevolucionRequestDTO;
import com.pazzioliweb.ventasmodule.service.DevolucionService;
import com.pazzioliweb.ventasmodule.service.EmailDevolucionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Endpoints REST para gestionar devoluciones de ventas.
 *
 * GET  /api/devoluciones                        → Todas las devoluciones (con filtros opcionales)
 * GET  /api/devoluciones/cajero/{cajeroId}      → Por cajero
 * POST /api/devoluciones                        → Registrar devolución
 * GET  /api/devoluciones/venta/{ventaId}        → Por venta
 * GET  /api/devoluciones/{id}                   → Detalle de una devolución
 */
@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionController {

    private final DevolucionService devolucionService;

    @Autowired
    private EmailDevolucionService emailDevolucionService;

    @Autowired
    private WhatsappService whatsappService;

    @Autowired
    public DevolucionController(DevolucionService devolucionService) {
        this.devolucionService = devolucionService;
    }

    /**
     * Retorna TODAS las devoluciones con filtros opcionales:
     * ?cajeroId=1&fechaInicio=2025-01-01&fechaFin=2025-12-31
     */
    @GetMapping
    public ResponseEntity<List<DevolucionDTO>> getAllDevoluciones(
            @RequestParam(required = false) Long terceroId,
            @RequestParam(required = false) Integer cajeroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        // Si viene algún filtro usa el método combinado, si no trae todo
        if (cajeroId != null || fechaInicio != null || fechaFin != null) {
            return ResponseEntity.ok(devolucionService.getDevolucionesByFiltros(cajeroId, fechaInicio, fechaFin));
        }
        return ResponseEntity.ok(devolucionService.getAllDevoluciones());
    }

    /**
     * Retorna todas las devoluciones de un cajero específico.
     * Admite filtro opcional de fechas: ?fechaInicio=2025-01-01&fechaFin=2025-12-31
     */
    @GetMapping("/cajero/{cajeroId}")
    public ResponseEntity<List<DevolucionDTO>> getDevolucionesByCajero(
            @PathVariable Integer cajeroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        return ResponseEntity.ok(devolucionService.getDevolucionesByFiltros(cajeroId, fechaInicio, fechaFin));
    }

    /**
     * Registra una devolución de venta.
     */
    @PostMapping
    public ResponseEntity<DevolucionDTO> registrarDevolucion(@RequestBody DevolucionRequestDTO request) {
        return ResponseEntity.ok(devolucionService.registrarDevolucion(request));
    }

    /**
     * Retorna todas las devoluciones de una venta específica.
     */
    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<DevolucionDTO>> getDevolucionesByVenta(@PathVariable Long ventaId) {
        return ResponseEntity.ok(devolucionService.getDevolucionesByVenta(ventaId));
    }

    /**
     * Retorna el detalle completo de una devolución por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DevolucionDTO> getDevolucionById(@PathVariable Long id) {
        return ResponseEntity.ok(devolucionService.getDevolucionById(id));
    }


    @PostMapping("/{ventaId}/{devolucionId}/convertir-pedido")
    public ResponseEntity<Void> cambiarEstadoDevolucion(@PathVariable Long ventaId, @PathVariable Long devolucionId) {
        devolucionService.convertirAPedido(ventaId, devolucionId);
        return ResponseEntity.ok().build();
    }

    /**
     * Anula una devolución REGISTRADA y revierte todos sus efectos (asiento, inventario, caja).
     * Body: { "motivo": "...", "usuario": "..." }
     */
    @PostMapping("/{id}/anular")
    public ResponseEntity<DevolucionDTO> anularDevolucion(@PathVariable Long id,
                                                          @RequestBody(required = false) java.util.Map<String, String> body,
                                                          jakarta.servlet.http.HttpServletRequest request) {
        String motivo = body != null ? body.get("motivo") : null;
        String usuario = body != null ? body.getOrDefault("usuario",
                request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "sistema")
                : "sistema";
        return ResponseEntity.ok(devolucionService.anularDevolucion(id, motivo, usuario));
    }

    /** Envía la devolución por correo con el PDF adjunto. */
    @PostMapping("/{id}/enviar-email")
    public ResponseEntity<java.util.Map<String, Object>> enviarEmail(@PathVariable Long id,
                                                                     @RequestParam String correo) {
        DevolucionDTO dev = devolucionService.getDevolucionById(id);
        boolean enviado = emailDevolucionService.enviarDevolucion(dev, correo);
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("enviado", enviado);
        resp.put("mensaje", enviado
                ? "Devolución enviada a " + correo
                : "Servicio de email no configurado. Configure spring.mail.* en application.properties.");
        return ResponseEntity.ok(resp);
    }

    /**
     * Envía la devolución por WhatsApp (Cloud API de Meta) con el PDF adjunto.
     * Si WhatsApp automático no está configurado responde {@code configurado:false} y el frontend
     * degrada al link wa.me.
     */
    @PostMapping("/{id}/enviar-whatsapp")
    public ResponseEntity<java.util.Map<String, Object>> enviarWhatsapp(@PathVariable Long id,
                                                                        @RequestParam String telefono) {
        DevolucionDTO dev = devolucionService.getDevolucionById(id);
        DocumentoAdjuntoDTO doc = emailDevolucionService.documentoDevolucion(dev);
        WhatsappService.Resultado r = whatsappService.enviarDocumento(
                telefono, doc.nombreArchivo(), doc.caption(), doc.pdf());
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("enviado", r.enviado());
        resp.put("configurado", r.configurado());
        resp.put("mensaje", r.mensaje());
        resp.put("texto", doc.caption());
        return ResponseEntity.ok(resp);
    }
}
