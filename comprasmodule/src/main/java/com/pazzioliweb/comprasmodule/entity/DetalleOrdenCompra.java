package com.pazzioliweb.comprasmodule.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "detalles_orden_compra")
public class DetalleOrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompra ordenCompra;

    @Column(name = "codigo_producto", nullable = false)
    private String codigoProducto;



    @Column(name = "sku", nullable = false)
    private String sku;
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

    @Column(nullable = false)
    private boolean recibido = false;

    @Column(name = "cantidad_recibida", nullable = false)
    private Integer cantidadRecibida = 0;

    @Column(name = "manifiesto")
    private String manifiesto;
    // Getters y setters generados por Lombok
}
