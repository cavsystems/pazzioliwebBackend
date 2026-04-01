package com.pazzioliweb.tercerosmodule.entity;

import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Municipio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sedes_tercero")
public class SedeTercero {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sede_id")
    private Integer sedeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tercero_id", nullable = false)
    private Terceros tercero;

    @Column(name = "nombre_sede", length = 100, nullable = false)
    private String nombreSede;

    @Column(name = "direccion", length = 200, nullable = false)
    private String direccion;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id")
    private Municipio municipio;

    @Column(name = "principal", nullable = false)
    private Boolean principal = false;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

	public Integer getSedeId() {
		return sedeId;
	}

	public void setSedeId(Integer sedeId) {
		this.sedeId = sedeId;
	}

	public Terceros getTercero() {
		return tercero;
	}

	public void setTercero(Terceros tercero) {
		this.tercero = tercero;
	}

	public String getNombreSede() {
		return nombreSede;
	}

	public void setNombreSede(String nombreSede) {
		this.nombreSede = nombreSede;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
}
