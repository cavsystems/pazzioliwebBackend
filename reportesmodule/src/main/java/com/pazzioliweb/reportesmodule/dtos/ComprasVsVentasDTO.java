package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Comparativa Compras vs Ventas — para gráfica de barras agrupadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComprasVsVentasDTO {
    private String periodo;
    private BigDecimal totalVentas;
    private BigDecimal totalCompras;
    private BigDecimal diferencia; // ventas - compras
}

