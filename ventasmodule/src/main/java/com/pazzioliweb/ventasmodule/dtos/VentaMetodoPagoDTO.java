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
    /** Plazo en días para pago a crédito (solo cuando tipoNegociacion = Credito) */
    private Integer plazoEnDias;
}

