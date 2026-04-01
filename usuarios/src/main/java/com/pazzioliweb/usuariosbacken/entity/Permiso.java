package com.pazzioliweb.usuariosbacken.entity;

import java.util.List;
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
@Table(name = "permisos")
public class Permiso{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int codigo;
	
	
	   @OneToMany(mappedBy= "codigopermiso")
	   	private List<PermisoRol> permisos_roles;
	   
	private String nombre;

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public List<PermisoRol> getPermisos_roles() {
		return permisos_roles;
	}

	public void setPermisos_roles(List<PermisoRol> permisos_roles) {
		this.permisos_roles = permisos_roles;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	
	
	
}