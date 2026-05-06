package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Cartera agrupada por antigüedad (aging buckets).
 * Útil para gestión de cobros: ver cuánto saldo está vencido por rangos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarteraAgingDTO {
    /** "Al día" / "1-30 días" / "31-60 días" / "61-90 días" / ">90 días". */
    private String rangoEdad;
    private Integer ordenRango;          // para ordenar en el front
    private Long cantidad;
    private BigDecimal totalSaldo;
    private Double porcentaje;
}
