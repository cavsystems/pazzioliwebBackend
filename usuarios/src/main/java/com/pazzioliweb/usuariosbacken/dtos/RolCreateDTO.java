package com.pazzioliweb.usuariosbacken.dtos;

public class RolCreateDTO {
    private String nombre;

    public RolCreateDTO() {}

    public RolCreateDTO(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
