package com.pazzioliweb.comprasmodule.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConfiguracionComprasDTO {
    private Integer cajeroDefaultId;
    private String cajeroDefaultNombre;
    private LocalDateTime fechaActualizacion;
}
