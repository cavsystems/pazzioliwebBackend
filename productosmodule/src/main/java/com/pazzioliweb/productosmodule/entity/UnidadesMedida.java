package com.pazzioliweb.productosmodule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "unidades_medida")
public class UnidadesMedida {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "unidad_medida_id")
	private Integer unidadMedidaId;
	
	private String descripcion;
	private String sigla;
	
	public int getUnidadMedidaId() {
		return unidadMedidaId;
	}
	public void setUnidadMedidaId(int unidadMedidaId) {
		this.unidadMedidaId = unidadMedidaId;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
}
