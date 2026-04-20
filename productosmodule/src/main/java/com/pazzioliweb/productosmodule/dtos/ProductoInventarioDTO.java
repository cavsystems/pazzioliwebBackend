package com.pazzioliweb.productosmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ProductoInventarioDTO {
	Integer getProductoId();
	String getCodigoContable();
	String getReferencia();
	String getCodigobarras();
	String getDescripcion();
	Double getCosto();
	String getUnidadMedida();
	int getTarifa();
	String getLinea();
	String getGrupo();
	BigDecimal getCantidadGlobal();
	BigDecimal getTotalGlobalInventario();
	 int getGrupoid();
	   int  getLineaid();
	    int getTipoproductid();
	   int getImpuestoid();
	   boolean getManejavariante();
	   String getEstado();
	LocalDateTime getFechaUltimaCompra();
	LocalDateTime getFechaUltimaVenta();
	Integer getProductoVarianteId();
	String getImagen();
}
