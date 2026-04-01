package com.pazzioliweb.productosmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ExistenciasBodegaDTO {
	Integer getExistenciaId();
    Integer getProductoVarianteId();
    Integer getBodegaId();
    BigDecimal getExistencia();
    BigDecimal getStockMin();
    BigDecimal getStockMax();
    String getUbicacion();
    LocalDateTime getFechaUltimoMovimiento();
    String getBodega();
}
