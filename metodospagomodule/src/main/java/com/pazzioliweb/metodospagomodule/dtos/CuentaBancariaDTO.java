package com.pazzioliweb.metodospagomodule.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CuentaBancariaDTO {
    private Long id;
    private String nombre;
    private String banco;
    private String numeroCuenta;
    private String tipo;       // AHORROS / CORRIENTE / OTRA
    private String moneda;
    private BigDecimal saldoInicial;
    private LocalDate fechaApertura;

    private Integer cuentaContableId;
    private String cuentaContableCodigo;
    private String cuentaContableNombre;

    private Boolean activo;
    private String observaciones;
    private LocalDateTime fechaCreacion;

    /** Saldo actual calculado en tiempo real (= saldoInicial + débitos − créditos). */
    private BigDecimal saldoActual;
}
