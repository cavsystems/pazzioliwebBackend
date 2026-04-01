package com.pazzioliweb.usuariosbacken.dtos;

import com.pazzioliweb.usuariosbacken.entity.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class UsuarioDTO {
   
	public UsuarioDTO(int codigo, String nombre, String usuario, String estado, RolDTO rol) {
	
		this.codigo = codigo;
		this.nombre = nombre;
		this.usuario = usuario;
		this.estado = estado;
		this.rol = rol;
	}
	
	public static UsuarioDTO fromEntity(Usuario usuario) {
        if (usuario == null) return null;

        return new UsuarioDTO(
            usuario.getCodigo(),
            usuario.getNombre(),
            usuario.getUsuario(),
            usuario.getEstado(),
            usuario.getCodigorol() != null ? RolDTO.fromEntity(usuario.getCodigorol()) : null
        );
    }
	
	private int codigo;
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
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public RolDTO getRol() {
		return rol;
	}
	public void setRol(RolDTO rol) {
		this.rol = rol;
	}
	private String nombre;
    private String usuario;
    private String estado;
    private RolDTO rol;
}

