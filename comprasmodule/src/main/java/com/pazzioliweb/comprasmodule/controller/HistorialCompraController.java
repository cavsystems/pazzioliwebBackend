package com.pazzioliweb.comprasmodule.controller;

import com.pazzioliweb.comprasmodule.dtos.HistorialCompraDTO;
import com.pazzioliweb.comprasmodule.service.HistorialCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/compras/historial")
public class HistorialCompraController {

    private final HistorialCompraService historialService;

    @Autowired
    public HistorialCompraController(HistorialCompraService historialService) {
        this.historialService = historialService;
    }

    @GetMapping
    public ResponseEntity<Page<HistorialCompraDTO>> obtenerHistorial(
            @RequestParam(required = false) Integer bodegaId,
            @RequestParam(required = false) Integer proveedorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String estado,
            Pageable pageable) {

        return ResponseEntity.ok(historialService.obtenerHistorial(
                bodegaId, proveedorId, fechaInicio, fechaFin, estado, pageable));
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Object> obtenerEstadisticas(
            @RequestParam(required = false) Integer bodegaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        return ResponseEntity.ok(historialService.obtenerEstadisticas(bodegaId, fechaInicio, fechaFin));
    }
}
