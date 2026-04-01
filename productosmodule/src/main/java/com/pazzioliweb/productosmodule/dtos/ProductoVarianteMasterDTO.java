package com.pazzioliweb.productosmodule.dtos;

import java.util.List;

public class ProductoVarianteMasterDTO {
	private Long productoVarianteId;
	private ProductoVarianteCreateDTO variante; // Datos básicos
	private List<ProductoVarianteDetalleCreateDTO> detalles;
	private List<PreciosProductoVarianteCreateDTO> precios;
	private List<ExistenciasCreateDTO> existencias;
	
	public Long getProductoVarianteId() {
		return productoVarianteId;
	}
	public void setProductoVarianteId(Long productoVarianteId) {
		this.productoVarianteId = productoVarianteId;
	}
	public ProductoVarianteCreateDTO getVariante() {
		return variante;
	}
	public void setVariante(ProductoVarianteCreateDTO variante) {
		this.variante = variante;
	}
	public List<ProductoVarianteDetalleCreateDTO> getDetalles() {
		return detalles;
	}
	public void setDetalles(List<ProductoVarianteDetalleCreateDTO> detalles) {
		this.detalles = detalles;
	}
	public List<PreciosProductoVarianteCreateDTO> getPrecios() {
		return precios;
	}
	public void setPrecios(List<PreciosProductoVarianteCreateDTO> precios) {
		this.precios = precios;
	}
	public List<ExistenciasCreateDTO> getExistencias() {
		return existencias;
	}
	public void setExistencias(List<ExistenciasCreateDTO> existencias) {
		this.existencias = existencias;
	}
	
}	
