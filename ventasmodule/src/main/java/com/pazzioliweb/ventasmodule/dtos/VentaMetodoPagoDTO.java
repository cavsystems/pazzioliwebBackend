package com.pazzioliweb.ventasmodule.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VentaMetodoPagoDTO {
    private Long id;
    private Long metodoPagoId;
    private String metodoPagoNombre;
    private BigDecimal monto;
    private String referencia;
}

