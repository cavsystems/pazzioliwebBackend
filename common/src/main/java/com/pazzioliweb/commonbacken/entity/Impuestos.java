package com.pazzioliweb.commonbacken.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "impuestos")
@Data
public class Impuestos {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer codigo;
	  	private String nombre;
	  	private Double tarifa;
	  	private Double base;
	  	private String estado;
	  
	  
	  
	  public String getEstado() {
		return estado;
	}
	  public void setEstado(String estado) {
		this.estado = estado;
	}
	  public Integer getCodigo() {
		return codigo;
	}
	  public void setCodigo(Integer codigo) {
		  this.codigo = codigo;
	  }
	  public String getNombre() {
		  return nombre;
	  }
	  public void setNombre(String nombre) {
		  this.nombre = nombre;
	  }
	  public Double getTarifa() {
		  return tarifa;
	  }
	  public void setTarifa(Double tarifa) {
		  this.tarifa = tarifa;
	  }
	  public Double getBase() {
		  return base;
	  }
	  public void setBase(Double base) {
		  this.base = base;
	  }
	  public String getSigla() {
		  return sigla;
	  }
	  public void setSigla(String sigla) {
		  this.sigla = sigla;
	  }
	  private String sigla;
}
