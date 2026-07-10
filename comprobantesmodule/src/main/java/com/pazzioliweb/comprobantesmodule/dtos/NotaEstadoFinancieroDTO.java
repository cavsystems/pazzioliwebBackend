package com.pazzioliweb.comprobantesmodule.dtos;

import lombok.Data;

@Data
public class NotaEstadoFinancieroDTO {
    private Integer id;
    private Integer anio;
    private Integer numero;
    private String titulo;
    private String contenido;
    private String estado;
}
