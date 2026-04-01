package com.pazzioliweb.productosmodule.dtos;

import com.pazzioliweb.productosmodule.entity.Grupos;

public class GrupoDTO {
    private Integer grupoId;
    private String nombre;

    public GrupoDTO() {
    }

    public static GrupoDTO fromEntity(Grupos grupo) {
        if (grupo == null) return null;

        GrupoDTO dto = new GrupoDTO();
        dto.grupoId = grupo.getId() != null ? grupo.getId() : 0;
        dto.nombre = grupo.getDescripcion() != null ? grupo.getDescripcion() : "";
        return dto;
    }

    public Integer getGrupoId() { return grupoId; }
    public void setGrupoId(Integer grupoId) { this.grupoId = grupoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
