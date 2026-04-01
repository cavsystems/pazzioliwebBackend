package com.pazzioliweb.comprasmodule.dtos;

import lombok.Data;

@Data
public class ProveedorDTO {
    private Long id;
    private String nit;
    private String razonSocial;
    private String direccion;
    private String telefono;
    private String email;
    private String contacto;
    private boolean activo;
}
