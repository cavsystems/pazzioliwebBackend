package com.pazzioliweb.comprobantesmodule.dtos;

import lombok.Data;

@Data
public class TipoComprobanteManualDTO {
    private Integer id;
    private String codigo;
    private String nombre;
    private String prefijo;
    private Integer siguienteConsecutivo;
    private String estado;
}
