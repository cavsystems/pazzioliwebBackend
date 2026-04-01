package com.pazzioliweb.usuariosbacken.entity;

import java.util.Date;
import java.util.List;

import io.micrometer.observation.transport.Propagator.Getter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "permisos_roles")
public class PermisoRol{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int codigo;

	 
String estado;	 
	    public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

		@ManyToOne
	    @JoinColumn(name = "codigorol")
	    private Roles codigorol;
	    
	    @ManyToOne
	    @JoinColumn(name = "codigopermiso")
	    private Permiso codigopermiso ;

		public int getCodigo() {
			return codigo;
		}

		public void setCodigo(int codigo) {
			this.codigo = codigo;
		}

		public Roles getCodigorol() {
			return codigorol;
		}

		public void setCodigorol(Roles codigorol) {
			this.codigorol = codigorol;
		}

		public Permiso getCodigopermiso() {
			return codigopermiso;
		}

		public void setCodigopermiso(Permiso codigopermiso) {
			this.codigopermiso = codigopermiso;
		}

	 
	 

	 
}