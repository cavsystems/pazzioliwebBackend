package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Utilidad por producto — para análisis de rentabilidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentabilidadProductoDTO {
    private String codigoProducto;
    private String descripcion;
    private String linea;
    private Long cantidadVendida;
    private BigDecimal ingresos;
    private BigDecimal costo;
    private BigDecimal utilidad;
    private Double margenPorcentaje;
    /** Precio promedio real de venta = ingresos / cantidadVendida */
    private BigDecimal precioPromedio;
    /** Costo unitario actual del producto (de la tabla productos) */
    private BigDecimal costoUnitario;
}

