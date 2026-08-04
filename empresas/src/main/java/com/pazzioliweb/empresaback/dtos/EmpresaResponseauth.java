package com.pazzioliweb.empresaback.dtos;

import java.util.List;

public class EmpresaResponseauth{
	String nombreconexion;
	String empresa;
	String identificacion;
	String tipoidentificacion;

	// Requerido por @ConstructorResult (EmpresaResponseauthMapping en Empresas.java):
	// el orden de los parámetros debe calzar EXACTO con el orden de los @ColumnResult.
	public EmpresaResponseauth(String nombreconexion, String empresa, String identificacion, String tipoidentificacion) {
		this.nombreconexion = nombreconexion;
		this.empresa = empresa;
		this.identificacion = identificacion;
		this.tipoidentificacion = tipoidentificacion;
	}

	public String getNombreconexion() {
		return nombreconexion;
	}

	public void setNombreconexion(String nombreconexion) {
		this.nombreconexion = nombreconexion;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public String getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	public String getTipoidentificacion() {
		return tipoidentificacion;
	}

	public void setTipoidentificacion(String tipoidentificacion) {
		this.tipoidentificacion = tipoidentificacion;
	}

}
