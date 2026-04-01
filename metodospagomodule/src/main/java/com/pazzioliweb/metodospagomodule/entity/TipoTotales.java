package com.pazzioliweb.metodospagomodule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tipo_totales")
@Data
public class TipoTotales {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipo_total_id")
    private Integer tipo_total_id;
	
	@Column(name = "descripcion", nullable = false, length = 50)
    private String descripcion;
	
	@Column(name = "sigla", nullable = false, length = 50)
    private String sigla;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private Estado estado = Estado.ACTIVO;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private Tipo tipo = Tipo.IMPUESTO;
	
	public enum Estado {
        ACTIVO, INACTIVO
    }
	
	public enum Tipo {
		IMPUESTO, BASE, DESCUENTO
    }

	public Integer getTipo_total_id() {
		return tipo_total_id;
	}

	public void setTipo_total_id(Integer tipo_total_id) {
		this.tipo_total_id = tipo_total_id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
}
