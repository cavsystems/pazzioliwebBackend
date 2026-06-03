package com.pazzioliweb.productosmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaracteristicaDetalleDTO {
    private Long caracteristicaId;
    private String nombre;
    private TipoCaracteristicaDTO tipo;
}
