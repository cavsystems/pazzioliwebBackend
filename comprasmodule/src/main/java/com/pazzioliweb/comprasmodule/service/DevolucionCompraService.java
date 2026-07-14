package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.comprasmodule.dtos.DevolucionCompraDTO;
import com.pazzioliweb.comprasmodule.dtos.DevolucionCompraRequestDTO;

import java.util.List;

public interface DevolucionCompraService {

    /** Registra una devolución de compra (nota débito) sobre una orden con mercancía recibida. */
    DevolucionCompraDTO registrar(DevolucionCompraRequestDTO request);

    /** Devoluciones de compra de una orden. */
    List<DevolucionCompraDTO> listarPorOrden(Long ordenCompraId);

    /** Detalle de una devolución de compra. */
    DevolucionCompraDTO obtenerPorId(Long id);

    /** Anula una devolución de compra REGISTRADA y revierte asiento, inventario y CxP. */
    DevolucionCompraDTO anular(Long id, String motivo, String usuario);
}
