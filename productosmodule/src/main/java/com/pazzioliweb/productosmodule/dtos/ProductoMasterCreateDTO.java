package com.pazzioliweb.productosmodule.dtos;

import java.util.List;

public class ProductoMasterCreateDTO {
	private ProductoCreateDTO producto; // Datos del producto
    private List<ProductoVarianteMasterDTO> variantes;
    private List<Integer> unidadesMedida;
    
	public ProductoCreateDTO getProducto() {
		return producto;
	}
	public void setProducto(ProductoCreateDTO producto) {
		this.producto = producto;
	}
	public List<ProductoVarianteMasterDTO> getVariantes() {
		return variantes;
	}
	public void setVariantes(List<ProductoVarianteMasterDTO> variantes) {
		this.variantes = variantes;
	}
	public List<Integer> getUnidadesMedida() {
		return unidadesMedida;
	}
	public void setUnidadesMedida(List<Integer> unidadesMedida) {
		this.unidadesMedida = unidadesMedida;
	}
    
}
