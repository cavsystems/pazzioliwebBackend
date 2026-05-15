package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Stock total actual por SKU/código de producto.
 * Útil para cruzar con reportes de ventas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockProductoDTO {
    private String codigoProducto;
    private BigDecimal stockTotal;
}
