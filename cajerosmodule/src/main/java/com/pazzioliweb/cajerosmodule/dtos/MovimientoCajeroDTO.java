package com.pazzioliweb.cajerosmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface MovimientoCajeroDTO {
    Long getMovimientoCajeroId();
    Long getDetalleCajeroId();
    Integer getCajeroId();
    String getCajeroNombre();
    String getTipoMovimiento();
    String getNumeroComprobante();
    Long getReferenciaDocumentoId();
    BigDecimal getMonto();
    BigDecimal getCosto();
    BigDecimal getMontoEfectivo();
    BigDecimal getMontoElectronico();
    Integer getConsecutivo();
    Integer getConsecutivoTipo();
    String getDescripcion();
    LocalDateTime getFechaMovimiento();
}
