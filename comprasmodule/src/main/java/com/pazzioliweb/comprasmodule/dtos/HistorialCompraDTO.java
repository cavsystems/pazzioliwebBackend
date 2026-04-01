package com.pazzioliweb.comprasmodule.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HistorialCompraDTO {
    private Long id;
    private String numeroOrden;
    private String estado;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEmision;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEntregaEsperada;
    
    private String proveedorNombre;
    private String proveedorNit;
    private String bodegaNombre;
    private Integer totalItems;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private String usuarioCreacion;
}
