package com.pazzioliweb.comprasmodule.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;

@Data
@Entity
@Table(name = "legalizaciones")
public class Legalizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompra ordenCompra;  // Referencia a la orden creada

    @Column(name = "numero_factura_proveedor", nullable = false)
    private String numeroFacturaProveedor;

    @Column(name = "fecha_factura", nullable = false)
    private LocalDate fechaFactura;

    @Column(name = "total_factura", nullable = false)
    private BigDecimal totalFactura;

    @Column(name = "proveedor_id", nullable = false)
    private Long proveedorId;

    @Column(nullable = false)
    private String estado = "LEGALIZADA";

    @Column(name = "usuario_creacion", nullable = false)
    private String usuarioCreacion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();

    /**
     * Comprobante contable usado al legalizar (heredado de la orden de compra).
     * Permite identificar la legalización por prefijo (CC-1-3 / CR-1-3) en
     * historial y reportes.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_id")
    private ComprobanteContable comprobante;

    @Column(name = "consecutivo_comprobante")
    private Integer consecutivoComprobante;
}