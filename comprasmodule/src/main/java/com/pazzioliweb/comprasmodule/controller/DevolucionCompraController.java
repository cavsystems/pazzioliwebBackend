package com.pazzioliweb.comprasmodule.controller;

import com.pazzioliweb.comprasmodule.dtos.DevolucionCompraDTO;
import com.pazzioliweb.comprasmodule.dtos.DevolucionCompraRequestDTO;
import com.pazzioliweb.comprasmodule.service.DevolucionCompraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compras/devoluciones-compra")
public class DevolucionCompraController {

    private final DevolucionCompraService service;

    public DevolucionCompraController(DevolucionCompraService service) {
        this.service = service;
    }

    /** Registra una devolución de compra (nota débito). */
    @PostMapping
    public ResponseEntity<DevolucionCompraDTO> registrar(@RequestBody DevolucionCompraRequestDTO request) {
        return ResponseEntity.ok(service.registrar(request));
    }

    /** Devoluciones de compra de una orden. */
    @GetMapping("/orden/{ordenCompraId}")
    public ResponseEntity<List<DevolucionCompraDTO>> listarPorOrden(@PathVariable Long ordenCompraId) {
        return ResponseEntity.ok(service.listarPorOrden(ordenCompraId));
    }

    /** Detalle de una devolución de compra. */
    @GetMapping("/{id}")
    public ResponseEntity<DevolucionCompraDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    /** Anula una devolución de compra. Body: { "motivo": "...", "usuario": "..." } */
    @PostMapping("/{id}/anular")
    public ResponseEntity<DevolucionCompraDTO> anular(@PathVariable Long id,
                                                      @RequestBody(required = false) Map<String, String> body,
                                                      jakarta.servlet.http.HttpServletRequest request) {
        String motivo = body != null ? body.get("motivo") : null;
        String usuario = body != null ? body.getOrDefault("usuario",
                request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "sistema")
                : "sistema";
        return ResponseEntity.ok(service.anular(id, motivo, usuario));
    }
}
