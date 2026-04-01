package com.pazzioliweb.tercerosmodule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "clasificaciones_terceros")
@Data
public class ClasificacionTercero {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "clasificacion_tercero_id")
	private Integer ClasificacionTerceroId;
	
	@Column(name = "nombre", length = 100)
	private String nombre;

	public Integer getClasificacionTerceroId() {
		return ClasificacionTerceroId;
	}

	public void setClasificacionTerceroId(Integer clasificacionTerceroId) {
		ClasificacionTerceroId = clasificacionTerceroId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
