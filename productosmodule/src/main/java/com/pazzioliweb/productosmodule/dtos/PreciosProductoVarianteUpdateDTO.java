package com.pazzioliweb.productosmodule.dtos;

import java.time.LocalDateTime;

public class PreciosProductoVarianteUpdateDTO {
	private Long preciosProductoId;
	private Integer precioId;
    private Double valor;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    
	public Long getPreciosProductoId() {
		return preciosProductoId;
	}
	public void setPreciosProductoId(Long preciosProductoId) {
		this.preciosProductoId = preciosProductoId;
	}
	public Integer getPrecioId() {
		return precioId;
	}
	public void setPrecioId(Integer precioId) {
		this.precioId = precioId;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(LocalDateTime fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public LocalDateTime getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(LocalDateTime fechaFin) {
		this.fechaFin = fechaFin;
	}
    
    
}
