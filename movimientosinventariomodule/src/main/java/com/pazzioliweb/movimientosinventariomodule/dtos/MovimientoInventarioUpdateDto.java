package com.pazzioliweb.movimientosinventariomodule.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

public class MovimientoInventarioUpdateDto {
	// Solo campos editables cuando el movimiento está en BORRADOR (por política)
    private Integer comprobanteId;
    private Integer consecutivo;
    private LocalDate fechaEmision;

    @Size(max = 1000)
    private String observaciones;

	public Integer getComprobanteId() {
		return comprobanteId;
	}

	public void setComprobanteId(Integer comprobanteId) {
		this.comprobanteId = comprobanteId;
	}

	public Integer getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(Integer consecutivo) {
		this.consecutivo = consecutivo;
	}

	public LocalDate getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(LocalDate fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
    
    
}
