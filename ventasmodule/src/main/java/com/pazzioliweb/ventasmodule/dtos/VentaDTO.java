package com.pazzioliweb.ventasmodule.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private BigDecimal descuentos;
    private BigDecimal total;
    // Retenciones que el cliente nos practica (sufridas)
    private BigDecimal retefuente;
    private BigDecimal reteiva;
    private BigDecimal reteica;
    private String usuarioCreacion;
    private LocalDateTime fechaCreacion;

    private List<DetalleVentaDTO> items;
}
