package com.pazzioliweb.productosmodule.dtos;

import java.time.LocalDateTime;

public class PreciosProductoVarianteResponseDTO {
	
	private Integer preciosProductoId;
    private Long productoVarianteId;
    private Integer precioId;
    private double valor;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    private String precioDescripcion;

	public Integer getPreciosProductoId() {
		return preciosProductoId;
	}

	public void setPreciosProductoId(Integer preciosProductoId) {
		this.preciosProductoId = preciosProductoId;
	}

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

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public LocalDateTime getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(LocalDateTime fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public String getPrecioDescripcion() {
		return precioDescripcion;
	}

	public void setPrecioDescripcion(String precioDescripcion) {
		this.precioDescripcion = precioDescripcion;
	}
    
    
}
