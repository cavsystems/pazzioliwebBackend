package com.pazzioliweb.comprasmodule.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrdenCompraRequestDTO {
    private Long id;
    private String numeroOrden;
    private Long proveedorId;
    private Long bodegaId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEmision;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEntregaEsperada;
    
    private String estado;
    private String observaciones;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private String usuarioCreacion;
    private LocalDate fechaCreacion;
    
    private List<DetalleOrdenCompraDTO> items;
}
