package com.pazzioliweb.tercerosmodule.dtos;

import com.pazzioliweb.productosmodule.dtos.PrecioDTO;
import com.pazzioliweb.productosmodule.entity.Precios;

public class PrecioDTOImpl implements PrecioDTO {
    private Integer precio_id;
    private String descripcion;

    public static PrecioDTOImpl fromEntity(Precios entity) {
        PrecioDTOImpl dto = new PrecioDTOImpl();
        dto.precio_id = entity.getPrecioId();
        dto.descripcion = entity.getDescripcion();
        return dto;
    }
    
    public Precios toEntity() {
    	Precios entity = new Precios();
        entity.setPrecioId(this.precio_id);
        entity.setDescripcion(this.descripcion);
        return entity;
    }
    
    @Override public Integer getPrecio_id() { return precio_id; }
    @Override public String getDescripcion() { return descripcion; }
}