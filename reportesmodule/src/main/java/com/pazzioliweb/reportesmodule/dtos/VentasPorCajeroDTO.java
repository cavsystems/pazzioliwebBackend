package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Ventas agrupadas por cajero — para gráfica de barras.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentasPorCajeroDTO {
    private Integer cajeroId;
    private String cajeroNombre;
    private Long cantidadVentas;
    private BigDecimal totalVendido;
    private BigDecimal totalEfectivo;
    private BigDecimal totalElectronico;
}

