package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Análisis ABC de productos por contribución a ventas.
 * Clase A: top 80% del valor vendido; B: 80–95%; C: 95–100%.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbcProductoDTO {
    private String codigoProducto;
    private String descripcion;
    private String linea;
    private BigDecimal cantidadVendida;
    private BigDecimal totalVendido;
    /** Porcentaje individual de contribución a las ventas totales */
    private Double porcentaje;
    /** Porcentaje acumulado (ordenado de mayor a menor) */
    private Double porcentajeAcumulado;
    private String clase;        // "A", "B", "C"
    private BigDecimal stockActual;
}
