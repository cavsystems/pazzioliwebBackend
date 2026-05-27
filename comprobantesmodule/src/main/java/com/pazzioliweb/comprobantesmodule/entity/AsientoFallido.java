package com.pazzioliweb.comprobantesmodule.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Registro de intentos fallidos de generación de asiento contable.
 * Cuando una venta/compra/devolución se persiste correctamente pero el
 * asiento contable falla (cuenta no configurada, periodo cerrado, etc.),
 * el sistema lo registra aquí para que un administrador pueda revisar
 * y resolver manualmente. Sin esta tabla, los asientos huérfanos quedan
 * solo en el log y son difíciles de detectar.
 */
@Entity
@Table(name = "asientos_fallidos")
public class AsientoFallido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Módulo que originó el fallo: VENTAS, COMPRAS, RC, CE, DV, INVENTARIO, etc. */
    @Column(name = "modulo", length = 40, nullable = false)
    private String modulo;

    /** Tipo del documento origen: FC, VC, CC, CR, RC, CE, DV, EI, SI, NC, ND, TPOS, DS. */
    @Column(name = "tipo_documento", length = 20)
    private String tipoDocumento;

    /** ID del documento origen (venta_id, orden_id, etc.). */
    @Column(name = "documento_id")
    private Long documentoId;

    /** Número visible del documento, si está disponible (ej. FC-1234, RC-007). */
    @Column(name = "numero_documento", length = 60)
    private String numeroDocumento;

    /** Mensaje legible del problema. */
    @Column(name = "motivo", length = 500, nullable = false)
    private String motivo;

    /** Stacktrace abreviado (primeras líneas) para diagnóstico técnico. */
    @Column(name = "stacktrace", columnDefinition = "TEXT")
    private String stacktrace;

    /** Fecha del intento. */
    @Column(name = "fecha_intento", nullable = false)
    private LocalDateTime fechaIntento;

    /** Si ya fue resuelto manualmente (con asiento manual creado). */
    @Column(name = "resuelto", nullable = false)
    private Boolean resuelto = false;

    /** Notas opcionales del admin al resolverlo. */
    @Column(name = "notas_resolucion", length = 500)
    private String notasResolucion;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public Long getDocumentoId() { return documentoId; }
    public void setDocumentoId(Long documentoId) { this.documentoId = documentoId; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getStacktrace() { return stacktrace; }
    public void setStacktrace(String stacktrace) { this.stacktrace = stacktrace; }
    public LocalDateTime getFechaIntento() { return fechaIntento; }
    public void setFechaIntento(LocalDateTime fechaIntento) { this.fechaIntento = fechaIntento; }
    public Boolean getResuelto() { return resuelto; }
    public void setResuelto(Boolean resuelto) { this.resuelto = resuelto; }
    public String getNotasResolucion() { return notasResolucion; }
    public void setNotasResolucion(String notasResolucion) { this.notasResolucion = notasResolucion; }
    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
}
