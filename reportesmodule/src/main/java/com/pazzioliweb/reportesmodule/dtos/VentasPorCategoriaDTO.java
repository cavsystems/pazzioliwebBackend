package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Ventas agrupadas por línea de producto — para gráfica de torta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentasPorCategoriaDTO {
    private String categoria;    // nombre de la línea o grupo
    private Long cantidadItems;
    private BigDecimal totalVendido;
    private Double porcentaje;
}

