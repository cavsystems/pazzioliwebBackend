package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Valorización del inventario agrupada por línea, grupo o bodega.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValorizacionInventarioDTO {
    /** "linea", "grupo" o "bodega" */
    private String agrupacion;
    private String nombre;
    private Long cantidadProductos;
    private BigDecimal unidadesTotales;
    private BigDecimal valorTotal;
    private Double porcentaje;
}
