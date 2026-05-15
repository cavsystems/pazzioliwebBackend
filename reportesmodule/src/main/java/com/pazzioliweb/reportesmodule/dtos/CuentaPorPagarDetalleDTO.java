package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Detalle individual de cuentas por pagar — a qué proveedor le debemos,
 * cuánto y desde cuándo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaPorPagarDetalleDTO {
    private Long id;
    private Integer proveedorId;
    private String nit;
    private String proveedorNombre;
    private String numeroFactura;
    private String numeroFacturaProveedor;
    private BigDecimal valorNeto;
    private BigDecimal saldo;
    private LocalDate fechaCreacion;
    private LocalDate fechaVencimiento;
    /** Positivo: días vencido. Negativo o cero: días por vencer / al día. */
    private Long diasVencido;
    private String estado;
    private String telefono;
}
