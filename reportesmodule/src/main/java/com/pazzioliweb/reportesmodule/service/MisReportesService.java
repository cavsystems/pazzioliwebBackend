package com.pazzioliweb.reportesmodule.service;

import com.pazzioliweb.reportesmodule.dtos.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Reportes filtrados al usuario / cajero logueado ("Mi panel").
 */
public interface MisReportesService {

    /** Resumen del día para el cajero/usuario logueado (KPIs). */
    MiResumenHoyDTO getResumenHoy(Integer cajeroId, Integer usuarioId, LocalDate fecha);

    /** Mis ventas del periodo. */
    List<MiVentaDTO> getMisVentas(Integer cajeroId, LocalDate inicio, LocalDate fin);

    /** Mis recibos de caja del periodo. */
    List<MiReciboDTO> getMisRecibos(Integer usuarioId, LocalDate inicio, LocalDate fin);

    /** Mis egresos del periodo. */
    List<MiEgresoDTO> getMisEgresos(Integer usuarioId, LocalDate inicio, LocalDate fin);

    /** Mis productos top vendidos. */
    List<ProductoMasVendidoDTO> getMisTopProductos(Integer cajeroId, LocalDate inicio, LocalDate fin, int topN);

    /** Mis clientes top. */
    List<VentasPorClienteDTO> getMisTopClientes(Integer cajeroId, LocalDate inicio, LocalDate fin, int topN);

    /** Mis movimientos de caja por tipo. */
    List<MovimientoCajaTipoDTO> getMisMovimientosCaja(Integer cajeroId, LocalDate inicio, LocalDate fin);

    /** Mis ventas por día (para gráfica). */
    List<VentasPorPeriodoDTO> getMisVentasPorDia(Integer cajeroId, LocalDate inicio, LocalDate fin);

    /** Mis ventas por hora (para gráfica). */
    List<VentasPorPeriodoDTO> getMisVentasPorHora(Integer cajeroId, LocalDate inicio, LocalDate fin);
}
