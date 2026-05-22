package com.pazzioliweb.facturacionmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Persistencia genérica para documentos electrónicos DIAN distintos a la
 * Factura Electrónica (FC/VC) y Nota Crédito (NC, ya persistida en Devolucion).
 *
 * Cubre:
 *  - TPOS  Tiquete POS Electrónico
 *  - ND    Nota Débito Electrónica
 *  - DS    Documento Soporte de compras a no facturadores
 */
@Data
@Entity
@Table(name = "documentos_electronicos")
public class DocumentoElectronico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** TPOS / ND / DS */
    @Column(nullable = false, length = 10)
    private String tipo;

    /** Ej: TPOS-100, ND-3, DS-15 */
    @Column(nullable = false, length = 50)
    private String numero;

    /** CUFE / CUDE retornado por DIAN o calculado en simulación */
    @Column(length = 200)
    private String cufe;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /** Cliente (TPOS/ND) o Proveedor (DS) */
    @Column(name = "tercero_identificacion", length = 50)
    private String terceroIdentificacion;

    @Column(name = "tercero_nombre", length = 200)
    private String terceroNombre;

    /** ID del documento origen (venta_id para TPOS, factura_id para ND) */
    @Column(name = "documento_referencia_id")
    private Long documentoReferenciaId;

    /** Tipo: VENTA, FACTURA, etc. */
    @Column(name = "documento_referencia_tipo", length = 20)
    private String documentoReferenciaTipo;

    @Column(name = "base_gravable", precision = 18, scale = 2)
    private BigDecimal baseGravable = BigDecimal.ZERO;

    @Column(precision = 18, scale = 2)
    private BigDecimal iva = BigDecimal.ZERO;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(length = 500)
    private String concepto;

    /** Código DIAN según el tipo de documento (e.g. ND: 1=Intereses, etc) */
    @Column(name = "codigo_concepto_dian")
    private Integer codigoConceptoDian;

    /** AUTORIZADA / RECHAZADA / SIMULADA / PENDIENTE */
    @Column(name = "estado_dian", length = 20)
    private String estadoDian;

    @Column(name = "mensaje_dian", length = 500)
    private String mensajeDian;

    @Column(name = "qr_data", columnDefinition = "TEXT")
    private String qrData;

    @Column(name = "xml_firmado", columnDefinition = "LONGTEXT")
    private String xmlFirmado;

    @Column(name = "fecha_validacion_dian")
    private LocalDateTime fechaValidacionDian;

    /** Asiento contable generado al persistir este documento (null si no aplica) */
    @Column(name = "asiento_id")
    private Long asientoId;

    @Column(length = 20)
    private String estado = "ACTIVO";

    // ── Campos específicos de Documento Soporte (DS) ──
    /** PRODUCTOS (mueve inventario) / SERVICIOS (no mueve inventario). Solo aplica a DS. */
    @Column(name = "tipo_compra", length = 20)
    private String tipoCompra;

    /** Autoretención en la fuente practicada por la empresa */
    @Column(name = "retencion_fuente", precision = 18, scale = 2)
    private BigDecimal retencionFuente = BigDecimal.ZERO;

    /** Autoretención de IVA */
    @Column(name = "retencion_iva", precision = 18, scale = 2)
    private BigDecimal retencionIva = BigDecimal.ZERO;

    /** Autoretención de ICA */
    @Column(name = "retencion_ica", precision = 18, scale = 2)
    private BigDecimal retencionIca = BigDecimal.ZERO;

    /** Total de retenciones (suma de las tres) */
    @Column(name = "total_retenciones", precision = 18, scale = 2)
    private BigDecimal totalRetenciones = BigDecimal.ZERO;

    /** Neto a pagar al proveedor = total - totalRetenciones */
    @Column(name = "total_pagar", precision = 18, scale = 2)
    private BigDecimal totalPagar = BigDecimal.ZERO;
}
