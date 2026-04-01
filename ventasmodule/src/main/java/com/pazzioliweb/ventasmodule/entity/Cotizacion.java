package com.pazzioliweb.ventasmodule.entity;

import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.cajerosmodule.entity.Cajero;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "cotizaciones")
public class Cotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cotizacion", nullable = false, unique = true)
    private String numeroCotizacion;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Terceros cliente;

    @ManyToOne
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodegas bodega;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false)
    private String estado; // BORRADOR, ENVIADA, ACEPTADA, RECHAZADA, VENCIDA

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    private BigDecimal gravada = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal iva = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal descuentos = BigDecimal.ZERO;

    @Column(name = "total_cotizacion", nullable = false)
    private BigDecimal totalCotizacion = BigDecimal.ZERO;

    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCotizacion> items;

    @ManyToOne
    @JoinColumn(name = "cajero_id")
    private Cajero cajero;

    @Column(name = "usuario_creacion", nullable = false)
    private String usuarioCreacion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();
    // Getters y setters generados por Lombok
}

