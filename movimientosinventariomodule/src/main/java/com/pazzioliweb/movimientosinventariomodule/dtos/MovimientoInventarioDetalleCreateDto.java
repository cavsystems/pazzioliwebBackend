package com.pazzioliweb.movimientosinventariomodule.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class MovimientoInventarioDetalleCreateDto {
	@NotNull
    private Long productoVarianteId;

    // Bodegas.codigo es int -> usamos Integer aquí
    private Integer bodegaOrigenId;   // null si no aplica (ej. entradas que no tienen origen)
    private Integer bodegaDestinoId;  // null si no aplica (ej. salidas que no tienen destino)

    @NotNull
    @DecimalMin("0.0001")
    private Double cantidad;

    // costos pueden venir desde el documento origen, o calcularse en server:
    private Double costoUnitario;    // optional on create
    private Double costoPromedio;    // optional on create (server can compute)
    private Double totalDetalle;     // optional (server puede calcular cantidad * costoUnitario)
	public Long getProductoVarianteId() {
		return productoVarianteId;
	}
	public void setProductoVarianteId(Long productoVarianteId) {
		this.productoVarianteId = productoVarianteId;
	}
	public Integer getBodegaOrigenId() {
		return bodegaOrigenId;
	}
	public void setBodegaOrigenId(Integer bodegaOrigenId) {
		this.bodegaOrigenId = bodegaOrigenId;
	}
	public Integer getBodegaDestinoId() {
		return bodegaDestinoId;
	}
	public void setBodegaDestinoId(Integer bodegaDestinoId) {
		this.bodegaDestinoId = bodegaDestinoId;
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
