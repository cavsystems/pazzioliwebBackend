package com.pazzioliweb.productosmodule.mapper;

import org.springframework.stereotype.Component;

import com.pazzioliweb.productosmodule.dtos.PrecioCreateDTO;
import com.pazzioliweb.productosmodule.dtos.PrecioResponseDTO;
import com.pazzioliweb.productosmodule.dtos.PrecioUpdateDTO;
import com.pazzioliweb.productosmodule.entity.Precios;

@Component
public class PrecioMapper {

    public Precios toEntity(PrecioCreateDTO dto) {
        Precios entity = new Precios();
        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }

    public void updateEntity(Precios entity, PrecioUpdateDTO dto) {
        entity.setDescripcion(dto.getDescripcion());
    }

    public PrecioResponseDTO toResponseDto(Precios entity) {
        PrecioResponseDTO dto = new PrecioResponseDTO();
        dto.setPrecioId(entity.getPrecioId());
        dto.setDescripcion(entity.getDescripcion());
        return dto;
    }
}
