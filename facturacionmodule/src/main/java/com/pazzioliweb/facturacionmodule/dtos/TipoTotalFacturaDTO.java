package com.pazzioliweb.facturacionmodule.dtos;

import java.math.BigDecimal;

public interface TipoTotalFacturaDTO {
	Integer getTipoTotalId();
    BigDecimal getBase();
    BigDecimal getValor();
}
