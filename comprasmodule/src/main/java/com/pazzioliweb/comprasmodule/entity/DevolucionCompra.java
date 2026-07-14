package com.pazzioliweb.comprasmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Devolución de COMPRA (nota débito al proveedor): la mercancía comprada se devuelve total o
 * parcialmente. Revierte proporcionalmente la compra (inventario, IVA descontable, retención
 * practicada y CxP) y saca la mercancía del inventario. Solo aplica sobre órdenes cuya mercancía
 * ya fue recibida (ingreso/legalización).
 */
@Data
@Entity
@Table(name = "devoluciones_compra")
public class DevolucionCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompra ordenCompra;

    @Column(name = "numero_devolucion", nullable = false)
    private String numeroDevolucion;

    @Column(length = 300)
    private String motivo;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    /** REGISTRADA, ANULADA */
    @Column(nullable = false)
    private String estado = "REGISTRADA";

    /** Base devuelta sin IVA */
    @Column(name = "total_devuelto", nullable = false)
    private BigDecimal totalDevuelto = BigDecimal.ZERO;

    @Column(name = "iva_devuelto", nullable = false)
    private BigDecimal ivaDevuelto = BigDecimal.ZERO;

    /** Retención practicada que se reversa (proporcional a lo devuelto) */
    @Column(name = "retencion_devuelta", nullable = false)
    private BigDecimal retencionDevuelta = BigDecimal.ZERO;

    /** Neto devuelto = base + IVA − retención (lo que baja la CxP / se recupera del proveedor) */
    @Column(name = "total_neto", nullable = false)
    private BigDecimal totalNeto = BigDecimal.ZERO;

    /** Monto realmente debitado a CxP en el asiento (0 si fue contado o no se pudo postear).
     *  Se usa al anular para reponer exactamente lo reducido en el auxiliar de CxP. */
    @Column(name = "monto_cxp_debitado", nullable = false)
    private BigDecimal montoCxpDebitado = BigDecimal.ZERO;

    @Column(name = "nit_proveedor", length = 50)
    private String nitProveedor;

    @Column(name = "nombre_proveedor", length = 200)
    private String nombreProveedor;

    @Column(name = "usuario_creacion", nullable = false)
    private String usuarioCreacion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();

    @Column(name = "motivo_anulacion", length = 300)
    private String motivoAnulacion;

    @Column(name = "fecha_anulacion")
    private LocalDate fechaAnulacion;

    @Column(name = "usuario_anulacion", length = 100)
    private String usuarioAnulacion;

    @OneToMany(mappedBy = "devolucionCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleDevolucionCompra> items;
}
