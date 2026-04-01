package com.pazzioliweb.productosmodule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "precios")
public class Precios {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "precio_id")
	private Integer precioId;
	private String descripcion;
	public Integer getPrecioId() {
		return precioId;
	}
	public void setPrecioId(Integer precioId) {
		this.precioId = precioId;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
}
