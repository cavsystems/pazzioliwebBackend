package com.pazzioliweb.productosmodule.dtos;

import java.util.List;

public class ProductoMasterUpdateDTO {
	private ProductoUpdateDTO producto;

    private List<ProductoVarianteMasterUpdateDTO> variantes;

	public ProductoUpdateDTO getProducto() {
		return producto;
	}

	public void setProducto(ProductoUpdateDTO producto) {
		this.producto = producto;
	}

	public List<ProductoVarianteMasterUpdateDTO> getVariantes() {
		return variantes;
	}

	public void setVariantes(List<ProductoVarianteMasterUpdateDTO> variantes) {
		this.variantes = variantes;
	}
    
    
}
