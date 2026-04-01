package com.pazzioliweb.productosmodule.dtos;

import java.time.LocalDateTime;

public class PreciosProductoVarianteCreateDTO {
	private Long productoVarianteId;
    private Integer precioId;
    private double valor;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
	public Long getProductoVarianteId() {
		return productoVarianteId;
	}
	public void setProductoVarianteId(Long productoVarianteId) {
		this.productoVarianteId = productoVarianteId;
	}
	public Integer getPrecioId() {
		return precioId;
	}
	public void setPrecioId(Integer precioId) {
		this.precioId = precioId;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
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
