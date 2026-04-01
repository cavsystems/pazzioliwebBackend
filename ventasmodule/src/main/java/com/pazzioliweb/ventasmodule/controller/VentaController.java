package com.pazzioliweb.ventasmodule.controller;

import com.pazzioliweb.ventasmodule.dtos.DetalleVentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import com.pazzioliweb.ventasmodule.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    @Autowired
    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VentaDTO>> getVentasByCliente(@PathVariable Long clienteId) {
        List<VentaDTO> ventas = ventaService.getVentasByCliente(clienteId);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/detalle")
    public ResponseEntity<VentaDTO> getVentaDetalle(@RequestParam String numeroVenta) {
        VentaDTO venta = ventaService.getVentaByNumero(numeroVenta);
        return ResponseEntity.ok(venta);
    }

    @PostMapping("/crear")
    public ResponseEntity<VentaDTO> crearVenta(@RequestBody VentaDTO ventaDTO) {

        return ResponseEntity.ok(ventaService.crearVenta(ventaDTO));
    }

    @PostMapping("/ajustar-precios")
    public ResponseEntity<Void> ajustarPrecios(@RequestBody List<DetalleVentaDTO> detalles) {
        ventaService.ajustarPreciosVenta(detalles);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{ventaId}/completar")
    public ResponseEntity<Void> completarVenta(@PathVariable Long ventaId, @RequestBody List<VentaMetodoPagoDTO> metodosPago) {
        ventaService.completarVenta(ventaId, metodosPago);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{ventaId}/devolver")
    public ResponseEntity<Void> devolverVenta(@PathVariable Long ventaId, @RequestBody List<DetalleVentaDTO> detallesDevueltos) {
        ventaService.devolverVenta(ventaId, detallesDevueltos);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{ventaId}/anular")
    public ResponseEntity<Void> anularVenta(@PathVariable Long ventaId) {
        ventaService.anularVenta(ventaId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reportes/total-ventas")
    public ResponseEntity<Double> getTotalVentasByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        Double total = ventaService.getTotalVentasByFecha(fechaInicio, fechaFin);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/reportes/cantidad-producto")
    public ResponseEntity<Long> getCantidadVendidaByProducto(
            @RequestParam String codigoProducto,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        Long cantidad = ventaService.getCantidadVendidaByProducto(codigoProducto, fechaInicio, fechaFin);
        return ResponseEntity.ok(cantidad);
    }

    @GetMapping("/reportes/total-cajero")
    public ResponseEntity<Double> getTotalVentasByCajero(
            @RequestParam Integer cajeroId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        Double total = ventaService.getTotalVentasByCajero(cajeroId, fechaInicio, fechaFin);
        return ResponseEntity.ok(total);
    }
}
