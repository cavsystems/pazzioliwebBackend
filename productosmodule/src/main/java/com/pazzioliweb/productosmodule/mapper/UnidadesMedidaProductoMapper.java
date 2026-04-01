package com.pazzioliweb.productosmodule.mapper;

import org.springframework.stereotype.Component;

import com.pazzioliweb.productosmodule.dtos.UnidadMedidaProductoCreateDTO;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaProductoResponseDTO;
import com.pazzioliweb.productosmodule.entity.Productos;
import com.pazzioliweb.productosmodule.entity.UnidadesMedida;
import com.pazzioliweb.productosmodule.entity.UnidadesMedidaProducto;
import com.pazzioliweb.productosmodule.entity.UnidadesMedidaProductoId;

@Component
public class UnidadesMedidaProductoMapper {

    public UnidadesMedidaProducto toEntity(UnidadMedidaProductoCreateDTO dto, 
                                           Productos producto,
                                           UnidadesMedida unidad) {

        UnidadesMedidaProducto entity = new UnidadesMedidaProducto();

        UnidadesMedidaProductoId id = new UnidadesMedidaProductoId(
                dto.getProductoId(),
                dto.getUnidadMedidaId()
        );

        entity.setId(id);
        entity.setProducto(producto);
        entity.setUnidadMedida(unidad);

        return entity;
    }

    public UnidadMedidaProductoResponseDTO toResponse(UnidadesMedidaProducto entity) {
        UnidadMedidaProductoResponseDTO dto = new UnidadMedidaProductoResponseDTO();

        dto.setProductoId(entity.getProducto().getProductoId());
        dto.setUnidadMedidaId(entity.getUnidadMedida().getUnidadMedidaId());

        dto.setNombreProducto(entity.getProducto().getDescripcion());
        dto.setNombreUnidadMedida(entity.getUnidadMedida().getDescripcion());
        dto.setSiglaUnidadMedida(entity.getUnidadMedida().getSigla());

        return dto;
    }
}
