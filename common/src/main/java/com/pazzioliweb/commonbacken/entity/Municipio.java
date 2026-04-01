package com.pazzioliweb.commonbacken.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "municipios")
@Data
public class Municipio {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int codigo;
	  private int codigoDepartamento;
	  private int codigoMunicipio;
	  public int getCodigoDepartamento() {
		return codigoDepartamento;
	}

	  public void setCodigoDepartamento(int codigoDepartamento) {
		  this.codigoDepartamento = codigoDepartamento;
	  }

	  public int getCodigoMunicipio() {
		  return codigoMunicipio;
	  }

	  public void setCodigoMunicipio(int codigoMunicipio) {
		  this.codigoMunicipio = codigoMunicipio;
	  }

	  private String municipio;

	  public int getCodigo() {
		  return codigo;
	  }

	  public void setCodigo(int codigo) {
		  this.codigo = codigo;
	  }

	  public String getMunicipio() {
		  return municipio;
	  }

	  public void setMunicipio(String municipio) {
		  this.municipio = municipio;
	  }
}
