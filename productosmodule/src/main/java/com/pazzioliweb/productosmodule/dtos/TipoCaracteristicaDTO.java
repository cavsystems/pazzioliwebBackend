package com.pazzioliweb.productosmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoCaracteristicaDTO {
    private Long tipoCaracteristicaId;
    private String nombre;
}
