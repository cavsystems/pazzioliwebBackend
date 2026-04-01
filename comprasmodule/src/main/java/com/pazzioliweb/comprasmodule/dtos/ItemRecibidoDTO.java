package com.pazzioliweb.comprasmodule.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ItemRecibidoDTO {
    private Long detalleId;
    private String codigoProducto;
    private Integer cantidadRecibida;
    private BigDecimal precioUnitarioReal;
    private String lote;
    private LocalDate fechaVencimiento;
    private String observaciones;
}
