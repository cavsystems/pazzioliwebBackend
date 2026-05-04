package com.pazzioliweb.tesoreriamodule.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CrearComprobanteEgresoDTO {
    private Integer terceroId;
    private List<MedioPagoDTO> mediosPago;
    private String concepto;
    private String centroCosto;
    private String fechaEgreso;
    private BigDecimal retefuente = BigDecimal.ZERO;
    private BigDecimal reteica = BigDecimal.ZERO;
    private BigDecimal reteiva = BigDecimal.ZERO;
    private BigDecimal descuento = BigDecimal.ZERO;
    private Integer cajeroId;
    private Integer usuarioId;
    private List<DetallePagoDTO> cuentas;
    private Boolean conceptoAbierto = false;
    private BigDecimal montoConceptoAbierto;
    private Long conceptoAbiertoId;
    private Integer cuentaContableId;
    private String beneficiarioNombre;
    private String beneficiarioDocumento;

    @Data
    public static class MedioPagoDTO {
        private Integer metodoPagoId;
        private BigDecimal monto;
    }

    @Data
    public static class DetallePagoDTO {
        private Long cuentaPorPagarId;
        private BigDecimal montoAbonado;
    }
}
