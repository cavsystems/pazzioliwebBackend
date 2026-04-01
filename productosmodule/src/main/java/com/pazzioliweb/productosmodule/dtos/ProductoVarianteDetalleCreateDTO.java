package com.pazzioliweb.productosmodule.dtos;

import java.util.List;

public class ProductoVarianteDetalleCreateDTO {
	private Long productoVarianteId;

    // Puede ser uno o varios IDs
    private List<Long> caracteristicaId;

    public Long getProductoVarianteId() {
        return productoVarianteId;
    }

    public void setProductoVarianteId(Long productoVarianteId) {
        this.productoVarianteId = productoVarianteId;
    }

    public List<Long> getCaracteristicaId() {
        return caracteristicaId;
    }

    public void setCaracteristicaIds(List<Long> caracteristicaId) {
        this.caracteristicaId = caracteristicaId;
    }
	
}
