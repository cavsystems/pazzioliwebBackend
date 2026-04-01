package com.pazzioliweb.productosmodule.dtos;

public class ProductoVarianteDetalleUpdateDTO {
	private Long productoVariantesDetalleId;
	private Long productoVarianteId;
	private Long caracteristicaId;
	
	public Long getProductoVariantesDetalleId() {
		return productoVariantesDetalleId;
	}
	public void setProductoVariantesDetalleId(Long productoVariantesDetalleId) {
		this.productoVariantesDetalleId = productoVariantesDetalleId;
	}
	public Long getProductoVarianteId() {
		return productoVarianteId;
	}
	public void setProductoVarianteId(Long productoVarianteId) {
		this.productoVarianteId = productoVarianteId;
	}
	public Long getCaracteristicaId() {
		return caracteristicaId;
	}
	public void setCaracteristicaId(Long caracteristicaId) {
		this.caracteristicaId = caracteristicaId;
	}
	
	
}
