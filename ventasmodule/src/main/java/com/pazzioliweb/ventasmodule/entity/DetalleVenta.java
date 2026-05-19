package com.pazzioliweb.ventasmodule.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "detalles_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @Column(name = "codigo_producto", nullable = false)
    private String codigoProducto;

    @Column(name = "codigo_barras", nullable = false)
    private String codigoBarras;

    @Column(name = "descripcion_producto", nullable = false)
    private String descripcionProducto;

    @Column(name = "observacion_producto", nullable = false)
    private String observacionProducto;

    @Column(name = "referencia_variantes", nullable = false)
    private String referenciaVariantes;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal iva;

    @Column(nullable = false)
    private BigDecimal total;

    /**
     * Número de guía/manifiesto/remisión asociado a esta línea de venta.
     * Equivalente al "manifiesto" en compras: documento de transporte que
     * acompaña la salida de mercancía (guía del transportador, número de
     * remisión emitida al cliente, etc.). Texto libre, opcional.
     */
    @Column(name = "manifiesto", length = 255)
    private String manifiesto;

    // Getters y setters generados por Lombok
}
