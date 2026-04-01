package com.pazzioliweb.ventasmodule.controller;

import com.pazzioliweb.ventasmodule.dtos.CotizacionDTO;
import com.pazzioliweb.ventasmodule.dtos.PedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import com.pazzioliweb.ventasmodule.service.CotizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}



