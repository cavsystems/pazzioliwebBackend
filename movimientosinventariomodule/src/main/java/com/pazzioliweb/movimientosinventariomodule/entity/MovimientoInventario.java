package com.pazzioliweb.movimientosinventariomodule.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pazzioliweb.comprobantesmodule.entity.Comprobantes;
import com.pazzioliweb.movimientosinventariomodule.enums.EstadoMovimiento;
import com.pazzioliweb.movimientosinventariomodule.enums.TipoMovimiento;
import com.pazzioliweb.usuariosbacken.entity.Usuario;

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

@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_inventario_id")
    private Long movimientoId;

    @ManyToOne
    @JoinColumn(name = "comprobante_id", nullable = false)
    private Comprobantes comprobante;

    @Column(nullable = false)
    private Integer consecutivo;

    @Enumerated(EnumType.STRING)
    @Column(name ="tipo_movimiento", nullable = false)
    private TipoMovimiento tipo; // ENTRADA, SALIDA, TRASLADO

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_movimiento", nullable = false)
    private EstadoMovimiento estado;

    private Double total;

    private String observaciones;

	public Long getMovimientoId() {
		return movimientoId;
	}

	public void setMovimientoId(Long movimientoId) {
		this.movimientoId = movimientoId;
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

	public TipoMovimiento getTipo() {
		return tipo;
	}

	public void setTipo(TipoMovimiento tipo) {
		this.tipo = tipo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public LocalDate getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(LocalDate fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public EstadoMovimiento getEstado() {
		return estado;
	}

	public void setEstado(EstadoMovimiento estado) {
		this.estado = estado;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
    
}
