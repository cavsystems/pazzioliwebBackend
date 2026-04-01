package com.pazzioliweb.productosmodule.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pazzioliweb.productosmodule.dtos.UnidadMedidaCreateDTO;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaResponseDTO;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaUpdateDTO;
import com.pazzioliweb.productosmodule.entity.UnidadesMedida;

@Component
public class UnidadesMedidaMapper {

    public UnidadesMedida toEntity(UnidadMedidaCreateDTO dto) {
        UnidadesMedida u = new UnidadesMedida();
        u.setDescripcion(dto.getDescripcion());
        u.setSigla(dto.getSigla());
        return u;
    }

    public void toEntity(UnidadMedidaUpdateDTO dto, UnidadesMedida u) {
        u.setDescripcion(dto.getDescripcion());
        u.setSigla(dto.getSigla());
    }

    public UnidadMedidaResponseDTO toResponse(UnidadesMedida u) {
        UnidadMedidaResponseDTO dto = new UnidadMedidaResponseDTO();
        dto.setUnidadMedidaId(u.getUnidadMedidaId());
        dto.setDescripcion(u.getDescripcion());
        dto.setSigla(u.getSigla());
        return dto;
    }

    /*public List<UnidadMedidaResponseDTO> toResponseList(List<UnidadesMedida> entities) {
        return entities.stream()
                .map(UnidadesMedidaMapper::toResponse)
                .toList();
    }*/
}
