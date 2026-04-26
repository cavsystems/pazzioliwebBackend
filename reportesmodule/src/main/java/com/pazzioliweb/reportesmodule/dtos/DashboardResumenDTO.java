package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Dashboard principal — resumen general del POS.
 * Pensado para alimentar tarjetas KPI en el frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumenDTO {

    // ── Ventas del período ──
    private BigDecimal totalVentas;
    private Long cantidadVentas;
    private BigDecimal totalDevoluciones;
    private Long cantidadDevoluciones;
    private BigDecimal ventaNeta; // totalVentas - totalDevoluciones

    // ── Costo y utilidad ──
    private BigDecimal totalCosto;
    private BigDecimal utilidadBruta; // ventaNeta - totalCosto
    private Double margenPorcentaje; // (utilidadBruta / ventaNeta) * 100

    // ── Caja ──
    private BigDecimal totalEfectivo;
    private BigDecimal totalMediosElectronicos;
    private BigDecimal totalCredito;

    // ── Inventario ──
    private BigDecimal valorInventario;
    private Long productosConStockBajo;

    // ── Cartera ──
    private BigDecimal carteraPendiente;
    private Long cuentasPorCobrarVencidas;

    // ── Contadores ──
    private Long totalClientes;
    private Long totalProductosActivos;
    private BigDecimal ticketPromedio; // totalVentas / cantidadVentas

    // ── Compras ──
    private BigDecimal totalCompras;
    private BigDecimal cuentasPorPagarPendientes;
}

