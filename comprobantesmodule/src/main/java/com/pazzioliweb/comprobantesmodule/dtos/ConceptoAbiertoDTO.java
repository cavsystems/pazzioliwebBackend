package com.pazzioliweb.comprobantesmodule.dtos;

import lombok.Data;

@Data
public class ConceptoAbiertoDTO {
    private Long id;
    private String descripcion;
    private String tipo;                 // RECIBO / EGRESO / AMBOS
    private Integer cuentaContableId;
    private String cuentaContableCodigo;
    private String cuentaContableNombre;
    private Integer terceroId;           // opcional
    private String terceroNombre;        // proyectado en lectura
    private String terceroIdentificacion;
    private String beneficiarioNombre;
    private String beneficiarioDocumento;
    private String infoExtra;
    private String estado;
}
