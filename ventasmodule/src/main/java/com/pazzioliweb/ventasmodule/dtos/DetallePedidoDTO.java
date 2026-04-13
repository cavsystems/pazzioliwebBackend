package com.pazzioliweb.ventasmodule.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetallePedidoDTO {
    private Long id;
    private String codigoProducto;
    private String codigoBarras;
    private String descripcionProducto;
    private String observacionProducto;
    private String referenciaVariantes;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal ivaPorcentaje;
    private BigDecimal subtotal;
    private BigDecimal total;
}

