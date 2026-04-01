package com.pazzioliweb.productosmodule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "caracteristicas")
public class Caracteristica {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "caracteristica_id")
	private Long caracteristicaId;
	
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tipo_caracteristica_id")
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
	
}
