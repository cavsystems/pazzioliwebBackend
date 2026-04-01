package com.pazzioliweb.productosmodule.dtos;

import com.pazzioliweb.productosmodule.entity.TipoCaracteristica;

public class CaracteristicaDTO {
	private Long caracteristicaId;
	private String nombre;
	private TipoCaracteristica tipo;
	public Long getCaracteristicaId() {
		return caracteristicaId;
	}
	public void setCaracteristicaId(Long caracteristicaId) {
		this.caracteristicaId = caracteristicaId;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public TipoCaracteristica getTipo() {
		return tipo;
	}
	public void setTipo(TipoCaracteristica tipo) {
		this.tipo = tipo;
	}
	public CaracteristicaDTO(Long caracteristicaId, String nombre, TipoCaracteristica tipo) {
		
		this.caracteristicaId = caracteristicaId;
		this.nombre = nombre;
		this.tipo = tipo;
	}
}
