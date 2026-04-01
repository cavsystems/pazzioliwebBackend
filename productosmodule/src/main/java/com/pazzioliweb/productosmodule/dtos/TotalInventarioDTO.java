package com.pazzioliweb.productosmodule.dtos;

import java.math.BigDecimal;

public interface TotalInventarioDTO {
	BigDecimal getTotal();
	Long getCantidadTotal();
	String getBodega();
}
