package com.pazzioliweb.usuariosbacken.dtos;

public class CrearusuariorolDTOS {
	private int[] bodegas;
	
	
	
	private String contrasena;

	private String estado;

	private String nombre;

	private int rol;
	
	private String usuario;

	public int[] getBodegas() {
		return bodegas;
	}

	public void setBodegas(int[] bodegas) {
		this.bodegas = bodegas;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getRol() {
		return rol;
	}

	public void setRol(int rol) {
		this.rol = rol;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	 public CrearusuariorolDTOS() {
		 
	    }

	public CrearusuariorolDTOS(int[] bodegas, String contrasena, String estado, String nombre, int rol,
			String usuario) {
		super();
		this.bodegas = bodegas;
		this.contrasena = contrasena;
		this.estado = estado;
		this.nombre = nombre;
		this.rol = rol;
		this.usuario = usuario;
	}
	
}
