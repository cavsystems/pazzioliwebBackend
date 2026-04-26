package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Ventas agrupadas por método de pago — para gráfica de torta / dona.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentasPorMetodoPagoDTO {
    private Integer metodoPagoId;
    private String metodoPagoNombre;
    private String sigla;
    private Long cantidadTransacciones;
    private BigDecimal totalMonto;
    private Double porcentaje;
}

