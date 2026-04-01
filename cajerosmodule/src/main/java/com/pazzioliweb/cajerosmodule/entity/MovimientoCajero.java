package com.pazzioliweb.cajerosmodule.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Cada registro = 1 transacción individual del cajero en el POS.
 * Se usa para el cuadre de caja: trazabilidad completa de cada operación.
 * 
 * Los 6 tipos de documento POS:
 * 1. VENTA           → Suma al cuadre (+)
 * 2. COTIZACION      → No afecta caja (informativo)
 * 3. PEDIDO          → No afecta caja (hasta facturar)
 * 4. CUENTA_POR_COBRAR → Crédito otorgado (no ingresa efectivo, afecta cartera)
 * 5. EGRESO          → Resta al cuadre (−) salida de efectivo
 * 6. DEVOLUCION      → Resta al cuadre (−) reembolso al cliente
 * 
 * Tipos auxiliares:
 * - ANULACION        → Reversa una venta (−)
 * - INGRESO_EFECTIVO → Ingreso manual de efectivo (+)
 * - ABONO            → Pago parcial de cuenta por cobrar (+)
 */
@Entity
@Table(name = "movimiento_cajero")
@Data
public class MovimientoCajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_cajero_id")
    private Long movimientoCajeroId;

    /** Sesión/turno al que pertenece este movimiento */
    @ManyToOne
    @JoinColumn(name = "detalle_cajero_id", nullable = false)
    private DetalleCajero detalleCajero;

    /** Cajero que realizó el movimiento */
    @ManyToOne
    @JoinColumn(name = "cajero_id", nullable = false)
    private Cajero cajero;

    /** Tipo de documento POS */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 30)
    private TipoMovimiento tipoMovimiento;

    /** Número de comprobante / número de venta / número de cotización */
    @Column(name = "numero_comprobante", length = 100)
    private String numeroComprobante;

    /** ID del documento origen (venta_id, pedido_id, cotizacion_id, etc.) */
    @Column(name = "referencia_documento_id")
    private Long referenciaDocumentoId;

    /** Consecutivo global dentro de la sesión (orden cronológico) */
    @Column(name = "consecutivo", nullable = false)
    private Integer consecutivo = 0;

    /** Consecutivo independiente por tipo de documento dentro de la sesión */
    @Column(name = "consecutivo_tipo", nullable = false)
    private Integer consecutivoTipo = 0;

    /** Monto total de la transacción */
    @Column(name = "monto", nullable = false)
    private BigDecimal monto = BigDecimal.ZERO;

    /** Costo asociado a la transacción */
    @Column(name = "costo", nullable = false)
    private BigDecimal costo = BigDecimal.ZERO;

    /** Desglose: monto recibido en efectivo */
    @Column(name = "monto_efectivo", nullable = false)
    private BigDecimal montoEfectivo = BigDecimal.ZERO;

    /** Desglose: monto recibido por medios electrónicos (tarjeta, transferencia, etc.) */
    @Column(name = "monto_electronico", nullable = false)
    private BigDecimal montoElectronico = BigDecimal.ZERO;

    /** Descripción o detalle del movimiento */
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    /** Fecha y hora exacta del movimiento */
    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    /**
     * 6 Tipos de documento POS + auxiliares.
     * afectaCaja: true si suma/resta al cuadre de caja.
     * signo: +1 = ingreso, -1 = egreso, 0 = no afecta.
     */
    public enum TipoMovimiento {
        // === 6 documentos principales POS ===
        VENTA(true, 1),
        COTIZACION(false, 0),
        PEDIDO(false, 0),
        CUENTA_POR_COBRAR(false, 0),
        EGRESO(true, -1),
        DEVOLUCION(true, -1),

        // === Auxiliares ===
        ANULACION(true, -1),
        INGRESO_EFECTIVO(true, 1),
        ABONO(true, 1);

        private final boolean afectaCaja;
        private final int signo;

        TipoMovimiento(boolean afectaCaja, int signo) {
            this.afectaCaja = afectaCaja;
            this.signo = signo;
        }

        public boolean isAfectaCaja() {
            return afectaCaja;
        }

        public int getSigno() {
            return signo;
        }
    }

    // Getters y setters explícitos
    public Long getMovimientoCajeroId() { return movimientoCajeroId; }
    public void setMovimientoCajeroId(Long id) { this.movimientoCajeroId = id; }
    public DetalleCajero getDetalleCajero() { return detalleCajero; }
    public void setDetalleCajero(DetalleCajero d) { this.detalleCajero = d; }
    public Cajero getCajero() { return cajero; }
    public void setCajero(Cajero c) { this.cajero = c; }
    public TipoMovimiento getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(TipoMovimiento t) { this.tipoMovimiento = t; }
    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String n) { this.numeroComprobante = n; }
    public Long getReferenciaDocumentoId() { return referenciaDocumentoId; }
    public void setReferenciaDocumentoId(Long r) { this.referenciaDocumentoId = r; }
    public Integer getConsecutivo() { return consecutivo; }
    public void setConsecutivo(Integer c) { this.consecutivo = c; }
    public Integer getConsecutivoTipo() { return consecutivoTipo; }
    public void setConsecutivoTipo(Integer c) { this.consecutivoTipo = c; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal m) { this.monto = m; }
    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal c) { this.costo = c; }
    public BigDecimal getMontoEfectivo() { return montoEfectivo; }
    public void setMontoEfectivo(BigDecimal m) { this.montoEfectivo = m; }
    public BigDecimal getMontoElectronico() { return montoElectronico; }
    public void setMontoElectronico(BigDecimal m) { this.montoElectronico = m; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String d) { this.descripcion = d; }
    public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(LocalDateTime f) { this.fechaMovimiento = f; }
}
