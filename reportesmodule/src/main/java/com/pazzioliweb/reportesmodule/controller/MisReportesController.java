package com.pazzioliweb.reportesmodule.controller;

import com.pazzioliweb.reportesmodule.dtos.*;
import com.pazzioliweb.reportesmodule.service.MisReportesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Endpoints "Mi panel" — reportes filtrados al cajero/usuario logueado.
 *
 * El front pasa cajeroId y/o usuarioId como query params, leídos del
 * Redux authglobal.user.cajeroId y user.usuarioId.
 *
 * Base URL: /api/mis-reportes
 */
@RestController
@RequestMapping("/api/mis-reportes")
public class MisReportesController {

    private final MisReportesService service;

    public MisReportesController(MisReportesService service) {
        this.service = service;
    }

    /** Resumen del día actual (KPIs). */
    @GetMapping("/resumen-hoy")
    public ResponseEntity<MiResumenHoyDTO> resumenHoy(
            @RequestParam(required = false) Integer cajeroId,
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(service.getResumenHoy(cajeroId, usuarioId, fecha));
    }

    @GetMapping("/mis-ventas")
    public ResponseEntity<List<MiVentaDTO>> misVentas(
            @RequestParam Integer cajeroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] r = defaults(inicio, fin);
        return ResponseEntity.ok(service.getMisVentas(cajeroId, r[0], r[1]));
    }

    @GetMapping("/mis-recibos")
    public ResponseEntity<List<MiReciboDTO>> misRecibos(
            @RequestParam Integer usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] r = defaults(inicio, fin);
        return ResponseEntity.ok(service.getMisRecibos(usuarioId, r[0], r[1]));
    }

    @GetMapping("/mis-egresos")
    public ResponseEntity<List<MiEgresoDTO>> misEgresos(
            @RequestParam Integer usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] r = defaults(inicio, fin);
        return ResponseEntity.ok(service.getMisEgresos(usuarioId, r[0], r[1]));
    }

    @GetMapping("/mis-top-productos")
    public ResponseEntity<List<ProductoMasVendidoDTO>> misTopProductos(
            @RequestParam Integer cajeroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "10") int topN) {
        LocalDate[] r = defaults(inicio, fin);
        return ResponseEntity.ok(service.getMisTopProductos(cajeroId, r[0], r[1], topN));
    }

    @GetMapping("/mis-top-clientes")
    public ResponseEntity<List<VentasPorClienteDTO>> misTopClientes(
            @RequestParam Integer cajeroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "10") int topN) {
        LocalDate[] r = defaults(inicio, fin);
        return ResponseEntity.ok(service.getMisTopClientes(cajeroId, r[0], r[1], topN));
    }

    @GetMapping("/mis-movimientos-caja")
    public ResponseEntity<List<MovimientoCajaTipoDTO>> misMovimientosCaja(
            @RequestParam Integer cajeroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] r = defaults(inicio, fin);
        return ResponseEntity.ok(service.getMisMovimientosCaja(cajeroId, r[0], r[1]));
    }

    @GetMapping("/mis-ventas-por-dia")
    public ResponseEntity<List<VentasPorPeriodoDTO>> misVentasPorDia(
            @RequestParam Integer cajeroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] r = defaults(inicio, fin);
        return ResponseEntity.ok(service.getMisVentasPorDia(cajeroId, r[0], r[1]));
    }

    @GetMapping("/mis-ventas-por-hora")
    public ResponseEntity<List<VentasPorPeriodoDTO>> misVentasPorHora(
            @RequestParam Integer cajeroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate[] r = defaults(inicio, fin);
        return ResponseEntity.ok(service.getMisVentasPorHora(cajeroId, r[0], r[1]));
    }

    private LocalDate[] defaults(LocalDate inicio, LocalDate fin) {
        if (inicio == null) inicio = LocalDate.now().withDayOfMonth(1);
        if (fin == null) fin = LocalDate.now();
        return new LocalDate[]{inicio, fin};
    }
}
