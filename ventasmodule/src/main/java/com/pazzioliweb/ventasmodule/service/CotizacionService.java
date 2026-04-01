package com.pazzioliweb.ventasmodule.service;

import com.pazzioliweb.ventasmodule.dtos.CotizacionDTO;
import com.pazzioliweb.ventasmodule.dtos.PedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;

import java.util.List;

public interface CotizacionService {
    void crearCotizacion(CotizacionDTO cotizacionDTO);
    List<CotizacionDTO> getCotizacionesByCliente(Long clienteId);
    CotizacionDTO getCotizacionByNumero(String numeroCotizacion);
    List<CotizacionDTO> getCotizacionesActivas();
    void enviarCotizacion(Long cotizacionId);
    void aceptarCotizacion(Long cotizacionId);
    void rechazarCotizacion(Long cotizacionId);
    void anularCotizacion(Long cotizacionId);
    PedidoDTO convertirAPedido(Long cotizacionId);
    VentaDTO convertirAVenta(Long cotizacionId, List<VentaMetodoPagoDTO> metodosPago);
}


