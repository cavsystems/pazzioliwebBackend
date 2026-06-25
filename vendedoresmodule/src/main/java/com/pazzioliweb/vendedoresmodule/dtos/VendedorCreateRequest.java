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

    private String identificacion;

    private String correo;

    private Double comision = 0.0;

    private Double meta_ventas = 0.0;

    private String tipo_vendedor = "INTERNO";

    private Integer usuarioid;

    private Integer bodegaId;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
