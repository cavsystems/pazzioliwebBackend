package com.pazzioliweb.tesoreriamodule.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CrearReciboCajaDTO {
    private Integer terceroId;
    private Integer metodoPagoId;
    private String concepto;
    private String centroCosto;
    private BigDecimal retefuente = BigDecimal.ZERO;
    private BigDecimal reteica = BigDecimal.ZERO;
    private BigDecimal reteiva = BigDecimal.ZERO;
    private BigDecimal descuento = BigDecimal.ZERO;
    private Integer cajeroId;
    private Integer usuarioId;
    private List<DetalleCobroDTO> cuentas;

    @Data
    public static class DetalleCobroDTO {
        private Long cuentaPorCobrarId;
        private BigDecimal montoAbonado;
    }
}

