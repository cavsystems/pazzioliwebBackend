package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Resumen de cuentas por pagar agrupado por estado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentasPorPagarResumenDTO {
    private String estado;
    private Long cantidad;
    private BigDecimal totalValorNeto;
    private BigDecimal totalSaldo;
}
