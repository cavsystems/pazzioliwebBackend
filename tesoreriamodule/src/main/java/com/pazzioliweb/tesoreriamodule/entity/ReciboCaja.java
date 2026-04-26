package com.pazzioliweb.tesoreriamodule.entity;

import com.pazzioliweb.tercerosmodule.entity.Terceros;
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
@EqualsAndHashCode(exclude = {"detalles", "mediosPago"})
@ToString(exclude = {"detalles", "mediosPago"})
public class ReciboCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer consecutivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tercero_id")
    private Terceros tercero;

    @Column(name = "tercero_nombre")
    private String terceroNombre;

    @Column(name = "tercero_nit")
    private String terceroNit;

    @Column(name = "concepto_abierto", nullable = false)
    private Boolean conceptoAbierto = false;

    @Column(name = "monto_concepto_abierto", precision = 18, scale = 2)
    private BigDecimal montoConceptoAbierto;

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
    private BigDecimal averias = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal fletes = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "fecha_recibo")
    private LocalDate fechaRecibo;

    @Column(columnDefinition = "TEXT")
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

    @OneToMany(mappedBy = "reciboCaja", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReciboCajaMedioPago> mediosPago = new ArrayList<>();
}
