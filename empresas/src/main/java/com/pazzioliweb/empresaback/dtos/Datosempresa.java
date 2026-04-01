package com.pazzioliweb.empresaback.dtos;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Impuestos;
import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.commonbacken.entity.Pais;
import com.pazzioliweb.commonbacken.entity.Tipoidentificacion;
import com.pazzioliweb.empresasback.entity.Actividadeconomica;
import com.pazzioliweb.empresasback.entity.Regimen;
import com.pazzioliweb.usuariosbacken.entity.Tipopersona;
@Component
public class Datosempresa {
	private List<Tipopersona> tipopersona;
    private List<Tipoidentificacion> tipoidentificacion;
   private List<Regimen> regimen;
   private List<Pais> pais;
   private List<Departamento> departamento;
   private List<Municipio> municipio;
   

	public List<Municipio> getMunicipio() {
	return municipio;
}

   public void setMunicipio(List<Municipio> municipio) {
	this.municipio = municipio;
   }

	public List<Pais> getPais() {
	return pais;
}

   public void setPais(List<Pais> pais) {
	this.pais = pais;
   }

   public List<Departamento> getDepartamento() {
	return departamento;
   }

   public void setDepartamento(List<Departamento> departamento) {
	this.departamento = departamento;
   }

	public List<Regimen> getRegimen() {
	return regimen;
}

   public void setRegimen(List<Regimen> regimen) {
	this.regimen = regimen;
   }

	public List<Tipoidentificacion> getTipoidentificacion() {
		return tipoidentificacion;
	}

	public void setTipoidentificacion(List<Tipoidentificacion> tipoidentificacion) {
		this.tipoidentificacion = tipoidentificacion;
	}

	public List<Tipopersona> getTipopersona() {
		return tipopersona;
	}

	public void setTipopersona(List<Tipopersona> tipopersona) {
		this.tipopersona = tipopersona;
	}
	

}
