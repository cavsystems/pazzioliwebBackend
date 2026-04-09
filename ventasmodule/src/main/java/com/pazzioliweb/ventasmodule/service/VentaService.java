package com.pazzioliweb.ventasmodule.service;

import com.pazzioliweb.ventasmodule.dtos.DetalleVentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;

import java.time.LocalDate;
import java.util.List;

public interface VentaService {
    VentaDTO crearVenta(VentaDTO ventaDTO);
    List<VentaDTO> getVentasByCliente(Long clienteId);
    VentaDTO getVentaByNumero(String numeroVenta);
    void ajustarPreciosVenta(List<DetalleVentaDTO> detalles);
    void completarVenta(Long ventaId, List<VentaMetodoPagoDTO> metodosPago);
    void devolverVenta(Long ventaId, List<DetalleVentaDTO> detallesDevueltos);
    void anularVenta(Long ventaId);
    Double getTotalVentasByFecha(LocalDate fechaInicio, LocalDate fechaFin);
    Long getCantidadVendidaByProducto(String codigoProducto, LocalDate fechaInicio, LocalDate fechaFin);
    Double getTotalVentasByCajero(Integer cajeroId, LocalDate fechaInicio, LocalDate fechaFin);
    List<VentaDTO> getVentasByFiltros(Long terceroId, Integer vendedorId, Integer cajeroId,
                                      LocalDate fechaInicio, LocalDate fechaFin);
    Long getUltimaVentaId();


}
