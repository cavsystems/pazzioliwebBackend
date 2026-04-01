package com.pazzioliweb.cajerosmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pazzioli.comprobantesmodule.dtos.ComprobanteDTO;

public interface DetalleCajeroDTO {
    Long getDetalleCajeroId();
    Integer getCajeroId();
    String getCajeroNombre();
    BigDecimal getMontoInicial();
    BigDecimal getMontoFinal();
    LocalDateTime getFechaApertura();
    LocalDateTime getFechaCierre();
    String getEstado();
    ComprobanteDTO getComprobante();
    Integer getConsecutivo();
    BigDecimal getTotalRecaudo();
    BigDecimal getTotalCosto();
    BigDecimal getBaseCaja();
    // Cuadre de caja
    BigDecimal getTotalEfectivo();
    BigDecimal getTotalMediosElectronicos();
    BigDecimal getEfectivoDeclarado();
    BigDecimal getMediosElectronicosDeclarado();
    BigDecimal getDiferenciaEfectivo();
    BigDecimal getDiferenciaMediosElectronicos();
}
