package com.pazzioliweb.ventasmodule.controller;

import com.pazzioliweb.ventasmodule.dtos.DevolucionDTO;
import com.pazzioliweb.ventasmodule.dtos.DevolucionRequestDTO;
import com.pazzioliweb.ventasmodule.service.DevolucionService;
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
}
