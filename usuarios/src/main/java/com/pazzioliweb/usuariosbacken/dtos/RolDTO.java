package com.pazzioliweb.usuariosbacken.dtos;

import com.pazzioliweb.usuariosbacken.entity.Roles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RolDTO {
    public RolDTO(int codigo2, String nombre2) {
		this.codigo=codigo2;
		this.nombre=nombre2;
		
	}
    
    public static RolDTO fromEntity(Roles rol) {
        return rol != null ? new RolDTO(rol.getCodigo(), rol.getNombre()) : null;
    }
    
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
	private int codigo;
    private String nombre;
}
