package com.pazzioliweb.ventasmodule.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleDevolucionDTO {
    private Long id;
    private Long itemVentaId;
    private String codigoProducto;
    private String descripcionProducto;
    private Integer cantidadDevuelta;
    private BigDecimal precioUnitario;
    private BigDecimal ivaLinea;
    private BigDecimal totalLinea;
    private String motivo;
}

