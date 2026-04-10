package com.pazzioliweb.ventasmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Detalle de cada ítem devuelto dentro de una devolución de venta.
 */
@Data
@Entity
@Table(name = "detalles_devolucion_venta")
public class DetalleDevolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucion_id", nullable = false)
    private Devolucion devolucion;

    /** Referencia al DetalleVenta original */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_venta_id", nullable = false)
    private DetalleVenta detalleVenta;

    /** Cantidad efectivamente devuelta */
    @Column(name = "cantidad_devuelta", nullable = false)
    private Integer cantidadDevuelta;

    /** Precio unitario al momento de la venta */
    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;

    /** IVA de esta línea devuelta */
    @Column(name = "iva_linea", nullable = false)
    private BigDecimal ivaLinea = BigDecimal.ZERO;

    /** Total de esta línea (precioUnitario * cantidadDevuelta) */
    @Column(name = "total_linea", nullable = false)
    private BigDecimal totalLinea = BigDecimal.ZERO;

    /** Motivo específico del ítem */
    @Column(length = 500)
    private String motivo;
}

