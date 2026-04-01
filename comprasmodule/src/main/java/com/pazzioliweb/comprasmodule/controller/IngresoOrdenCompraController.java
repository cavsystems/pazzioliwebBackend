package com.pazzioliweb.comprasmodule.controller;

import com.pazzioliweb.comprasmodule.dtos.DetalleOrdenCompraDTO;
import com.pazzioliweb.comprasmodule.dtos.OrdenCompraDTO;
import com.pazzioliweb.comprasmodule.entity.OrdenCompra;
import com.pazzioliweb.comprasmodule.service.IngresoOrdenCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras/ingreso-orden-compra")
public class IngresoOrdenCompraController {

    private final IngresoOrdenCompraService ingresoService;

    @Autowired
    public IngresoOrdenCompraController(IngresoOrdenCompraService ingresoService) {
        this.ingresoService = ingresoService;
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<OrdenCompraDTO>> getOrdenesPendientesByProveedor(@RequestParam Integer proveedorId) {
        List<OrdenCompraDTO> ordenes = ingresoService.getOrdenesPendientesByProveedor(proveedorId);
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/detalle")
    public ResponseEntity<OrdenCompraDTO> getOrdenCompraDetalle(@RequestParam String numeroOrden) {
        OrdenCompraDTO orden = ingresoService.getOrdenCompraByNumero(numeroOrden);
        return ResponseEntity.ok(orden);
    }

    @PostMapping("/{ordenId}")
    public ResponseEntity<Void> ingresarOrdenCompra(@PathVariable Long ordenId, @RequestBody List<DetalleOrdenCompraDTO> detallesRecibidos, @RequestParam String numeroFacturaProveedor) {
        ingresoService.ingresarOrdenCompra(ordenId, detallesRecibidos, numeroFacturaProveedor);
        return ResponseEntity.ok().build();
    }
}
