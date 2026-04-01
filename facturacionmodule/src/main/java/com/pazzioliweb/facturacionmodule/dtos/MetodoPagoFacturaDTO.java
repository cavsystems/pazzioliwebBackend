package com.pazzioliweb.facturacionmodule.dtos;

import java.math.BigDecimal;

import com.pazzioliweb.metodospagomodule.dtos.MetodoPagoDTO;

public interface MetodoPagoFacturaDTO {
	Integer getMetodoPagoId();
    BigDecimal getValor();
    MetodoPagoDTO getMetodopago();
}
