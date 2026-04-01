package com.pazzioliweb.ventasmodule.controller;

import com.pazzioliweb.ventasmodule.dtos.PedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import com.pazzioliweb.ventasmodule.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Void> crearPedido(@RequestBody PedidoDTO pedidoDTO) {
        pedidoService.crearPedido(pedidoDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoDTO>> getPedidosByCliente(@PathVariable Long clienteId) {
        List<PedidoDTO> pedidos = pedidoService.getPedidosByCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/detalle")
    public ResponseEntity<PedidoDTO> getPedidoDetalle(@RequestParam String numeroPedido) {
        PedidoDTO pedido = pedidoService.getPedidoByNumero(numeroPedido);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<PedidoDTO>> getPedidosActivos() {
        List<PedidoDTO> pedidos = pedidoService.getPedidosActivos();
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping("/{pedidoId}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long pedidoId, @RequestParam String nuevoEstado) {
        pedidoService.cambiarEstado(pedidoId, nuevoEstado);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{pedidoId}/cancelar")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long pedidoId) {
        pedidoService.cancelarPedido(pedidoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{pedidoId}/convertir-venta")
    public ResponseEntity<VentaDTO> convertirAVenta(@PathVariable Long pedidoId, @RequestBody List<VentaMetodoPagoDTO> metodosPago) {
        VentaDTO venta = pedidoService.convertirAVenta(pedidoId, metodosPago);
        return ResponseEntity.ok(venta);
    }
}

