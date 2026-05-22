package com.pazzioliweb.metodospagomodule.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Cuenta bancaria del negocio (similar al módulo Bancos de Alegra).
 * Cada cuenta bancaria está vinculada a una cuenta contable del PUC
 * (típicamente subcuenta del grupo 1110 Bancos). El saldo se calcula
 * en tiempo real desde los asientos contables; aquí solo guardamos el
 * saldo inicial de apertura.
 */
@Entity
@Table(name = "cuentas_bancarias")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CuentaBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre interno de la cuenta (ej: "Bancolombia Principal"). */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /** Banco emisor (ej: "BANCOLOMBIA", "DAVIVIENDA", "BBVA"). */
    @Column(name = "banco", nullable = false, length = 80)
    private String banco;

    /** Número de cuenta del banco (puede estar enmascarado en la UI). */
    @Column(name = "numero_cuenta", length = 50)
    private String numeroCuenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoCuenta tipo = TipoCuenta.AHORROS;

    /** Moneda — por defecto COP. */
    @Column(name = "moneda", nullable = false, length = 10)
    private String moneda = "COP";

    /** Saldo de apertura al crear la cuenta (puede ser 0 si es nueva). */
    @Column(name = "saldo_inicial", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    /** Fecha de apertura interna (no necesariamente la fecha real del banco). */
    @Column(name = "fecha_apertura", nullable = false)
    private LocalDate fechaApertura = LocalDate.now();

    /** Cuenta contable del PUC asociada (típicamente subcuenta de 1110). Obligatoria. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_contable_id", nullable = false)
    private CuentaContable cuentaContable;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public enum TipoCuenta {
        AHORROS, CORRIENTE, OTRA
    }
}
