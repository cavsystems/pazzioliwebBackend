package com.pazzioliweb.ventasmodule.entity;

import com.pazzioliweb.cajerosmodule.entity.Cajero;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


/**
 * Encabezado de una devolución de venta.
 * Una venta puede tener múltiples devoluciones parciales.
 */
@Data
@Entity
@Table(name = "devoluciones_venta")
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Venta de origen */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    /** Número de devolución (ej. DEV-00001) */
    @Column(name = "numero_devolucion", nullable = false, unique = true)
    private String numeroDevolucion;

    /** Motivo general de la devolución */
    @Column(nullable = false)
    private String motivo;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Estado: REGISTRADA, ANULADA
     */
    @Column(nullable = false)
    private String estado = "REGISTRADA";

    /** Total de items devueltos (subtotal sin IVA) */
    @Column(name = "total_devuelto", nullable = false)
    private BigDecimal totalDevuelto = BigDecimal.ZERO;

    /** IVA total devuelto */
    @Column(name = "iva_devuelto", nullable = false)
    private BigDecimal ivaDevuelto = BigDecimal.ZERO;

    /** Total neto devuelto (totalDevuelto + ivaDevuelto) */
    @Column(name = "total_neto", nullable = false)
    private BigDecimal totalNeto = BigDecimal.ZERO;

    @Column(name = "usuario_creacion", nullable = false)
    private String usuarioCreacion;

    /** Cajero que registró la devolución (puede ser null si no hay sesión activa) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cajero_id")
    private Cajero cajero;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();

    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleDevolucion> items;
}


