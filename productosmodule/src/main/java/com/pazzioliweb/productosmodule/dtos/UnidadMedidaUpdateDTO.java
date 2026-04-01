package com.pazzioliweb.productosmodule.dtos;

public class UnidadMedidaUpdateDTO {
	private Integer unidadMedidaId;
	private String descripcion;
	private String sigla;
	
	public Integer getUnidadMedidaId() {
		return unidadMedidaId;
	}
	public void setUnidadMedidaId(Integer unidadMedidaId) {
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
