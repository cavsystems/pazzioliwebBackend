package com.pazzioliweb.comprobantesmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Asiento contable (partida doble). Representa una transacción contable
 * generada automáticamente desde un documento de origen (FC, VC, CC, CR, RC, CE, DV).
 * Cada asiento tiene varias líneas (debitos y créditos) que deben cuadrar.
 */
@Entity
@Table(name = "asientos_contables",
        indexes = {
                @Index(name = "idx_asiento_fecha", columnList = "fecha"),
                @Index(name = "idx_asiento_origen", columnList = "documento_origen_tipo,documento_origen_id"),
                @Index(name = "idx_asiento_comp", columnList = "comprobante_id")
        })
@Data
public class AsientoContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Número del asiento (puede coincidir con el del documento, ej. RC-1-15). */
    @Column(name = "numero_asiento", nullable = false, length = 40)
    private String numeroAsiento;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    /** Suma de débitos (debe igualar totalCredito). */
    @Column(name = "total_debito", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalDebito = BigDecimal.ZERO;

    /** Suma de créditos (debe igualar totalDebito). */
    @Column(name = "total_credito", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalCredito = BigDecimal.ZERO;

    /**
     * Tipo de documento de origen: FC, VC, CC, CR, RC, CE, DV.
     * Permite navegar desde el asiento al documento real sin tener FKs polimórficas.
     */
    @Column(name = "documento_origen_tipo", length = 10)
    private String documentoOrigenTipo;

    /** ID de la entidad de origen (venta_id, recibo_id, etc.) */
    @Column(name = "documento_origen_id")
    private Long documentoOrigenId;

    /** Comprobante contable del documento que generó este asiento. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_id")
    private ComprobanteContable comprobante;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "CONFIRMADO";    // CONFIRMADO / ANULADO

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // ─── Integración con facturación electrónica DIAN ─────────────
    // Estos campos solo aplican a asientos derivados de FC/VC (ventas)
    // que se factureran electrónicamente. NULL en los demás casos.

    /** Estado DIAN del documento asociado: PENDIENTE/AUTORIZADA/RECHAZADA/SIMULADA. */
    @Column(name = "estado_dian", length = 20)
    private String estadoDian;

    /** CUFE asignado por la DIAN al autorizar la factura. */
    @Column(name = "cufe", length = 200)
    private String cufe;

    /** Fecha-hora en que la DIAN autorizó. */
    @Column(name = "fecha_autorizacion_dian")
    private LocalDateTime fechaAutorizacionDian;

    /** Mensaje devuelto por la DIAN (errores de validación, observaciones, etc.). */
    @Column(name = "mensaje_dian", length = 500)
    private String mensajeDian;

    @OneToMany(mappedBy = "asiento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AsientoContableLinea> lineas = new ArrayList<>();
}
