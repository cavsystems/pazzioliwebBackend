package com.pazzioliweb.productosmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExistenciasResponseDTO {
	private Integer existenciaId;
    private Long productoVarianteId;
    private Integer bodegaId;
    private BigDecimal existencia;
    private BigDecimal stockMin;
    private BigDecimal stockMax;
    private String ubicacion;
    private LocalDateTime fechaUltimoMovimiento;
	public Integer getExistenciaId() {
		return existenciaId;
	}
	public void setExistenciaId(Integer existenciaId) {
		this.existenciaId = existenciaId;
	}
	public Long getProductoVarianteId() {
		return productoVarianteId;
	}
	public void setProductoVarianteId(Long productoVarianteId) {
		this.productoVarianteId = productoVarianteId;
	}
	public Integer getBodegaId() {
		return bodegaId;
	}
	public void setBodegaId(Integer bodegaId) {
		this.bodegaId = bodegaId;
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
	public LocalDateTime getFechaUltimoMovimiento() {
		return fechaUltimoMovimiento;
	}
	public void setFechaUltimoMovimiento(LocalDateTime fechaUltimoMovimiento) {
		this.fechaUltimoMovimiento = fechaUltimoMovimiento;
	}
    
    
}
