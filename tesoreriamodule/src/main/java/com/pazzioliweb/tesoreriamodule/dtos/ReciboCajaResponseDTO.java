package com.pazzioliweb.tesoreriamodule.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReciboCajaResponseDTO {
    private Long id;
    private Integer consecutivo;
    private Integer terceroId;
    private String terceroNombre;
    private String terceroNit;
    private LocalDate fecha;
    private LocalDate fechaRecibo;
    private BigDecimal subtotal;
    private BigDecimal retefuente;
    private BigDecimal reteica;
    private BigDecimal reteiva;
    private BigDecimal descuento;
    private BigDecimal averias;
    private BigDecimal fletes;
    private BigDecimal total;
    private List<MedioPagoResponseDTO> mediosPago;
    private String metodoPago;
    private String concepto;
    private String centroCosto;
    private String estado;
    private Boolean conceptoAbierto;
    private BigDecimal montoConceptoAbierto;
    private LocalDateTime fechaCreacion;
    private List<DetalleReciboResponseDTO> detalles;

    @Data
    public static class MedioPagoResponseDTO {
        private Long id;
        private Integer metodoPagoId;
        private String metodoPagoDescripcion;
        private BigDecimal monto;
    }

    @Data
    public static class DetalleReciboResponseDTO {
        private Long id;
        private Long cuentaPorCobrarId;
        private String numeroVenta;
        private BigDecimal valorNeto;
        private BigDecimal saldo;
        private BigDecimal montoAbonado;
        private String estado;
    }
}
