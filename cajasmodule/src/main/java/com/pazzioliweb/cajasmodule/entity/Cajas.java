package com.pazzioliweb.cajasmodule.entity;

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
@Table(name = "cajas")
@Data
public class Cajas {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "caja_id")
    private Integer caja_id;

    /*@ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;*/
	@Column(name = "usuario_id", nullable = false)
    private Integer usuario_id;

    @Column(name = "monto_inicial", nullable = false)
    private BigDecimal monto_inicial = BigDecimal.ZERO;

    @Column(name = "monto_final", nullable = false)
    private BigDecimal monto_final = BigDecimal.ZERO;

    @Column(name = "fecha_apertura", nullable = false, updatable = false)
    private LocalDateTime fecha_apertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fecha_cierre;

    @ManyToOne
    @JoinColumn(name = "comprobante_id", nullable = false)
    private Comprobantes comprobante;

    @Column(nullable = false)
    private Integer consecutivo = 0;

    @Column(name = "total_recaudo", nullable = false)
    private BigDecimal total_recaudo = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCaja estado = EstadoCaja.ABIERTA;

    public enum EstadoCaja {
        ABIERTA, CERRADA
    }

	public Integer getCaja_id() {
		return caja_id;
	}

	public void setCaja_id(Integer caja_id) {
		this.caja_id = caja_id;
	}

	public Integer getUsuario_id() {
		return usuario_id;
	}

	public void setUsuario_id(Integer usuario_id) {
		this.usuario_id = usuario_id;
	}

	public BigDecimal getMonto_inicial() {
		return monto_inicial;
	}

	public void setMonto_inicial(BigDecimal monto_inicial) {
		this.monto_inicial = monto_inicial;
	}

	public BigDecimal getMonto_final() {
		return monto_final;
	}

	public void setMonto_final(BigDecimal monto_final) {
		this.monto_final = monto_final;
	}

	public LocalDateTime getFecha_apertura() {
		return fecha_apertura;
	}

	public void setFecha_apertura(LocalDateTime fecha_apertura) {
		this.fecha_apertura = fecha_apertura;
	}

	public LocalDateTime getFecha_cierre() {
		return fecha_cierre;
	}

	public void setFecha_cierre(LocalDateTime fecha_cierre) {
		this.fecha_cierre = fecha_cierre;
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

	public BigDecimal getTotal_recaudo() {
		return total_recaudo;
	}

	public void setTotal_recaudo(BigDecimal total_recaudo) {
		this.total_recaudo = total_recaudo;
	}

	public EstadoCaja getEstado() {
		return estado;
	}

	public void setEstado(EstadoCaja estado) {
		this.estado = estado;
	}
}
