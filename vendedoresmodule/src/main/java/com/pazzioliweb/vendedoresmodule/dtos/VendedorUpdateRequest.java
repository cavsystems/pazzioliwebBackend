package com.pazzioliweb.vendedoresmodule.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VendedorUpdateRequest {
    
    private Integer usuarioid;

    private Integer bodegaId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    
    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
