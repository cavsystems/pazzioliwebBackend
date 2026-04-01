package com.pazzioliweb.empresasback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "regimen")
public class Regimen {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int codigo;
	
	private String codigoRegimen;
	private String descripcion;
	private String estado;
   public String getCodigoRegimen() {
	return codigoRegimen;
}
  public void setCodigoRegimen(String codigoRegimen) {
	this.codigoRegimen = codigoRegimen;
  }
  public String getDescripcion() {
	return descripcion;
  }
  public void setDescripcion(String descripcion) {
	this.descripcion = descripcion;
  }
  public String getEstado() {
	return estado;
  }
  public void setEstado(String estado) {
	this.estado = estado;
  }
   public int getCodigo() {
	return codigo;
   }
   public void setCodigo(int codigo) {
	this.codigo = codigo;
   }
   
}
