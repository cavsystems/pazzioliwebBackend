package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Estado de cartera por vendedor con buckets de antigüedad
 * (al día / 1-30 / 31-60 / 61-90 / >90 días).
 * vendedorId = 0 agrupa las cuentas por cobrar sin vendedor asociado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarteraVendedorDTO {
    private Integer vendedorId;
    private String vendedorNombre;
    private BigDecimal alDia;
    private BigDecimal dias1a30;
    private BigDecimal dias31a60;
    private BigDecimal dias61a90;
    private BigDecimal mas90;
    private BigDecimal totalSaldo;
    private Long cantidadCuentas;
}
