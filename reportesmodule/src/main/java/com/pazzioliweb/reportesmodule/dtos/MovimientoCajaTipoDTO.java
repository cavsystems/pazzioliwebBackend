package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Movimiento de caja agrupado por tipo — para gráfica de barras apiladas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCajaTipoDTO {
    private String tipoMovimiento;
    private Long cantidad;
    private BigDecimal totalMonto;
    private BigDecimal totalEfectivo;
    private BigDecimal totalElectronico;
}

