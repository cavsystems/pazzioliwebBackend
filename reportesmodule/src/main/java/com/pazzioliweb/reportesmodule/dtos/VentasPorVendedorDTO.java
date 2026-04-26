package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Ventas agrupadas por vendedor — para gráfica de barras / ranking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentasPorVendedorDTO {
    private Integer vendedorId;
    private String vendedorNombre;
    private Long cantidadVentas;
    private BigDecimal totalVendido;
    private BigDecimal ticketPromedio;
}

