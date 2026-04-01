package com.pazzioliweb.usuariosbacken.dtos;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
@Data
public class crearpersonaDTOS {
	 private String nombre;
	    private String direccion;
	    private  String apellido;
	    private String  identificacion;
	    private int codigousuario;
	    public int getCodigousuario() {
			return codigousuario;
		}
		public void setCodigousuario(int codigousuario) {
			this.codigousuario = codigousuario;
		}
		public String getIdentificacion() {
			return identificacion;
		}
		public void setIdentificacion(String identificacion) {
			this.identificacion = identificacion;
		}
		public String getApellido() {
			return apellido;
		}
		public void setApellido(String apellido) {
			this.apellido = apellido;
		}
		public String getNombre() {
			return nombre;
		}
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		public String getDireccion() {
			return direccion;
		}
		public void setDireccion(String direccion) {
			this.direccion = direccion;
		}
		public String getCorreo() {
			return correo;
		}
		public void setCorreo(String correo) {
			this.correo = correo;
		}
		public String getEstado() {
			return estado;
		}
		public void setEstado(String estado) {
			this.estado = estado;
		}
		public MultipartFile getImagenperfil() {
			return imagenperfil;
		}
		public void setImagenperfil(MultipartFile imagenperfil) {
			this.imagenperfil = imagenperfil;
		}
		private String correo;
	    private String estado;
	    private MultipartFile imagenperfil;

}
