package com.pazzioliweb.productosmodule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipo_caracteristica")
public class TipoCaracteristica {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tipo_caracteristica_id")
	private Long tipoCaracteristicaId;
	
	private String nombre;
	public Long getTipoCaracteristicaId() {
		return tipoCaracteristicaId;
	}
	public void setTipoCaracteristicaId(Long tipoCaracteristicaId) {
		this.tipoCaracteristicaId = tipoCaracteristicaId;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
