package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Cuánto se ha movido por cada concepto abierto en un periodo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConceptoAbiertoUsoDTO {
    private Long conceptoId;
    private String descripcion;
    private String tipo;            // RECIBO / EGRESO / AMBOS
    private String cuentaContableCodigo;
    private String cuentaContableNombre;
    private Long cantidadUsos;
    private BigDecimal totalMovido;
}
