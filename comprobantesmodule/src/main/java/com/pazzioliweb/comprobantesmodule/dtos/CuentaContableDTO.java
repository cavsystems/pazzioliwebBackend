package com.pazzioliweb.comprobantesmodule.dtos;

import lombok.Data;

@Data
public class CuentaContableDTO {
    private Integer id;
    private String codigo;
    private String nombre;
    private String tipo;          // ACTIVO/PASIVO/PATRIMONIO/INGRESO/GASTO
    private String naturaleza;    // derivado: DEBITO/CREDITO
    private Integer nivel;
    private Integer padreId;
    private Boolean esMovimiento;
    private String estado;
}
