package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Producto más vendido — para gráfica de barras / tabla ranking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoMasVendidoDTO {
    private String codigoProducto;
    private String descripcion;
    private String linea;
    private String grupo;
    private Long cantidadVendida;
    private BigDecimal totalVendido;
    private BigDecimal costoTotal;
    private BigDecimal utilidad;
    private String imagen;
}

