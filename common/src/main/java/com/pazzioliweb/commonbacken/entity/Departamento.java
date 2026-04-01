package com.pazzioliweb.commonbacken.entity;

import java.util.List;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "departamentos")
@Data
public class Departamento {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int codigo;

private int codigopais;

private int codigoDepartamento;

public int getCodigoDepartamento() {
	return codigoDepartamento;
}
public void setCodigoDepartamento(int codigoDepartamento) {
	this.codigoDepartamento = codigoDepartamento;
}
public int getCodigopais() {
	return codigopais;
}
public void setCodigopais(int codigopais) {
	this.codigopais = codigopais;
}

private String departamento;

public int getCodigo() {
	return codigo;
}
public void setCodigo(int codigo) {
	this.codigo = codigo;
}
public String getDepartamento() {
	return departamento;
}
public void setDepartamento(String departamento) {
	this.departamento = departamento;
}
}
