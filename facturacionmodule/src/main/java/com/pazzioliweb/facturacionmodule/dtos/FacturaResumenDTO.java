package com.pazzioliweb.facturacionmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FacturaResumenDTO {

    Integer getFacturaId();
    Integer getConsecutivo();
    Integer getComprobanteId();
    LocalDateTime getFechaCreacion();
    LocalDate getFechaEmision();
    Integer getTerceroId();
    BigDecimal getTotalFactura();
    String getEstado();
    
    List<MetodoPagoFacturaDTO> getMetodosPago();
    List<TipoTotalFacturaDTO> getTipoTotales();
}
