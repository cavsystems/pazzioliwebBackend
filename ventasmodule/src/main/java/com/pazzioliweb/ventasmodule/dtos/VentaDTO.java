package com.pazzioliweb.ventasmodule.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class VentaDTO {
    private Long id;
    private String numeroVenta;
    private Long clienteId;
    private String clienteNombre;
    private Integer bodegaId;
    private String bodegaNombre;

    private Long cajeroId;
    private String cajeroNombre;

    private Integer vendedorId;
    private String vendedorNombre;

    private List<VentaMetodoPagoDTO> metodosPago;

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

    private List<DetalleVentaDTO> items;
}
