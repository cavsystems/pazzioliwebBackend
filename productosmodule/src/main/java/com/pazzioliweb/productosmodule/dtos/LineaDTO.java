package com.pazzioliweb.productosmodule.dtos;

import com.pazzioliweb.productosmodule.entity.Lineas;


public class LineaDTO {
    private Integer id;
    private String descripcion;

    public LineaDTO() {
    }

    public static LineaDTO fromEntity(Lineas linea) {
        if (linea == null) return null;

        LineaDTO dto = new LineaDTO();
        dto.id = linea.getId() != null ? linea.getId() : 0;
        dto.descripcion = linea.getDescripcion() != null ? linea.getDescripcion() : "";
        return dto;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}