package com.pazzioliweb.tesoreriamodule.entity;

import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "recibos_caja")
@EqualsAndHashCode(exclude = {"detalles"})
@ToString(exclude = {"detalles"})
public class ReciboCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer consecutivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tercero_id", nullable = false)
    private Terceros tercero;

    @Column(name = "tercero_nombre")
    private String terceroNombre;

    @Column(name = "tercero_nit")
    private String terceroNit;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal retefuente = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal reteica = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal reteiva = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodo_pago_id")
    private MetodosPago metodoPago;

    private String concepto;

    @Column(name = "centro_costo")
    private String centroCosto;

    @Column(nullable = false)
    private String estado = "ACTIVO";

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "cajero_id")
    private Integer cajeroId;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "reciboCaja", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleReciboCaja> detalles = new ArrayList<>();
}

