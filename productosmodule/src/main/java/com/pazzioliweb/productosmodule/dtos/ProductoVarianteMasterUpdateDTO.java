package com.pazzioliweb.productosmodule.dtos;

import java.util.List;

public class ProductoVarianteMasterUpdateDTO {
	private Long productoVarianteId;

    // SOLO PARA ACTUALIZAR VARIANTE EXISTENTE
    private ProductoVarianteUpdateDTO variante;

    // Si la variante es nueva -> enviar este
    private ProductoVarianteCreateDTO varianteCreate;

    // DETALLES
    private List<ProductoVarianteDetalleCreateDTO> detalles;
    private List<ProductoVarianteDetalleUpdateDTO> detallesUpdate;

    // PRECIOS
    private List<PreciosProductoVarianteCreateDTO> precios;
    private List<PreciosProductoVarianteUpdateDTO> preciosUpdate;

    // EXISTENCIAS (solo crear por ahora)
    private List<ExistenciasUpdateDTO> existencias;

	public Long getProductoVarianteId() {
		return productoVarianteId;
	}

	public void setProductoVarianteId(Long productoVarianteId) {
		this.productoVarianteId = productoVarianteId;
	}

	public ProductoVarianteUpdateDTO getVariante() {
		return variante;
	}

	public void setVariante(ProductoVarianteUpdateDTO variante) {
		this.variante = variante;
	}

	public ProductoVarianteCreateDTO getVarianteCreate() {
		return varianteCreate;
	}

	public void setVarianteCreate(ProductoVarianteCreateDTO varianteCreate) {
		this.varianteCreate = varianteCreate;
	}

	public List<ProductoVarianteDetalleCreateDTO> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<ProductoVarianteDetalleCreateDTO> detalles) {
		this.detalles = detalles;
	}

	public List<ProductoVarianteDetalleUpdateDTO> getDetallesUpdate() {
		return detallesUpdate;
	}

	public void setDetallesUpdate(List<ProductoVarianteDetalleUpdateDTO> detallesUpdate) {
		this.detallesUpdate = detallesUpdate;
	}

	public List<PreciosProductoVarianteCreateDTO> getPrecios() {
		return precios;
	}

	public void setPrecios(List<PreciosProductoVarianteCreateDTO> precios) {
		this.precios = precios;
	}

	public List<PreciosProductoVarianteUpdateDTO> getPreciosUpdate() {
		return preciosUpdate;
	}

	public void setPreciosUpdate(List<PreciosProductoVarianteUpdateDTO> preciosUpdate) {
		this.preciosUpdate = preciosUpdate;
	}

	public List<ExistenciasUpdateDTO> getExistencias() {
		return existencias;
	}

	public void setExistencias(List<ExistenciasUpdateDTO> existencias) {
		this.existencias = existencias;
	}
    
    
    
    
}
