package com.pazzioliweb.comprasmodule.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CuentaPorPagarDTO {
    private Long id;
    private String nit;
    private String nombre;
    private LocalDate fechaVencimiento;
    private String numeroFactura;
    private BigDecimal valorNeto;
    private String estado;
    private LocalDate fechaCreacion;
    private Integer proveedorId;
    private String numeroFacturaProveedor;
}
