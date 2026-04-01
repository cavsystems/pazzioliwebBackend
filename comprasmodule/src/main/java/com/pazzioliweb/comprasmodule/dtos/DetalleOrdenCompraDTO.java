package com.pazzioliweb.comprasmodule.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleOrdenCompraDTO {
    private Long id;
    private String codigoProducto;
    private String codigoBarras;
    private String descripcionProducto;
    private String referenciaVariantes;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal ivaPorcentaje;
    private BigDecimal subtotal;
    private BigDecimal total;
    private boolean recibido;
    private Integer cantidadRecibida;
    private String manifiesto;
}
