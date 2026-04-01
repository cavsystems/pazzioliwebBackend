package com.pazzioliweb.productosmodule.mapper;

import org.springframework.stereotype.Component;

import com.pazzioliweb.productosmodule.dtos.ProductoVarianteDetalleCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteDetalleResponseDTO;
import com.pazzioliweb.productosmodule.entity.Caracteristica;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.entity.ProductoVarianteDetalle;

@Component
public class ProductoVarianteDetalleMapper {
	// ---------------------------------------------------------
    // Convertir CREATE DTO -> Entity
    // ---------------------------------------------------------
	public ProductoVarianteDetalle fromCreateDto(ProductoVariante variante, Caracteristica caracteristica) {
	    ProductoVarianteDetalle pvd = new ProductoVarianteDetalle();
	    pvd.setProductoVariante(variante);
	    pvd.setCaracteristica(caracteristica);
	    return pvd;
	}
    
 // ---------------------------------------------------------
    // Convertir Entity -> Response DTO
    // ---------------------------------------------------------
    public ProductoVarianteDetalleResponseDTO toResponseDto(ProductoVarianteDetalle pvd) {
        ProductoVarianteDetalleResponseDTO dto = new ProductoVarianteDetalleResponseDTO();

        dto.setProductoVariantesDetalleId(pvd.getProductoVariantesDetalleId());
        dto.setProductoVarianteId(pvd.getProductoVariante().getProductoVarianteId());
        dto.setCaracteristicaId(pvd.getCaracteristica().getCaracteristicaId());

        return dto;
    }
    
    // pendiente el de actualizar
}
