package com.pazzioliweb.comprasmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasPorEstadoDTO {
    private String estado;
    private Long cantidad;
}
