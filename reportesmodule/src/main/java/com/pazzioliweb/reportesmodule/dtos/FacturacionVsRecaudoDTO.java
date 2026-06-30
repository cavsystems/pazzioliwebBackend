package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
public class FacturacionVsRecaudoDTO {
    private String periodo;
    private BigDecimal totalFacturado;
    private BigDecimal totalRecaudado;
    private BigDecimal diferencia;
}
