package com.pazzioliweb.cajerosmodule.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pazzioliweb.comprobantesmodule.entity.Comprobantes;

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

@Entity
@Table(name = "detalle_cajero")
@Data
public class DetalleCajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_cajero_id")
    private Long detalleCajeroId;

    @ManyToOne
    @JoinColumn(name = "cajero_id", nullable = false)
    private Cajero cajero;

    @Column(name = "monto_inicial", nullable = false)
    private BigDecimal montoInicial = BigDecimal.ZERO;

    @Column(name = "monto_final", nullable = false)
    private BigDecimal montoFinal = BigDecimal.ZERO;

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoDetalleCajero estado = EstadoDetalleCajero.ABIERTA;

    @ManyToOne
    @JoinColumn(name = "comprobante_id")
    private Comprobantes comprobante;

    @Column(name = "consecutivo", nullable = false)
    private Integer consecutivo = 0;

    @Column(name = "total_recaudo", nullable = false)
    private BigDecimal totalRecaudo = BigDecimal.ZERO;

    @Column(name = "total_costo", nullable = false)
    private BigDecimal totalCosto = BigDecimal.ZERO;

    @Column(name = "base_caja", nullable = false)
    private BigDecimal baseCaja = BigDecimal.ZERO;

    // ========================
    // 3 COMPONENTES DEL CUADRE DE CAJA
    // ========================

    /** Componente 1: Total acumulado recibido en EFECTIVO durante la sesión */
    @Column(name = "total_efectivo", nullable = false)
    private BigDecimal totalEfectivo = BigDecimal.ZERO;

    /** Componente 2: Total acumulado recibido por MEDIOS ELECTRÓNICOS (tarjeta, transferencia, etc.) */
    @Column(name = "total_medios_electronicos", nullable = false)
    private BigDecimal totalMediosElectronicos = BigDecimal.ZERO;

    /** Componente 3: Al cierre - lo que el cajero DECLARA tener en efectivo */
    @Column(name = "efectivo_declarado")
    private BigDecimal efectivoDeclarado;

    /** Al cierre - lo que el cajero DECLARA en medios electrónicos */
    @Column(name = "medios_electronicos_declarado")
    private BigDecimal mediosElectronicosDeclarado;

    /** Diferencia = efectivoDeclarado − (baseCaja + totalEfectivo). Positivo = sobrante, negativo = faltante */
    @Column(name = "diferencia_efectivo")
    private BigDecimal diferenciaEfectivo;

    /** Diferencia medios electrónicos = mediosElectronicosDeclarado − totalMediosElectronicos */
    @Column(name = "diferencia_medios_electronicos")
    private BigDecimal diferenciaMediosElectronicos;

    public enum EstadoDetalleCajero {
        ABIERTA, CERRADA
    }

    public Long getDetalleCajeroId() {
        return detalleCajeroId;
    }

    public void setDetalleCajeroId(Long detalleCajeroId) {
        this.detalleCajeroId = detalleCajeroId;
    }

    public Cajero getCajero() {
        return cajero;
    }

    public void setCajero(Cajero cajero) {
        this.cajero = cajero;
    }

    public BigDecimal getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(BigDecimal montoInicial) {
        this.montoInicial = montoInicial;
    }

    public BigDecimal getMontoFinal() {
        return montoFinal;
    }

    public void setMontoFinal(BigDecimal montoFinal) {
        this.montoFinal = montoFinal;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public EstadoDetalleCajero getEstado() {
        return estado;
    }

    public void setEstado(EstadoDetalleCajero estado) {
        this.estado = estado;
    }

    public Comprobantes getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobantes comprobante) {
        this.comprobante = comprobante;
    }

    public Integer getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(Integer consecutivo) {
        this.consecutivo = consecutivo;
    }

    public BigDecimal getTotalRecaudo() {
        return totalRecaudo;
    }

    public void setTotalRecaudo(BigDecimal totalRecaudo) {
        this.totalRecaudo = totalRecaudo;
    }

    public BigDecimal getTotalCosto() {
        return totalCosto;
    }

    public void setTotalCosto(BigDecimal totalCosto) {
        this.totalCosto = totalCosto;
    }

    public BigDecimal getBaseCaja() {
        return baseCaja;
    }

    public void setBaseCaja(BigDecimal baseCaja) {
        this.baseCaja = baseCaja;
    }

    public BigDecimal getTotalEfectivo() {
        return totalEfectivo;
    }

    public void setTotalEfectivo(BigDecimal totalEfectivo) {
        this.totalEfectivo = totalEfectivo;
    }

    public BigDecimal getTotalMediosElectronicos() {
        return totalMediosElectronicos;
    }

    public void setTotalMediosElectronicos(BigDecimal totalMediosElectronicos) {
        this.totalMediosElectronicos = totalMediosElectronicos;
    }

    public BigDecimal getEfectivoDeclarado() {
        return efectivoDeclarado;
    }

    public void setEfectivoDeclarado(BigDecimal efectivoDeclarado) {
        this.efectivoDeclarado = efectivoDeclarado;
    }

    public BigDecimal getMediosElectronicosDeclarado() {
        return mediosElectronicosDeclarado;
    }

    public void setMediosElectronicosDeclarado(BigDecimal mediosElectronicosDeclarado) {
        this.mediosElectronicosDeclarado = mediosElectronicosDeclarado;
    }

    public BigDecimal getDiferenciaEfectivo() {
        return diferenciaEfectivo;
    }

    public void setDiferenciaEfectivo(BigDecimal diferenciaEfectivo) {
        this.diferenciaEfectivo = diferenciaEfectivo;
    }

    public BigDecimal getDiferenciaMediosElectronicos() {
        return diferenciaMediosElectronicos;
    }

    public void setDiferenciaMediosElectronicos(BigDecimal diferenciaMediosElectronicos) {
        this.diferenciaMediosElectronicos = diferenciaMediosElectronicos;
    }
}
