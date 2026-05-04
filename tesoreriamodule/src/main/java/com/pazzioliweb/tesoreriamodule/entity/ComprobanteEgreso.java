package com.pazzioliweb.tesoreriamodule.entity;

import com.pazzioliweb.comprobantesmodule.entity.ConceptoAbierto;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
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
@Table(name = "comprobantes_egreso")
@EqualsAndHashCode(exclude = {"detalles", "mediosPago"})
@ToString(exclude = {"detalles", "mediosPago"})
public class ComprobanteEgreso {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concepto_abierto_id")
    private ConceptoAbierto conceptoAbiertoRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_contable_id")
    private CuentaContable cuentaContable;

    @Column(name = "beneficiario_nombre", length = 200)
    private String beneficiarioNombre;

    @Column(name = "beneficiario_documento", length = 50)
    private String beneficiarioDocumento;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "fecha_egreso")
    private LocalDate fechaEgreso;

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

    @Column(name = "motivo_anulacion", columnDefinition = "TEXT")
    private String motivoAnulacion;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @Column(name = "anulado_por_usuario_id")
    private Integer anuladoPorUsuarioId;

    @OneToMany(mappedBy = "comprobanteEgreso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleComprobanteEgreso> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "comprobanteEgreso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComprobanteEgresoMedioPago> mediosPago = new ArrayList<>();
}
