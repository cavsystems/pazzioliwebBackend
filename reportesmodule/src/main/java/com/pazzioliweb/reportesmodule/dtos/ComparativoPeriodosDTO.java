package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Comparativo de un periodo actual vs un periodo previo.
 * Útil para ver "cómo voy vs el periodo anterior".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComparativoPeriodosDTO {
    private String etiquetaActual;
    private String etiquetaAnterior;

    private BigDecimal ventasActual    = BigDecimal.ZERO;
    private BigDecimal ventasAnterior  = BigDecimal.ZERO;
    private Double deltaVentas         = 0.0;        // % cambio

    private Long cantidadVentasActual    = 0L;
    private Long cantidadVentasAnterior  = 0L;
    private Double deltaCantidadVentas   = 0.0;

    private BigDecimal ticketPromedioActual    = BigDecimal.ZERO;
    private BigDecimal ticketPromedioAnterior  = BigDecimal.ZERO;
    private Double deltaTicketPromedio         = 0.0;

    private BigDecimal devolucionesActual    = BigDecimal.ZERO;
    private BigDecimal devolucionesAnterior  = BigDecimal.ZERO;
    private Double deltaDevoluciones         = 0.0;

    private BigDecimal utilidadActual    = BigDecimal.ZERO;
    private BigDecimal utilidadAnterior  = BigDecimal.ZERO;
    private Double deltaUtilidad         = 0.0;
}
