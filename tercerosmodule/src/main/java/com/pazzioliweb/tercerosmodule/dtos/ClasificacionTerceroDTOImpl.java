package com.pazzioliweb.tercerosmodule.dtos;

import com.pazzioliweb.tercerosmodule.entity.ClasificacionTercero;

public class ClasificacionTerceroDTOImpl implements ClasificacionTerceroDTO {
    private Integer clasificacionTerceroId;
    private String nombre;

    // --- Conversión desde entidad ---
    public static ClasificacionTerceroDTOImpl fromEntity(ClasificacionTercero entity) {
        ClasificacionTerceroDTOImpl dto = new ClasificacionTerceroDTOImpl();
        dto.clasificacionTerceroId = entity.getClasificacionTerceroId();
        dto.nombre = entity.getNombre();
        return dto;
    }

    // --- Conversión hacia entidad ---
    public ClasificacionTercero toEntity() {
        ClasificacionTercero entity = new ClasificacionTercero();
        entity.setClasificacionTerceroId(this.clasificacionTerceroId);
        entity.setNombre(this.nombre);
        return entity;
    }

    @Override public Integer getClasificacionTerceroId() { return clasificacionTerceroId; }
    @Override public String getNombre() { return nombre; }

    public void setClasificacionTerceroId(Integer clasificacionTerceroId) { this.clasificacionTerceroId = clasificacionTerceroId; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}