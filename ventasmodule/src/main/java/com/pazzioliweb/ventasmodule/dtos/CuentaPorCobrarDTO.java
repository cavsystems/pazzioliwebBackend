package com.pazzioliweb.ventasmodule.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CuentaPorCobrarDTO {
    private Long id;
    private Integer clienteId;
    private String nit;
    private String nombre;
    private String numeroVenta;
    private Long ventaId;
    private BigDecimal valorNeto;
    private BigDecimal saldo;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private Integer plazoDias;
    private String estado;
    private LocalDate fechaCreacion;
}

