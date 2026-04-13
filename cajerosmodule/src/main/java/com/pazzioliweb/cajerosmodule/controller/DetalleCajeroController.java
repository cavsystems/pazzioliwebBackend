package com.pazzioliweb.cajerosmodule.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.cajerosmodule.dtos.CuadreCajaDTO;
import com.pazzioliweb.cajerosmodule.dtos.DetalleCajeroDTO;
import com.pazzioliweb.cajerosmodule.dtos.InformeDiarioVentasDTO;
import com.pazzioliweb.cajerosmodule.dtos.MovimientoCajeroDTO;
import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.cajerosmodule.entity.DetalleCajero;
import com.pazzioliweb.cajerosmodule.repositori.CajeroRepository;
import com.pazzioliweb.cajerosmodule.service.DetalleCajeroService;
import com.pazzioliweb.cajerosmodule.service.InformeDiarioService;

/**
 * Controlador para la gestión del detalle de cajeros (sesiones de caja).
 *
 * Endpoints principales:
 *   POST  /{cajeroId}/cerrar      → Cierre con cuadre declarado (3 componentes)
 *   GET   /{id}/cuadre            → Ver cuadre completo de una sesión
 *   GET   /{id}/movimientos       → Ver movimientos individuales (los 6 documentos POS)
 *   GET   /movimientos/cajero/{id}→ Historial de movimientos de un cajero
 */
@RestController
@RequestMapping("/api/detalle-cajeros")
public class DetalleCajeroController {

    private final DetalleCajeroService detalleCajeroService;
    private final CajeroRepository     cajeroRepository;
    private final InformeDiarioService informeDiarioService;

    @Autowired
    public DetalleCajeroController(DetalleCajeroService detalleCajeroService,
                                   CajeroRepository cajeroRepository,
                                   InformeDiarioService informeDiarioService) {
        this.detalleCajeroService = detalleCajeroService;
        this.cajeroRepository     = cajeroRepository;
        this.informeDiarioService = informeDiarioService;
    }

    // ============================================================
    // APERTURA MANUAL DE SESIÓN
    // ============================================================

    /**
     * Abre una sesión de caja manualmente para un cajero específico.
     * Permite indicar la base de caja y el comprobante a usar.
     * Si ya hay una sesión abierta, la retorna sin crear una nueva.
     *
     * Body: { "baseCaja": 200000, "comprobanteId": 1 }
     */
    @PostMapping("/cajero/{cajeroId}/abrir")
    public ResponseEntity<?> abrirSesion(
            @PathVariable Integer cajeroId,
            @RequestBody Map<String, Object> body) {
        try {
            Cajero cajero = cajeroRepository.findById(cajeroId)
                    .orElseThrow(() -> new RuntimeException("Cajero no encontrado: " + cajeroId));
            BigDecimal baseCaja = new java.math.BigDecimal(
                    body.getOrDefault("baseCaja", "0").toString());
            Integer comprobanteId = body.containsKey("comprobanteId")
                    ? Integer.valueOf(body.get("comprobanteId").toString()) : null;
            DetalleCajero detalle = detalleCajeroService.abrirSesionCajero(cajero, baseCaja, comprobanteId);
            return ResponseEntity.ok(Map.of(
                    "detalleCajeroId", detalle.getDetalleCajeroId(),
                    "cajeroId", cajeroId,
                    "estado", detalle.getEstado(),
                    "baseCaja", detalle.getBaseCaja(),
                    "fechaApertura", detalle.getFechaApertura()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========================
    // CRUD BÁSICO
    // ========================

    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "detalleCajeroId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<DetalleCajeroDTO> detallePage = detalleCajeroService.listar(page, size, sortField, sortDirection);
        Map<String, Object> response = new HashMap<>();
        response.put("content", detallePage.getContent());
        response.put("currentPage", detallePage.getNumber());
        response.put("totalItems", detallePage.getTotalElements());
        response.put("totalPages", detallePage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cajero/{cajeroId}")
    public ResponseEntity<Map<String, Object>> listarPorCajero(
            @PathVariable Integer cajeroId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "detalleCajeroId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<DetalleCajeroDTO> detallePage = detalleCajeroService.listarPorCajero(cajeroId, page, size, sortField, sortDirection);
        Map<String, Object> response = new HashMap<>();
        response.put("content", detallePage.getContent());
        response.put("currentPage", detallePage.getNumber());
        response.put("totalItems", detallePage.getTotalElements());
        response.put("totalPages", detallePage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleCajero> obtener(@PathVariable Long id) {
        return detalleCajeroService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DetalleCajero> crear(@RequestBody DetalleCajero detalle) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleCajeroService.guardar(detalle));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleCajero> actualizar(@PathVariable Long id, @RequestBody DetalleCajero detalle) {
        return detalleCajeroService.buscarPorId(id)
                .map(actual -> {
                    detalle.setDetalleCajeroId(id);
                    return ResponseEntity.ok(detalleCajeroService.guardar(detalle));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (detalleCajeroService.buscarPorId(id).isPresent()) {
            detalleCajeroService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ============================================================
    // CIERRE DE CAJA — con los 3 componentes declarados por el cajero
    // ============================================================

    /**
     * Cierre formal de caja: el cajero declara cuánto tiene en efectivo
     * y cuánto en medios electrónicos. El sistema calcula las diferencias.
     *
     * Body: { "efectivoDeclarado": 150000, "mediosElectronicosDeclarado": 80000 }
     */
    @PostMapping("/cajero/{cajeroId}/cerrar")
    public ResponseEntity<?> cerrarCaja(
            @PathVariable Integer cajeroId,
            @RequestBody Map<String, BigDecimal> body) {
        try {
            BigDecimal efectivo = body.getOrDefault("efectivoDeclarado", BigDecimal.ZERO);
            BigDecimal electronico = body.getOrDefault("mediosElectronicosDeclarado", BigDecimal.ZERO);
            DetalleCajero cerrado = detalleCajeroService.cerrarSesionCajero(cajeroId, efectivo, electronico);
            // Retornar el cuadre completo al cierre
            CuadreCajaDTO cuadre = detalleCajeroService.construirCuadre(cerrado.getDetalleCajeroId());
            return ResponseEntity.ok(cuadre);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // CUADRE DE CAJA — vista completa con 3 componentes + desglose
    // ============================================================

    /**
     * Devuelve el cuadre de caja completo de una sesión:
     * - Resumen de los 3 componentes (efectivo, electrónico, diferencia)
     * - Desglose por cada uno de los 6 tipos de documento POS
     *   (VENTA, COTIZACION, PEDIDO, CUENTA_POR_COBRAR, EGRESO, DEVOLUCION)
     */
    @GetMapping("/{detalleCajeroId}/cuadre")
    public ResponseEntity<CuadreCajaDTO> obtenerCuadre(@PathVariable Long detalleCajeroId) {
        try {
            return ResponseEntity.ok(detalleCajeroService.construirCuadre(detalleCajeroId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ============================================================
    // MOVIMIENTOS INDIVIDUALES (1 registro por transacción)
    // ============================================================

    /**
     * Lista los movimientos individuales de una sesión ordenados cronológicamente.
     * Cada fila = 1 transacción (venta, cotización, pedido, egreso, devolución…)
     */
    @GetMapping("/{detalleCajeroId}/movimientos")
    public ResponseEntity<Map<String, Object>> listarMovimientosPorSesion(
            @PathVariable Long detalleCajeroId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Page<MovimientoCajeroDTO> movimientosPage =
                detalleCajeroService.listarMovimientosPorSesion(detalleCajeroId, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("content", movimientosPage.getContent());
        response.put("currentPage", movimientosPage.getNumber());
        response.put("totalItems", movimientosPage.getTotalElements());
        response.put("totalPages", movimientosPage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    /**
     * Historial de todos los movimientos de un cajero (todas sus sesiones).
     */
    @GetMapping("/movimientos/cajero/{cajeroId}")
    public ResponseEntity<Map<String, Object>> listarMovimientosPorCajero(
            @PathVariable Integer cajeroId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Page<MovimientoCajeroDTO> movimientosPage =
                detalleCajeroService.listarMovimientosPorCajero(cajeroId, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("content", movimientosPage.getContent());
        response.put("currentPage", movimientosPage.getNumber());
        response.put("totalItems", movimientosPage.getTotalElements());
        response.put("totalPages", movimientosPage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // EGRESO MANUAL — salida de efectivo de caja
    // ============================================================

    /**
     * Registra una salida manual de efectivo de la sesión activa.
     * Ejemplo de uso: pagar un servicio, gastos de papelería, fondos a otro cajero.
     *
     * Body: { "monto": 50000, "descripcion": "Pago factura servicios" }
     */
    @PostMapping("/{detalleCajeroId}/egreso")
    public ResponseEntity<?> registrarEgreso(
            @PathVariable Long detalleCajeroId,
            @RequestBody Map<String, Object> body) {
        try {
            BigDecimal monto = new java.math.BigDecimal(body.get("monto").toString());
            String descripcion = body.getOrDefault("descripcion", "Egreso de caja").toString();
            detalleCajeroService.registrarEgreso(detalleCajeroId, monto, descripcion);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Egreso registrado correctamente",
                    "monto", monto,
                    "descripcion", descripcion
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // INGRESO EFECTIVO MANUAL — entrada de efectivo a caja
    // ============================================================

    /**
     * Registra una entrada manual de efectivo en la sesión activa.
     * Ejemplo de uso: fondos de cambio, reposición de base, ingresos no documentados.
     *
     * Body: { "monto": 100000, "descripcion": "Reposición de base" }
     */
    @PostMapping("/{detalleCajeroId}/ingreso-efectivo")
    public ResponseEntity<?> registrarIngresoEfectivo(
            @PathVariable Long detalleCajeroId,
            @RequestBody Map<String, Object> body) {
        try {
            BigDecimal monto = new java.math.BigDecimal(body.get("monto").toString());
            String descripcion = body.getOrDefault("descripcion", "Ingreso de efectivo").toString();
            detalleCajeroService.registrarIngresoEfectivo(detalleCajeroId, monto, descripcion);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Ingreso de efectivo registrado correctamente",
                    "monto", monto,
                    "descripcion", descripcion
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  INFORME DIARIO DE VENTAS  (Reporte Z / Cuadre de Caja)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Genera el Informe Diario de Ventas completo de una sesión.
     *
     * Secciones del reporte:
     *   - Encabezado      → cajero, fecha, hora, transacción inicial/final, N° transacciones, Z
     *   - Movimiento de Cuentas → bruta, descuentos, retenciones, gravadas, exentas, IVA, total
     *   - Ventas por Línea
     *   - Formas de Pago
     *   - Recibos de Caja  (abonos recibidos)
     *   - Comprobantes de Egreso
     *   - Vales
     *   - Devoluciones     → devGravada, ivaDevGravada, devExentas, contado, CxC, DSC, total
     *   - Resumen Final    → Neto Caja, UPT, VPT, VPU
     *
     * GET /api/detalle-cajeros/{detalleCajeroId}/informe-diario
     */
    @GetMapping("/{detalleCajeroId}/informe-diario")
    public ResponseEntity<?> generarInformeDiario(@PathVariable Long detalleCajeroId) {
        try {
            InformeDiarioVentasDTO informe = informeDiarioService.generarInforme(detalleCajeroId);
            return ResponseEntity.ok(informe);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}



