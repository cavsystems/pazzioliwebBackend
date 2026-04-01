package com.pazzioliweb.movimientosinventariomodule.dtos;

public class MovimientoInventarioDetalleResponseDto {
	private Long detalleId;
	private Long productoVarianteId;
	private String productoSku;          // opcional, para facilitar UI
	private Integer bodegaOrigenId;
	private String bodegaOrigenNombre;   // opcional
	private Integer bodegaDestinoId;
	private String bodegaDestinoNombre;  // opcional
	private Double cantidad;
	private Double costoUnitario;
	private Double costoPromedio;
	private Double totalDetalle;
	public Long getDetalleId() {
		return detalleId;
	}
	public void setDetalleId(Long detalleId) {
		this.detalleId = detalleId;
	}
	public Long getProductoVarianteId() {
		return productoVarianteId;
	}
	public void setProductoVarianteId(Long productoVarianteId) {
		this.productoVarianteId = productoVarianteId;
	}
	public String getProductoSku() {
		return productoSku;
	}
	public void setProductoSku(String productoSku) {
		this.productoSku = productoSku;
	}
	public Integer getBodegaOrigenId() {
		return bodegaOrigenId;
	}
	public void setBodegaOrigenId(Integer bodegaOrigenId) {
		this.bodegaOrigenId = bodegaOrigenId;
	}
	public String getBodegaOrigenNombre() {
		return bodegaOrigenNombre;
	}
	public void setBodegaOrigenNombre(String bodegaOrigenNombre) {
		this.bodegaOrigenNombre = bodegaOrigenNombre;
	}
	public Integer getBodegaDestinoId() {
		return bodegaDestinoId;
	}
	public void setBodegaDestinoId(Integer bodegaDestinoId) {
		this.bodegaDestinoId = bodegaDestinoId;
	}
	public String getBodegaDestinoNombre() {
		return bodegaDestinoNombre;
	}
	public void setBodegaDestinoNombre(String bodegaDestinoNombre) {
		this.bodegaDestinoNombre = bodegaDestinoNombre;
	}
	public Double getCantidad() {
		return cantidad;
	}
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}
	public Double getCostoUnitario() {
		return costoUnitario;
	}
	public void setCostoUnitario(Double costoUnitario) {
		this.costoUnitario = costoUnitario;
	}
	public Double getCostoPromedio() {
		return costoPromedio;
	}
	public void setCostoPromedio(Double costoPromedio) {
		this.costoPromedio = costoPromedio;
	}
	public Double getTotalDetalle() {
		return totalDetalle;
	}
	public void setTotalDetalle(Double totalDetalle) {
		this.totalDetalle = totalDetalle;
	}
	
	
}
