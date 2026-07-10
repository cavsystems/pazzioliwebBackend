package com.pazzioliweb.comprobantesmodule.dtos;

import lombok.Data;

@Data
public class CuentaContableDTO {
    private Integer id;
    private String codigo;
    private String nombre;
    private String tipo;          // 9 clases PUC (ACTIVO..ORDEN_ACREEDORAS)
    private String naturaleza;    // derivado: DEBITO/CREDITO
    private Integer nivel;
    private Integer padreId;
    private Boolean esMovimiento;
    private Boolean requiereTercero;
    private Boolean requiereDocumentoCruce;
    private String estado;
}
