package com.pazzioliweb.productosmodule.dtos;

import java.math.BigDecimal;

public class ExistenciasUpdateDTO {
	private Integer bodegaId;
	private String bodega;
	private BigDecimal existencia;
    private BigDecimal stockMin;
    private BigDecimal stockMax;
    private String ubicacion;
    
	public String getBodega() {
		return bodega;
	}
	public void setBodega(String bodega) {
		this.bodega = bodega;
	}
	public BigDecimal getExistencia() {
		return existencia;
	}
	public void setExistencia(BigDecimal existencia) {
		this.existencia = existencia;
	}
	public BigDecimal getStockMin() {
		return stockMin;
	}
	public void setStockMin(BigDecimal stockMin) {
		this.stockMin = stockMin;
	}
	public BigDecimal getStockMax() {
		return stockMax;
	}
	public void setStockMax(BigDecimal stockMax) {
		this.stockMax = stockMax;
	}
	public String getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}
	public Integer getBodegaId() {
		return bodegaId;
	}
	public void setBodegaId(Integer bodegaId) {
		this.bodegaId = bodegaId;
	}
    
    
}
