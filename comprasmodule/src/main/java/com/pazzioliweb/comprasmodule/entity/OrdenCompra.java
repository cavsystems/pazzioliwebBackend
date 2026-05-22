package com.pazzioliweb.comprasmodule.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.tercerosmodule.entity.Terceros;

@Data
@Entity
@Table(name = "ordenes_compra")
public class OrdenCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_orden", nullable = false)
    private String numeroOrden;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Terceros proveedor;

    @ManyToOne
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodegas bodega;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_entrega_esperada", nullable = false)
    private LocalDate fechaEntregaEsperada;

    @Column(nullable = false)
    private String estado; //  por defecto arranque como PENDIENTE, RECIBIDA_PARCIAL, RECIBIDA, ANULADA

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    private BigDecimal gravada = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal iva = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal descuentos = BigDecimal.ZERO;

    @Column(name = "total_orden_compra", nullable = false)
    private BigDecimal totalOrdenCompra = BigDecimal.ZERO;

    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrdenCompra> items;

    /** Métodos de pago utilizados al pagar esta compra (mixtos posibles). */
    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenCompraMetodoPago> metodosPago;

    @Column(name = "usuario_creacion", nullable = false)
    private String usuarioCreacion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();

    /** Cajero que registró la compra (necesario para resolver el comprobante). */
    @Column(name = "cajero_id")
    private Integer cajeroId;

    /** Comprobante contable usado para generar el número de orden (CC o CR). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_id")
    private ComprobanteContable comprobante;

    @Column(name = "consecutivo_comprobante")
    private Integer consecutivoComprobante;

    // Getters y setters generados por Lombok
}
