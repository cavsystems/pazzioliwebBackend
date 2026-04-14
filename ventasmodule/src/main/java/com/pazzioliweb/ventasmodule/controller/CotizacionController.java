package com.pazzioliweb.ventasmodule.controller;

import com.pazzioliweb.ventasmodule.dtos.CotizacionDTO;
import com.pazzioliweb.ventasmodule.dtos.PedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import com.pazzioliweb.ventasmodule.service.CotizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    @Autowired
    public CotizacionController(CotizacionService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Void> crearCotizacion(@RequestBody CotizacionDTO cotizacionDTO) {
        cotizacionService.crearCotizacion(cotizacionDTO);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/ultima-cotizacion-id")
    public ResponseEntity<Long> getUltimaVentaId() {
        Long id = cotizacionService.getUltimacotizacion();

        return ResponseEntity.ok(id+1);
    }
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CotizacionDTO>> getCotizacionesByCliente(@PathVariable Long clienteId) {
        List<CotizacionDTO> cotizaciones = cotizacionService.getCotizacionesByCliente(clienteId);
        return ResponseEntity.ok(cotizaciones);
    }

    @GetMapping("/detalle")
    public ResponseEntity<CotizacionDTO> getCotizacionDetalle(@RequestParam String numeroCotizacion) {
        CotizacionDTO cotizacion = cotizacionService.getCotizacionByNumero(numeroCotizacion);
        return ResponseEntity.ok(cotizacion);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<CotizacionDTO>> getCotizacionesActivas() {
        List<CotizacionDTO> cotizaciones = cotizacionService.getCotizacionesActivas();
        return ResponseEntity.ok(cotizaciones);
    }

    @PostMapping("/{cotizacionId}/enviar")
    public ResponseEntity<Void> enviarCotizacion(@PathVariable Long cotizacionId) {
        cotizacionService.enviarCotizacion(cotizacionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{cotizacionId}/aceptar")
    public ResponseEntity<Void> aceptarCotizacion(@PathVariable Long cotizacionId) {
        cotizacionService.aceptarCotizacion(cotizacionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{cotizacionId}/rechazar")
    public ResponseEntity<Void> rechazarCotizacion(@PathVariable Long cotizacionId) {
        cotizacionService.rechazarCotizacion(cotizacionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{cotizacionId}/anular")
    public ResponseEntity<Void> anularCotizacion(@PathVariable Long cotizacionId) {
        cotizacionService.anularCotizacion(cotizacionId);
        return ResponseEntity.ok().build();
    }

    /**
     * Cambia el estado de una cotización con validación de transiciones.
     * Estados válidos: BORRADOR, ENVIADA, ACEPTADA, RECHAZADA, VENCIDA
     *
     * Transiciones permitidas:
     *   BORRADOR  → ENVIADA, VENCIDA
     *   ENVIADA   → ACEPTADA, RECHAZADA, BORRADOR, VENCIDA
     *   ACEPTADA  → VENCIDA
     *
     * PATCH /api/cotizaciones/{id}/estado
     * Body: { "estado": "ENVIADA" }
     */
    @PatchMapping("/{cotizacionId}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long cotizacionId,
            @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        cotizacionService.cambiarEstado(cotizacionId, nuevoEstado);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{cotizacionId}/convertir-pedido")
    public ResponseEntity<PedidoDTO> convertirAPedido(@PathVariable Long cotizacionId) {
        PedidoDTO pedido = cotizacionService.convertirAPedido(cotizacionId);
        return ResponseEntity.ok(pedido);
    }

    @PostMapping("/{cotizacionId}/convertir-venta")
    public ResponseEntity<VentaDTO> convertirAVenta(@PathVariable Long cotizacionId, @RequestBody List<VentaMetodoPagoDTO> metodosPago) {
        VentaDTO venta = cotizacionService.convertirAVenta(cotizacionId, metodosPago);
        return ResponseEntity.ok(venta);
    }






    /**
     * Consulta cotizaciones  con filtros opcionales combinables.
     * Todos los parámetros son opcionales y se pueden usar juntos o por separado.
     *
     * GET /api/ventas/filtrar?terceroId=1&vendedorId=2&cajeroId=3&fechaInicio=2025-01-01&fechaFin=2025-12-31
     */
    @GetMapping("/filtrar")
    public ResponseEntity<List<CotizacionDTO>> filtrarVentas(
            @RequestParam(required = false) Long terceroId,
            @RequestParam(required = false) Integer vendedorId,
            @RequestParam(required = false) Integer cajeroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<CotizacionDTO> ventas = cotizacionService.getCotizacionesByFiltros(terceroId, vendedorId, cajeroId, fechaInicio, fechaFin);
        return ResponseEntity.ok(ventas);
    }
}



