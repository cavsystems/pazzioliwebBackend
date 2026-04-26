package com.pazzioliweb.tesoreriamodule.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ComprobanteEgresoResponseDTO {
    private Long id;
    private Integer consecutivo;
    private Integer terceroId;
    private String terceroNombre;
    private String terceroNit;
    private LocalDate fecha;
    private LocalDate fechaEgreso;
    private BigDecimal subtotal;
    private BigDecimal retefuente;
    private BigDecimal reteica;
    private BigDecimal reteiva;
    private BigDecimal descuento;
    private BigDecimal total;
    private List<MedioPagoResponseDTO> mediosPago;
    private String metodoPago;
    private String concepto;
    private String centroCosto;
    private String estado;
    private Boolean conceptoAbierto;
    private BigDecimal montoConceptoAbierto;
    private LocalDateTime fechaCreacion;
    private List<DetalleEgresoResponseDTO> detalles;

    @Data
    public static class MedioPagoResponseDTO {
        private Long id;
        private Integer metodoPagoId;
        private String metodoPagoDescripcion;
        private BigDecimal monto;
    }

    @Data
    public static class DetalleEgresoResponseDTO {
        private Long id;
        private Long cuentaPorPagarId;
        private String numeroFactura;
        private BigDecimal valorNeto;
        private BigDecimal montoAbonado;
        private String estado;
    }
}
