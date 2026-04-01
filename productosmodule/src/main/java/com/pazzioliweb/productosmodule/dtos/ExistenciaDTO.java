package com.pazzioliweb.productosmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ExistenciaDTO {
	Integer getExistenciaId();

    // Producto
    Integer getProductoId();
    String getProductoDescripcion();
    String getProductoCodigoContable();

    // Bodega
    Integer getBodegaCodigo();
    String getBodegaNombre();

    // Existencias
    BigDecimal getExistencia();
    BigDecimal getStockMin();
    BigDecimal getStockMax();
    LocalDateTime getFechaUltimoMovimiento();
    String getUbicacion();
}
