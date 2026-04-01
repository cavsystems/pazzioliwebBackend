package com.pazzioliweb.ventasmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "detalles_cotizacion")
public class DetalleCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotizacion_id", nullable = false)
    private Cotizacion cotizacion;

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

    // Getters y setters generados por Lombok
}

