package com.pazzioliweb.reportesmodule.controller;

import com.pazzioliweb.reportesmodule.dtos.*;
import com.pazzioliweb.reportesmodule.service.ReportesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Endpoints de reportes para alimentar gráficas del dashboard POS.
 *
 * Todos los endpoints reciben rango de fechas como query params:
 *   ?inicio=2026-04-01&fin=2026-04-30
 *
 * Si no se envían, se usa el mes actual por defecto.
 *
 * Base URL: /api/reportes
 */
@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    private final ReportesService reportesService;

    public ReportesController(ReportesService reportesService) {
        this.reportesService = reportesService;
    }

    // ════════════════════════════════════════════════
    // 1. DASHBOARD KPI (tarjetas resumen)
    // ════════════════════════════════════════════════

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResumenDTO> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getDashboard(rango[0], rango[1]));
    }

    // ════════════════════════════════════════════════
    // 2. VENTAS POR DÍA (gráfica de líneas)
    // ════════════════════════════════════════════════

    @GetMapping("/ventas-por-dia")
    public ResponseEntity<List<VentasPorPeriodoDTO>> ventasPorDia(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getVentasPorDia(rango[0], rango[1]));
    }

    // ════════════════════════════════════════════════
    // 3. VENTAS POR MES (gráfica de barras anual)
    // ════════════════════════════════════════════════

    @GetMapping("/ventas-por-mes")
    public ResponseEntity<List<VentasPorPeriodoDTO>> ventasPorMes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getVentasPorMes(rango[0], rango[1]));
    }

    // ════════════════════════════════════════════════
    // 4. VENTAS POR HORA (horarios pico — barras)
    // ════════════════════════════════════════════════

    @GetMapping("/ventas-por-hora")
    public ResponseEntity<List<VentasPorPeriodoDTO>> ventasPorHora(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getVentasPorHora(rango[0], rango[1]));
    }

    // ════════════════════════════════════════════════
    // 5. TOP PRODUCTOS MÁS VENDIDOS (barras / tabla)
    // ════════════════════════════════════════════════

    @GetMapping("/top-productos")
    public ResponseEntity<List<ProductoMasVendidoDTO>> topProductos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "10") int topN) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getTopProductos(rango[0], rango[1], topN));
    }

    // ════════════════════════════════════════════════
    // 6. VENTAS POR VENDEDOR (barras horizontales)
    // ════════════════════════════════════════════════

    @GetMapping("/ventas-por-vendedor")
    public ResponseEntity<List<VentasPorVendedorDTO>> ventasPorVendedor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getVentasPorVendedor(rango[0], rango[1]));
    }

    // ════════════════════════════════════════════════
    // 7. VENTAS POR CAJERO (barras)
    // ════════════════════════════════════════════════

    @GetMapping("/ventas-por-cajero")
    public ResponseEntity<List<VentasPorCajeroDTO>> ventasPorCajero(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getVentasPorCajero(rango[0], rango[1]));
    }

    // ════════════════════════════════════════════════
    // 8. VENTAS POR MÉTODO DE PAGO (torta / dona)
    // ════════════════════════════════════════════════

    @GetMapping("/ventas-por-metodo-pago")
    public ResponseEntity<List<VentasPorMetodoPagoDTO>> ventasPorMetodoPago(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getVentasPorMetodoPago(rango[0], rango[1]));
    }

    // ════════════════════════════════════════════════
    // 9. TOP CLIENTES (barras / tabla)
    // ════════════════════════════════════════════════

    @GetMapping("/top-clientes")
    public ResponseEntity<List<VentasPorClienteDTO>> topClientes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "10") int topN) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getTopClientes(rango[0], rango[1], topN));
    }

    // ════════════════════════════════════════════════
    // 10. VENTAS POR LÍNEA / CATEGORÍA (torta)
    // ════════════════════════════════════════════════

    @GetMapping("/ventas-por-linea")
    public ResponseEntity<List<VentasPorCategoriaDTO>> ventasPorLinea(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getVentasPorLinea(rango[0], rango[1]));
    }

    // ════════════════════════════════════════════════
    // 11. CARTERA — CUENTAS POR COBRAR POR ESTADO
    // ════════════════════════════════════════════════

    @GetMapping("/cartera")
    public ResponseEntity<List<ReporteCarteraDTO>> cartera() {
        return ResponseEntity.ok(reportesService.getCarteraPorEstado());
    }

    // ════════════════════════════════════════════════
    // 12. COMPRAS POR PROVEEDOR (barras / tabla)
    // ════════════════════════════════════════════════

    @GetMapping("/compras-por-proveedor")
    public ResponseEntity<List<ComprasPorProveedorDTO>> comprasPorProveedor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "10") int topN) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getComprasPorProveedor(rango[0], rango[1], topN));
    }

    // ════════════════════════════════════════════════
    // 13. COMPRAS VS VENTAS (barras agrupadas)
    // ════════════════════════════════════════════════

    @GetMapping("/compras-vs-ventas")
    public ResponseEntity<List<ComprasVsVentasDTO>> comprasVsVentas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getComprasVsVentas(rango[0], rango[1]));
    }

    // ════════════════════════════════════════════════
    // 14. PRODUCTOS CON STOCK BAJO (tabla alertas)
    // ════════════════════════════════════════════════

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoStockBajoDTO>> stockBajo(
            @RequestParam(defaultValue = "20") int limite) {
        return ResponseEntity.ok(reportesService.getProductosStockBajo(limite));
    }

    // ════════════════════════════════════════════════
    // 15. MOVIMIENTOS DE CAJA POR TIPO (barras apiladas)
    // ════════════════════════════════════════════════

    @GetMapping("/movimientos-caja")
    public ResponseEntity<List<MovimientoCajaTipoDTO>> movimientosCaja(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getMovimientosCajaPorTipo(rango[0], rango[1]));
    }

    // ════════════════════════════════════════════════
    // 16. RENTABILIDAD POR PRODUCTO (tabla con margen)
    // ════════════════════════════════════════════════

    @GetMapping("/rentabilidad-productos")
    public ResponseEntity<List<RentabilidadProductoDTO>> rentabilidadProductos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "20") int topN) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getRentabilidadProductos(rango[0], rango[1], topN));
    }

    // ════════════════════════════════════════════════
    // 17. VENTAS POR DÍA DE LA SEMANA (radar / barras)
    // ════════════════════════════════════════════════

    @GetMapping("/ventas-por-dia-semana")
    public ResponseEntity<List<VentasPorPeriodoDTO>> ventasPorDiaSemana(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] rango = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getVentasPorDiaSemana(rango[0], rango[1]));
    }

    // ════════════════════════════════════════════════
    // HELPER — fechas por defecto (mes actual)
    // ════════════════════════════════════════════════

    private LocalDate[] defaults(LocalDate inicio, LocalDate fin) {
        if (inicio == null) inicio = LocalDate.now().withDayOfMonth(1);
        if (fin == null) fin = LocalDate.now();
        return new LocalDate[]{inicio, fin};
    }

    // ════════════════════════════════════════════════
    // 18. COMPARATIVO PERIODO ACTUAL vs ANTERIOR
    //     Si no se envían fechas anteriores, se calculan automáticamente
    //     como el periodo inmediatamente previo del mismo tamaño.
    // ════════════════════════════════════════════════

    @GetMapping("/comparativo-periodos")
    public ResponseEntity<ComparativoPeriodosDTO> comparativoPeriodos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate prevInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate prevFin) {
        LocalDate[] r = defaults(inicio, fin);
        if (prevInicio == null || prevFin == null) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(r[0], r[1]);
            prevFin = r[0].minusDays(1);
            prevInicio = prevFin.minusDays(dias);
        }
        return ResponseEntity.ok(reportesService.getComparativoPeriodos(r[0], r[1], prevInicio, prevFin));
    }

    // ════════════════════════════════════════════════
    // 19. ANULACIONES (vista unificada ventas + recibos + egresos)
    // ════════════════════════════════════════════════

    @GetMapping("/anulaciones")
    public ResponseEntity<List<AnulacionDTO>> anulaciones(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] r = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getAnulaciones(r[0], r[1]));
    }

    // ════════════════════════════════════════════════
    // 20. CONCEPTOS ABIERTOS — uso en el periodo
    // ════════════════════════════════════════════════

    @GetMapping("/conceptos-abiertos")
    public ResponseEntity<List<ConceptoAbiertoUsoDTO>> conceptosAbiertosUso(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(required = false) String tipo) {
        LocalDate[] r = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getConceptosAbiertosUso(r[0], r[1], tipo));
    }

    // ════════════════════════════════════════════════
    // 21. DEVOLUCIONES DETALLADAS
    // ════════════════════════════════════════════════

    @GetMapping("/devoluciones-detalladas")
    public ResponseEntity<List<DevolucionDetalladaDTO>> devolucionesDetalladas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] r = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getDevolucionesDetalladas(r[0], r[1]));
    }

    // ════════════════════════════════════════════════
    // 22. TICKET PROMEDIO POR CAJERO O VENDEDOR
    // ════════════════════════════════════════════════

    @GetMapping("/ticket-promedio")
    public ResponseEntity<List<TicketPromedioDTO>> ticketPromedio(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "cajero") String agrupacion) {
        LocalDate[] r = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getTicketPromedio(r[0], r[1], agrupacion));
    }

    // ════════════════════════════════════════════════
    // 23. CARTERA POR ANTIGÜEDAD (aging buckets)
    // ════════════════════════════════════════════════

    @GetMapping("/cartera-aging")
    public ResponseEntity<List<CarteraAgingDTO>> carteraAging() {
        return ResponseEntity.ok(reportesService.getCarteraAging());
    }

    // ════════════════════════════════════════════════
    // 24. PRODUCTOS SIN MOVIMIENTO (inventario inmovilizado)
    // ════════════════════════════════════════════════

    @GetMapping("/productos-sin-movimiento")
    public ResponseEntity<List<ProductoSinMovimientoDTO>> productosSinMovimiento(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "30") int topN) {
        LocalDate[] r = defaults(inicio, fin);
        return ResponseEntity.ok(reportesService.getProductosSinMovimiento(r[0], r[1], topN));
    }
}

