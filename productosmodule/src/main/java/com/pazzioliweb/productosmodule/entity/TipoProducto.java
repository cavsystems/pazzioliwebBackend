package com.pazzioliweb.productosmodule.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipo_producto")
public class TipoProducto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tipo_producto_id")
	private  Integer tipoProductoId;
	
	private String nombre;
	private String descripcion;
	private Boolean estado;
	
	@Column(name = "fecha_creacion")
	private LocalDateTime fechaCreacion;

	public Integer getTipoProductoId() {
		return tipoProductoId;
	}

	public void setTipoProductoId(Integer tipoProductoId) {
		this.tipoProductoId = tipoProductoId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	} 
	
	
	
}
