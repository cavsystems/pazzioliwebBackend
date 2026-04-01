package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.comprasmodule.dtos.CuentaPorPagarDTO;
import com.pazzioliweb.comprasmodule.entity.CuentaPorPagar;

import java.util.List;

public interface CuentaPorPagarService {
    CuentaPorPagar crear(CuentaPorPagarDTO cuentaPorPagarDTO);
    List<CuentaPorPagarDTO> listarPendientes();
    void pagar(Long id);
    void eliminarPorNumeroFactura(String numeroFactura);
    void actualizarNumeroFacturaProveedor(String numeroFactura, String numeroFacturaProveedor);
}
