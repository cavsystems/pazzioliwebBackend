package com.pazzioliweb.comprobantesmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Línea individual de un asiento contable. Cada asiento tiene 2+ líneas;
 * la suma de débitos debe igualar la suma de créditos del mismo asiento.
 */
@Entity
@Table(name = "asientos_contables_lineas",
        indexes = {
                @Index(name = "idx_linea_asiento", columnList = "asiento_id"),
                @Index(name = "idx_linea_cuenta", columnList = "cuenta_contable_id"),
                @Index(name = "idx_linea_tercero", columnList = "tercero_id")
        })
@Data
public class AsientoContableLinea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asiento_id", nullable = false)
    private AsientoContable asiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_contable_id", nullable = false)
    private CuentaContable cuentaContable;

    @Column(name = "debito", nullable = false, precision = 18, scale = 2)
    private BigDecimal debito = BigDecimal.ZERO;

    @Column(name = "credito", nullable = false, precision = 18, scale = 2)
    private BigDecimal credito = BigDecimal.ZERO;

    /** ID del tercero (cliente/proveedor) afectado en esta línea, si aplica. */
    @Column(name = "tercero_id")
    private Integer terceroId;

    /** Nombre del tercero al momento de generar el asiento (snapshot). */
    @Column(name = "tercero_nombre", length = 200)
    private String terceroNombre;

    /** Descripción específica de la línea. */
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    /** Documento de cruce (factura/CxC/CxP) cuando la cuenta lo exige. Para
     *  asientos automáticos se autocompleta con el documento origen. */
    @Column(name = "documento_cruce", length = 100)
    private String documentoCruce;

    /** Centro de costo (opcional) asociado a la línea. Referencia al id de
     *  la tabla centrocosto; sin FK dura, igual que terceroId. */
    @Column(name = "centro_costo_id")
    private Integer centroCostoId;

    /** Orden visual de la línea dentro del asiento. */
    @Column(name = "orden", nullable = false)
    private Integer orden = 0;

    /** Conciliación bancaria: true = el movimiento ya apareció en el extracto del banco. */
    @Column(name = "conciliado", nullable = false)
    private Boolean conciliado = false;

    /** Fecha en que se marcó como conciliado (puede ser distinta a la del asiento). */
    @Column(name = "fecha_conciliacion")
    private LocalDate fechaConciliacion;

    /** Referencia del extracto bancario (Nº de transacción, código de operación, etc.). */
    @Column(name = "referencia_extracto", length = 100)
    private String referenciaExtracto;

    /** Usuario que marcó la conciliación, para auditoría. */
    @Column(name = "usuario_concilio", length = 80)
    private String usuarioConcilio;
}
