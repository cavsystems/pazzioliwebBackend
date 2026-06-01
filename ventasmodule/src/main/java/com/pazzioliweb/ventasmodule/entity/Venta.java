package com.pazzioliweb.ventasmodule.entity;

import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.vendedoresmodule.entity.Vendedores;

@Data
@Entity
@Table(name = "ventas")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_venta", nullable = false)
    private String numeroVenta;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Terceros cliente;

    @ManyToOne
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodegas bodega;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_entrega_esperada", nullable = false)
    private LocalDate fechaEntregaEsperada;

    @Column(nullable = false)
    private String estado; // PENDIENTE, COMPLETADA, ANULADA

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    private BigDecimal gravada = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal iva = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal descuentos = BigDecimal.ZERO;
    @Column(name = "total_venta", nullable = false)
    private BigDecimal totalVenta = BigDecimal.ZERO;

    // ── Retenciones que el CLIENTE nos practica (retenciones sufridas).
    //    total_venta sigue siendo el BRUTO (gravada + IVA). El cliente paga
    //    neto = bruto − retenciones; la diferencia se registra como anticipo
    //    de impuestos (1355) en el asiento. ──
    @Column(name = "retefuente", nullable = false)
    private BigDecimal retefuente = BigDecimal.ZERO;
    @Column(name = "reteiva", nullable = false)
    private BigDecimal reteiva = BigDecimal.ZERO;
    @Column(name = "reteica", nullable = false)
    private BigDecimal reteica = BigDecimal.ZERO;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> items;

    @ManyToOne
    @JoinColumn(name = "cajero_id")
    private Cajero cajero;

    @ManyToOne
    @JoinColumn(name = "vendedor_id")
    private Vendedores vendedor;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaMetodoPago> metodosPago;

    @Column(name = "usuario_creacion", nullable = false)
    private String usuarioCreacion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();

    /** Comprobante contable usado para generar el número de venta (FC o VC). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_id")
    private ComprobanteContable comprobante;

    /** Consecutivo del comprobante en el momento de la venta (para auditoría). */
    @Column(name = "consecutivo_comprobante")
    private Integer consecutivoComprobante;
    // Getters y setters generados por Lombok
}
