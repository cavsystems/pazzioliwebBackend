package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.dtos.ComprobanteContableCreateDTO;
import com.pazzioliweb.comprobantesmodule.dtos.ComprobanteContableDTO;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService;
import com.pazzioliweb.comprobantesmodule.service.ComprobanteContableService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private AsignacionComprobanteService asignacionService;

    public ComprobanteContableController(ComprobanteContableService service) {
        this.service = service;
    }

    /**
     * Devuelve alertas de salud de la resolución DIAN para los comprobantes activos
     * de tipos DIAN (FC, VC, NC, ND, TPOS, DS). Permite al frontend mostrar avisos
     * de "vence en X días" o "consecutivos restantes" sin tener que emitir un doc.
     */
    @GetMapping("/alertas-resolucion")
    public ResponseEntity<List<AsignacionComprobanteService.AlertaResolucion>> alertasResolucion() {
        List<AsignacionComprobanteService.AlertaResolucion> alertas = service.listarActivos().stream()
                .map(c -> asignacionService.inspeccionarResolucion(c.getId()))
                .filter(java.util.Objects::nonNull)
                .filter(a -> Boolean.TRUE.equals(a.vencida)
                          || Boolean.TRUE.equals(a.rangoAgotado)
                          || (a.diasParaVencer != null && a.diasParaVencer <= 30)
                          || (a.consecutivosRestantes != null && a.consecutivosRestantes <= 500))
                .collect(Collectors.toList());
        return ResponseEntity.ok(alertas);
    }

    @GetMapping
    public ResponseEntity<List<ComprobanteContableDTO>> listar(
            @RequestParam(required = false) Boolean soloActivos,
            @RequestParam(required = false) Integer cajeroId,
            @RequestParam(required = false) Integer bodegaId,
            @RequestParam(required = false) String tipo) {
        if (cajeroId != null) return ResponseEntity.ok(service.listarPorCajero(cajeroId));
        List<ComprobanteContableDTO> base = Boolean.TRUE.equals(soloActivos)
                ? service.listarActivos() : service.listar();
        // Filtros adicionales en memoria
        return ResponseEntity.ok(base.stream()
                .filter(c -> bodegaId == null || (c.getBodegaId() != null && c.getBodegaId().equals(bodegaId)))
                .filter(c -> tipo == null || tipo.equalsIgnoreCase(c.getTipoMovimiento()))
                .collect(Collectors.toList()));
    }

    /**
     * Busca el comprobante activo para una (bodega, tipo). Si se pasa cajeroId,
     * además valida que ese cajero esté asignado al comprobante encontrado.
     * Estados posibles:
     *   - configurado: false  → no hay comprobante para la bodega+tipo
     *   - configurado: true, cajeroAsignado: false → el comprobante existe pero el cajero
     *     que está operando no está en su lista. La venta fallará al guardarse.
     *   - configurado: true, cajeroAsignado: true → todo en orden.
     */
    @GetMapping("/por-bodega-tipo")
    public ResponseEntity<Map<String, Object>> porBodegaYTipo(
            @RequestParam Integer bodegaId,
            @RequestParam String tipo,
            @RequestParam(required = false) Integer cajeroId) {
        return service.listarActivos().stream()
                .filter(c -> bodegaId.equals(c.getBodegaId())
                          && tipo.equalsIgnoreCase(c.getTipoMovimiento()))
                .findFirst()
                .<ResponseEntity<Map<String, Object>>>map(c -> {
                    Map<String, Object> body = new java.util.HashMap<>();
                    body.put("configurado", true);
                    body.put("id", c.getId());
                    body.put("prefijo", c.getPrefijo());
                    body.put("siguienteConsecutivo", c.getSiguienteConsecutivo());
                    body.put("resolucionDian", c.getResolucionDian());

                    body.put("fechaInicioResolucion", c.getFechaInicioResolucion());
                    body.put("fechaFinResolucion", c.getFechaFinResolucion());
                    body.put("consecutivoDesde", c.getConsecutivoDesde());
                    body.put("consecutivoHasta", c.getConsecutivoHasta());
                    body.put("numeroPreview", c.getPrefijo() + "-" + c.getSiguienteConsecutivo());

                    // Validación de cajero (si el frontend mandó el id)
                    if (cajeroId != null) {
                        boolean asignado = c.getCajeroIds() != null
                                && c.getCajeroIds().contains(cajeroId);
                        body.put("cajeroAsignado", asignado);
                        if (!asignado) {
                            body.put("warningCajero", "Tu usuario/cajero no está asignado a este comprobante. " +
                                    "La venta no podrá generar consecutivo. " +
                                    "Pídele al administrador que te agregue al comprobante '" + c.getPrefijo() + "'.");
                        }
                    }
                    return ResponseEntity.ok(body);
                })
                .orElseGet(() -> {
                    Map<String, Object> body = new java.util.HashMap<>();
                    body.put("configurado", false);
                    body.put("mensaje", "La bodega " + bodegaId + " no tiene comprobante " + tipo + " configurado.");
                    return ResponseEntity.ok(body);
                });
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

    /**
     * Devuelve el siguiente número que se asignará al próximo movimiento de
     * (cajero, tipo) sin reservarlo. Útil para previsualizar en el header de
     * formularios. Si no hay comprobante configurado, devuelve un payload
     * indicando ese estado para que el frontend muestre el mensaje correcto.
     */
    @GetMapping("/preview-siguiente")
    public ResponseEntity<Map<String, Object>> previewSiguiente(
            @RequestParam Integer cajeroId,
            @RequestParam String tipo) {
        try {
            TipoMovimientoComprobante t = TipoMovimientoComprobante.valueOf(tipo.toUpperCase());
            final TipoMovimientoComprobante tipoFinal = t;
            return service.listarPorCajero(cajeroId).stream()
                .filter(c -> Boolean.TRUE.equals(c.getActivo()) && !Boolean.TRUE.equals(c.getEsLegacy())
                          && tipoFinal.name().equalsIgnoreCase(c.getTipoMovimiento()))
                .findFirst()
                .<ResponseEntity<Map<String, Object>>>map(c -> {
                    Map<String, Object> body = new java.util.HashMap<>();
                    body.put("configurado", true);
                    body.put("prefijo", c.getPrefijo());
                    body.put("siguienteConsecutivo", c.getSiguienteConsecutivo());
                    body.put("numeroPreview", c.getPrefijo() + "-" + c.getSiguienteConsecutivo());
                    return ResponseEntity.ok(body);
                })
                .orElseGet(() -> {
                    Map<String, Object> body = new java.util.HashMap<>();
                    body.put("configurado", false);
                    body.put("mensaje", "El cajero " + cajeroId + " no tiene un comprobante " + tipo + " configurado.");
                    return ResponseEntity.ok(body);
                });
        } catch (IllegalArgumentException ex) {
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("configurado", false);
            body.put("mensaje", "Tipo de movimiento inválido: " + tipo);
            return ResponseEntity.badRequest().body(body);
        }
    }
}
