package com.pazzioliweb.comprasmodule.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrdenCompraDTO {
    private Long id;
    private String numeroOrden;
    private String numeroOc;
    private Long proveedorId;
    private String proveedorNombre;
    private Long bodegaId;
    private String bodegaNombre;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEmision;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEntregaEsperada;
    
    private String estado;
    private String observaciones;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private BigDecimal retefuente;
    private BigDecimal reteiva;
    private BigDecimal reteica;
    private String usuarioCreacion;
    private LocalDate fechaCreacion;
    
    private List<DetalleOrdenCompraDTO> items;

    /** Métodos de pago guardados en la orden (para mostrarlos editables en el ingreso). */
    private List<MetodoPagoOrdenDTO> metodosPago;

    @lombok.Data
    public static class MetodoPagoOrdenDTO {
        private Integer metodoPagoId;
        private String  metodoPagoNombre;
        private String  metodoPagoSigla;
        private java.math.BigDecimal monto;
        private String  referencia;
    }
}
