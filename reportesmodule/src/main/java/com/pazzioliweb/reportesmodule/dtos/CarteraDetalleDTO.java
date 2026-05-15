package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Detalle individual de cuentas por cobrar — quién debe, cuánto y desde cuándo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarteraDetalleDTO {
    private Long id;
    private Integer clienteId;
    private String identificacion;
    private String clienteNombre;
    private String numeroVenta;
    private BigDecimal valorNeto;
    private BigDecimal saldo;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private Integer plazoDias;
    /** Positivo: días vencido. Negativo o cero: días por vencer / al día. */
    private Long diasVencido;
    private String estado;
    private String telefono;
}
