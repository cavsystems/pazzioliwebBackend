package com.pazzioliweb.ventasmodule.entity;

import com.pazzioliweb.tercerosmodule.entity.Terceros;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Cuenta por cobrar generada automáticamente cuando una venta
 * se realiza con método de pago a Crédito.
 *
 * Estados posibles: PENDIENTE, PARCIAL, PAGADA, VENCIDA
 */
@Data
@Entity
@Table(name = "cuentas_por_cobrar")
public class CuentaPorCobrar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Terceros cliente;

    @Column(nullable = false)
    private String nit;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "numero_venta", nullable = false)
    private String numeroVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @Column(name = "valor_neto", nullable = false)
    private BigDecimal valorNeto;

    /** Saldo pendiente por cobrar (se reduce con abonos) */
    @Column(nullable = false)
    private BigDecimal saldo;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "plazo_dias", nullable = false)
    private Integer plazoDias;

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();
}

