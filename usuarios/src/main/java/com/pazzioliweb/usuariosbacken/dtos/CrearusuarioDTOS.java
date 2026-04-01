package com.pazzioliweb.usuariosbacken.dtos;



public class CrearusuarioDTOS {
    private String nombre;
    private String contrasena;
    public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public CrearusuarioDTOS() {} // 👈 constructor vacío obligatorio

    public CrearusuarioDTOS(String nombre ,String contrasena) {
        this.nombre = nombre;
        this.contrasena=contrasena;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; } // 👈 setter obligatorio
}

