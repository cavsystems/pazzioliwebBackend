package com.pazzioliweb.productosmodule.mapper;

import org.springframework.stereotype.Component;

import com.pazzioliweb.productosmodule.dtos.ExistenciasCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasUpdateDTO;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.entity.Existencias;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;

@Component
public class ExistenciasMapper {
	
	public Existencias toEntity(ExistenciasCreateDTO dto, ProductoVariante variante, Bodegas bodega) {
        Existencias e = new Existencias();
        e.setProductoVariante(variante);
        e.setBodega(bodega);
        e.setExistencia(dto.getExistencia());
        e.setStockMin(dto.getStockMin());
        e.setStockMax(dto.getStockMax());
        e.setUbicacion(dto.getUbicacion());
        return e;
    }

    public void applyUpdate(Existencias existente, ExistenciasUpdateDTO dto) {
        existente.setExistencia(dto.getExistencia());
        existente.setStockMin(dto.getStockMin());
        existente.setStockMax(dto.getStockMax());
        existente.setUbicacion(dto.getUbicacion());
    }

    public ExistenciasResponseDTO toResponseDto(Existencias e) {
        ExistenciasResponseDTO dto = new ExistenciasResponseDTO();
        dto.setExistenciaId(e.getExistenciaId());
        dto.setProductoVarianteId(e.getProductoVariante().getProductoVarianteId());
        dto.setBodegaId(e.getBodega().getCodigo());
        dto.setExistencia(e.getExistencia());
        dto.setStockMin(e.getStockMin());
        dto.setStockMax(e.getStockMax());
        dto.setUbicacion(e.getUbicacion());
        dto.setFechaUltimoMovimiento(e.getFechaUltimoMovimiento());
        return dto;
    }
}
