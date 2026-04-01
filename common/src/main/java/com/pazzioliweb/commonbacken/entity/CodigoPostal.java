package com.pazzioliweb.commonbacken.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "codigospostalesnacionales")
@Data
public class CodigoPostal {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int codigo;
	int codigoMunicipio;
	String nombreMunicipio;
	String zonaPostal;
	String codigoPostal;
	
	public int getCodigo() {
		return codigo;
	}
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	public int getCodigoMunicipio() {
		return codigoMunicipio;
	}
	public void setCodigoMunicipio(int codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}
	public String getNombreMunicipio() {
		return nombreMunicipio;
	}
	public void setNombreMunicipio(String nombreMunicipio) {
		this.nombreMunicipio = nombreMunicipio;
	}
	public String getZonaPostal() {
		return zonaPostal;
	}
	public void setZonaPostal(String zonaPostal) {
		this.zonaPostal = zonaPostal;
	}
	public String getCodigoPostal() {
		return codigoPostal;
	}
	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}
	
}
