package com.pazzioliweb.vendedoresmodule.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VendedorCreateRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    
    private Integer usuarioid;
    
    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
