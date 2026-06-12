package com.pazzioliweb.productosmodule.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteCreateDTO;
import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteResponseDTO;
import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteUpdateDTO;
import com.pazzioliweb.productosmodule.entity.Precios;
import com.pazzioliweb.productosmodule.entity.PreciosProductoVariante;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;

@Component
public class PreciosProductoVarianteMapper {

    public PreciosProductoVariante toEntity(
            PreciosProductoVarianteCreateDTO dto,
            ProductoVariante productoVariante,
            Precios precio
    ) {
        System.out.println("Mapper toEntity - dto.getPredeterminada(): " + dto.getPredeterminada());
        PreciosProductoVariante entity = new PreciosProductoVariante();
        entity.setProductoVariante(productoVariante);
        entity.setPrecio(precio);
        entity.setValor(dto.getValor());
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaFin(dto.getFechaFin());
        entity.setPredeterminada(dto.getPredeterminada() != null ? dto.getPredeterminada() : false);
        System.out.println("Mapper toEntity - entity.getPredeterminada(): " + entity.getPredeterminada());
        entity.setFechaCreacion(LocalDateTime.now());
        return entity;
    }

    public void updateEntity(
            PreciosProductoVariante entity, 
            PreciosProductoVarianteUpdateDTO dto,
            Precios precio
    ) {
        entity.setPrecio(precio);
        entity.setValor(dto.getValor());
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaFin(dto.getFechaFin());
        if (dto.getPredeterminada() != null) entity.setPredeterminada(dto.getPredeterminada());
        entity.setFechaModificacion(LocalDateTime.now());
    }

    public PreciosProductoVarianteResponseDTO toResponse(PreciosProductoVariante entity) {
        PreciosProductoVarianteResponseDTO dto = new PreciosProductoVarianteResponseDTO();
        dto.setPreciosProductoId(entity.getPreciosProductoId());
        dto.setProductoVarianteId(entity.getProductoVariante().getProductoVarianteId());
        dto.setPrecioId(entity.getPrecio().getPrecioId());
        dto.setValor(entity.getValor());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setFechaModificacion(entity.getFechaModificacion());
        dto.setPrecioDescripcion(entity.getPrecio().getDescripcion());
        dto.setPredeterminada(entity.getPredeterminada());
        return dto;
    }
}
