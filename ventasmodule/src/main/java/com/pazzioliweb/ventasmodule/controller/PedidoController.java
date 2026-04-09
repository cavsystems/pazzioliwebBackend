package com.pazzioliweb.ventasmodule.controller;

import com.pazzioliweb.ventasmodule.dtos.PedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import com.pazzioliweb.ventasmodule.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<PedidoDTO> crearPedido(@RequestBody PedidoDTO pedidoDTO) {

        return ResponseEntity.ok( pedidoService.crearPedido(pedidoDTO));
    }
    @GetMapping("/ultimo-pedido-id")
    public ResponseEntity<Long> getUltimaVentaId() {
        Long id = pedidoService.getUltimopedido();

        return ResponseEntity.ok(id+1);
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





    /**
     * Consulta pedidos con filtros opcionales combinables.
     * Todos los parámetros son opcionales y se pueden usar juntos o por separado.
     *
     * GET /api/ventas/filtrar?terceroId=1&vendedorId=2&cajeroId=3&fechaInicio=2025-01-01&fechaFin=2025-12-31
     */
    @GetMapping("/filtrar")
    public ResponseEntity<List<PedidoDTO>> filtrarVentas(
            @RequestParam(required = false) Long terceroId,
            @RequestParam(required = false) Integer vendedorId,
            @RequestParam(required = false) Integer cajeroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<PedidoDTO> ventas = pedidoService.getPedidosByFiltros(terceroId, vendedorId, cajeroId, fechaInicio, fechaFin);
        return ResponseEntity.ok(ventas);
    }
}


