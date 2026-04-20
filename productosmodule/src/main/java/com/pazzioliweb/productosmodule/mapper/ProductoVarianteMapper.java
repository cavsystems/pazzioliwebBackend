package com.pazzioliweb.productosmodule.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pazzioliweb.productosmodule.dtos.ProductoVarianteConDetallesDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteConDetallesDTO.DetalleDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteUpdateDTO;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.entity.Productos;

@Component
public class ProductoVarianteMapper {

	// ---------------------------------------------------------
    // Convertir CREATE DTO -> Entity
    // ---------------------------------------------------------
    public ProductoVariante fromCreateDto(ProductoVarianteCreateDTO dto, Productos producto) {
        ProductoVariante pv = new ProductoVariante();
        pv.setProducto(producto);
        pv.setSku(dto.getSku());
        pv.setReferenciaVariantes(dto.getReferenciaVariantes());
        pv.setCodigoBarras(dto.getCodigoBarras());
        pv.setActivo(dto.getEstadovariante());
        pv.setPredeterminada(dto.getPredeterminada());
        pv.setImagen(dto.getImagen());
        return pv;
    }

    // ---------------------------------------------------------
    // Convertir Entity -> Response DTO
    // ---------------------------------------------------------
    public ProductoVarianteResponseDTO toResponseDto(ProductoVariante pv) {
        ProductoVarianteResponseDTO dto = new ProductoVarianteResponseDTO();
        
        dto.setProductoVarianteId(pv.getProductoVarianteId());
        dto.setProductoId(pv.getProducto().getProductoId());
        dto.setSku(pv.getSku());
        dto.setReferenciaVariantes(pv.getReferenciaVariantes());
        dto.setCodigoBarras(pv.getCodigoBarras());
        dto.setActivo(pv.getActivo());
        dto.setPredeterminada(pv.getPredeterminada());
        dto.setImagen(pv.getImagen());
        return dto;
    }

    // ---------------------------------------------------------
    // Partial Update (NO pisa con null)
    // ---------------------------------------------------------
    public void updateFromDto(ProductoVarianteUpdateDTO dto, ProductoVariante pv) {
    	if(!dto.getEstadovariante()) {
    		  if (dto.getEstadovariante() != null)
  	            pv.setActivo(dto.getEstadovariante());
    	}else {
    		   if (dto.getSku() != null)
    	            pv.setSku(dto.getSku());

    	        if (dto.getReferenciaVariantes() != null)
    	            pv.setReferenciaVariantes(dto.getReferenciaVariantes());

    	        if (dto.getCodigoBarras() != null)
    	            pv.setCodigoBarras(dto.getCodigoBarras());

    	        if (dto.getEstadovariante() != null)
    	            pv.setActivo(dto.getEstadovariante());
    	        
    	        if (dto.getPredeterminada() != null)
    	        	pv.setPredeterminada(dto.getPredeterminada());
    	        
    	        if (dto.getEstadovariante() != null)
      	            pv.setActivo(dto.getEstadovariante());

    	}

    	// Imagen se actualiza siempre, independiente del estado
    	if (dto.getImagen() != null)
    	    pv.setImagen(dto.getImagen());
     
    }
    
    public ProductoVarianteConDetallesDTO toDtoConDetalles(ProductoVariante pv) {
        ProductoVarianteConDetallesDTO dto = new ProductoVarianteConDetallesDTO();

        dto.setProductoVarianteId(pv.getProductoVarianteId());
        dto.setSku(pv.getSku());
        dto.setReferenciaVariantes(pv.getReferenciaVariantes());
        dto.setCodigoBarras(pv.getCodigoBarras());
        dto.setActivo(pv.getActivo());
        dto.setImagen(pv.getImagen());

        List<DetalleDTO> detalles = pv.getDetalles().stream()
            .map(d -> {
                DetalleDTO dd = new DetalleDTO();
                dd.setDetalleId(d.getProductoVariantesDetalleId());
                dd.setCaracteristicaId(d.getCaracteristica().getCaracteristicaId());
                dd.setCaracteristicaNombre(d.getCaracteristica().getNombre());
                return dd;
            }).toList();

        dto.setDetalles(detalles);

        return dto;
    }
}
