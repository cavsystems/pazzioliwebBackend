package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Producto con stock bajo / crítico — para alertas en dashboard.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoStockBajoDTO {
    private Long varianteId;
    private String sku;
    private String descripcion;
    private String linea;
    private String bodega;
    private BigDecimal existenciaActual;
    private BigDecimal stockMinimo;
    private BigDecimal stockMaximo;
    private BigDecimal costo;
    private String imagen;
}

