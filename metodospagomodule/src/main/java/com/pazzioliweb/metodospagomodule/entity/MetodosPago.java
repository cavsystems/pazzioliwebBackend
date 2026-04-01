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
@Table(name = "metodos_pago")
@Data
public class MetodosPago {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metodo_pago_id")
    private Integer metodo_pago_id;
	
	@Column(name = "descripcion", nullable = false, length = 50)
    private String descripcion;
	
	@Column(name = "sigla", nullable = false, length = 50)
    private String sigla;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private Estado estado = Estado.ACTIVO;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "tipo_negociacion", nullable = false)
	private TipoNegociacion tipoNegociacion = TipoNegociacion.Contado; 
	
	public enum Estado {
        ACTIVO, INACTIVO
    }
	
	public enum TipoNegociacion{
		Contado,Credito
	}

	public Integer getMetodo_pago_id() {
		return metodo_pago_id;
	}

	public void setMetodo_pago_id(Integer metodo_pago_id) {
		this.metodo_pago_id = metodo_pago_id;
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
}
