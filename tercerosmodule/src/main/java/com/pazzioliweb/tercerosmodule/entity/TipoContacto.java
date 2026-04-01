package com.pazzioliweb.tercerosmodule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipo_contacto")
public class TipoContacto {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "tipo_contacto_id")
	    private Integer tipoContactoId;

	    @Column(name = "nombre", length = 50, nullable = false)
	    private String nombre;

	    @Column(name = "descripcion", length = 150)
	    private String descripcion;

	    @Column(name = "activo", nullable = false)
	    private Boolean activo = true;

		public Integer getTipoContactoId() {
			return tipoContactoId;
		}

		public void setTipoContactoId(Integer tipoContactoId) {
			this.tipoContactoId = tipoContactoId;
		}

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		public Boolean getActivo() {
			return activo;
		}

		public void setActivo(Boolean activo) {
			this.activo = activo;
		}
	    
	    
}
