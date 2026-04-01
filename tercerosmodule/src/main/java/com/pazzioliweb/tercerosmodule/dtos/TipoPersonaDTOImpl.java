package com.pazzioliweb.tercerosmodule.dtos;

import com.pazzioliweb.usuariosbacken.entity.Tipopersona;

public class TipoPersonaDTOImpl implements TipoPersonaDTO{
	private Integer codigo;
	private String nombre;
	
	// --- Conversión desde entidad ---
	public static TipoPersonaDTOImpl fromEntity(Tipopersona entity) {
		TipoPersonaDTOImpl dto = new TipoPersonaDTOImpl();
		dto.codigo=entity.getCodigo();
		dto.nombre=entity.getNombre();
		return dto;
	}
	
	// --- Conversión hacia entidad ---
	public Tipopersona toEntity() {
		Tipopersona entity = new Tipopersona();
		entity.setCodigo(this.getCodigo());
		entity.setNombre(this.getNombre());
		return entity;
	}

	@Override public Integer getCodigo() {return codigo;}
	@Override public String getNombre() {return nombre;}
	
	public void setCodigo(Integer codigo) {this.codigo = codigo;}
	public void setNombre(String nombre) {this.nombre = nombre;}
	
}
