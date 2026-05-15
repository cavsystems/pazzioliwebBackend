package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Histórico mensual de ventas de un producto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoProductoDTO {
    /** Formato YYYY-MM */
    private String periodo;
    private BigDecimal cantidad;
    private BigDecimal totalVendido;
    private BigDecimal costoTotal;
    private BigDecimal utilidad;
}
