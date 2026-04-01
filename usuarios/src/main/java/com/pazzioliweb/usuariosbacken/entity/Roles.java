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
import lombok.ToString;

@Entity
@Data
@Table(name = "roles")
public class Roles {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigo;

    private String nombre;
    @OneToMany(mappedBy= "codigorol")
	@ToString.Exclude
   	private List<PermisoRol> permisos_roles;
 
    // Opcional: relación inversa si la necesitas
	@ToString.Exclude
    @OneToMany(mappedBy = "codigorol")
    private List<Usuario> usuarios;
    
   
	
	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<PermisoRol> getPermisos_roles() {
		return permisos_roles;
	}

	public void setPermisos_roles(List<PermisoRol> permisos_roles) {
		this.permisos_roles = permisos_roles;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
    
   
	
	
}
