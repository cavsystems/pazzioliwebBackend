package com.pazzioliweb.productosmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PreciosProductoVarianteDTO {
	Integer getProductoVarianteId();
	Integer getPreciosProductoId();
	String getPrecio();
	BigDecimal getValor();
	Integer getPrecioId();
	LocalDateTime getFechaCreacion();
	LocalDateTime getFechaInicio();
	LocalDateTime getFechaFin();
}
