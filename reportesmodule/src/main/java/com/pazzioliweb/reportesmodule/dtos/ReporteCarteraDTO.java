package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Resumen de cartera (cuentas por cobrar) — para gráfica de barras por estado / vencimiento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteCarteraDTO {
    private String estado;           // PENDIENTE, PARCIAL, PAGADA, VENCIDA
    private Long cantidad;
    private BigDecimal totalValorNeto;
    private BigDecimal totalSaldo;
}

