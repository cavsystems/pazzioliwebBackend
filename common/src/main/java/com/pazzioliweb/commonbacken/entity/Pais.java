package com.pazzioliweb.commonbacken.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "paises")
@Data
public class Pais {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int codigo;
	  
	  private int codigoPais;
	  
	  public int getCodigoPais() {
		return codigoPais;
	}

	  public void setCodigoPais(int codigoPais) {
		  this.codigoPais = codigoPais;
	  }

	  private String pais;

	  public int getCodigo() {
		  return codigo;
	  }

	  public void setCodigo(int codigo) {
		  this.codigo = codigo;
	  }

	  public String getPais() {
		  return pais;
	  }

	  public void setPais(String pais) {
		  this.pais= pais;
	  }

}
