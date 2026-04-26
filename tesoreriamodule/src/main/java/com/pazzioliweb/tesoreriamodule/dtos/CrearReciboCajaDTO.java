package com.pazzioliweb.tesoreriamodule.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CrearReciboCajaDTO {
    private Integer terceroId;
    private List<MedioPagoDTO> mediosPago;
    private String concepto;
    private String centroCosto;
    private String fechaRecibo;
    private BigDecimal retefuente = BigDecimal.ZERO;
    private BigDecimal reteica = BigDecimal.ZERO;
    private BigDecimal reteiva = BigDecimal.ZERO;
    private BigDecimal descuento = BigDecimal.ZERO;
    private BigDecimal averias = BigDecimal.ZERO;
    private BigDecimal fletes = BigDecimal.ZERO;
    private Integer cajeroId;
    private Integer usuarioId;
    private List<DetalleCobroDTO> cuentas;
    private Boolean conceptoAbierto = false;
    private BigDecimal montoConceptoAbierto;

    @Data
    public static class MedioPagoDTO {
        private Integer metodoPagoId;
        private BigDecimal monto;
    }

    @Data
    public static class DetalleCobroDTO {
        private Long cuentaPorCobrarId;
        private BigDecimal montoAbonado;
    }
}
