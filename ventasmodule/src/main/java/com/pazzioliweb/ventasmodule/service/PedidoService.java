package com.pazzioliweb.ventasmodule.service;

import com.pazzioliweb.ventasmodule.dtos.PedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;

import java.util.List;

public interface PedidoService {
    void crearPedido(PedidoDTO pedidoDTO);
    List<PedidoDTO> getPedidosByCliente(Long clienteId);
    PedidoDTO getPedidoByNumero(String numeroPedido);
    List<PedidoDTO> getPedidosActivos();
    void cambiarEstado(Long pedidoId, String nuevoEstado);
    void cancelarPedido(Long pedidoId);
    VentaDTO convertirAVenta(Long pedidoId, List<VentaMetodoPagoDTO> metodosPago);
}

