package com.pazzioliweb.movimientosinventariomodule.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MovimientoInventarioResponseDto {
	private Long movimientoId;
    private Integer comprobanteId;
    private String comprobanteNombre;    // opcional para mostrar
    private Integer consecutivo;

    private String tipo;                 // TipoMovimiento as String
    private Integer usuarioId;
    private String usuarioNombre;        // opcional

    private LocalDate fechaEmision;
    private LocalDateTime fechaCreacion;

    private String estado;
    private Double total;
    private String observaciones;

    private List<MovimientoInventarioDetalleResponseDto> detalles;

	public Long getMovimientoId() {
		return movimientoId;
	}

	public void setMovimientoId(Long movimientoId) {
		this.movimientoId = movimientoId;
	}

	public Integer getComprobanteId() {
		return comprobanteId;
	}

	public void setComprobanteId(Integer comprobanteId) {
		this.comprobanteId = comprobanteId;
	}

	public String getComprobanteNombre() {
		return comprobanteNombre;
	}

	public void setComprobanteNombre(String comprobanteNombre) {
		this.comprobanteNombre = comprobanteNombre;
	}

	public Integer getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(Integer consecutivo) {
		this.consecutivo = consecutivo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Integer usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getUsuarioNombre() {
		return usuarioNombre;
	}

	public void setUsuarioNombre(String usuarioNombre) {
		this.usuarioNombre = usuarioNombre;
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

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
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

	public List<MovimientoInventarioDetalleResponseDto> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<MovimientoInventarioDetalleResponseDto> detalles) {
		this.detalles = detalles;
	}
    
    
}
