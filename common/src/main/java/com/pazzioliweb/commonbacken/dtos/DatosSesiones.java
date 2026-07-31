package com.pazzioliweb.commonbacken.dtos;

import java.time.Instant;
import java.util.List;
import java.util.Map;

//DTO para trabajar colas secciones que se crean

public class DatosSesiones {
	public String getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public Instant getCreada() {
		return creada;
	}
	public void setCreada(Instant creada) {
		this.creada = creada;
	}
	public Instant getExpira() {
		return expira;
	}
	public void setExpira(Instant expira) {
		this.expira = expira;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	private String idUsuario;
	private String login;
	private String dbName;
	private String nivel;

	public int getIdusuario() {
		return idusuario;
	}

	public void setIdusuario(int idcliente) {
		this.idusuario = idcliente;
	}

	private int idusuario;
	public String getIdentificion() {
		return identificion;
	}

	public void setIdentificion(String identificion) {
		this.identificion = identificion;
	}

	private  String identificion;
	public int getBodegaid() {
		return bodegaid;
	}

	public void setBodegaid(int bodegaid) {
		this.bodegaid = bodegaid;
	}

	private int bodegaid;
	private List<String> permisos;
	public List<String> getPermisos() {
		return permisos;
	}
	public void setPermisos(List<String> permisos) {
		this.permisos = permisos;
	}

	// Clave = nombre del permiso padre, valor = lista de codigo_accion asignados al rol
	private Map<String, List<String>> subpermisos;
	public Map<String, List<String>> getSubpermisos() {
		return subpermisos;
	}
	public void setSubpermisos(Map<String, List<String>> subpermisos) {
		this.subpermisos = subpermisos;
	}
	public String getNivel() {
		return nivel;
	}
	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	// ✅ Datos del cajero — se pueblan en el login si el usuario tiene cajero activo con sesión abierta
	private Integer cajeroId;
	private Long detalleCajeroId;

	public Integer getCajeroId() {
		return cajeroId;
	}
	public void setCajeroId(Integer cajeroId) {
		this.cajeroId = cajeroId;
	}
	public Long getDetalleCajeroId() {
		return detalleCajeroId;
	}
	public void setDetalleCajeroId(Long detalleCajeroId) {
		this.detalleCajeroId = detalleCajeroId;
	}

	// ✅ Datos de la empresa
	private byte[] imagenempresa;
	private String tipoImagen;
	private String nombreEmpresa;
	private String numeroIdentificacion;
	private String digitoVerificacion;
	private String tipoIdentificacion;

	public byte[] getImagenempresa() {
		return imagenempresa;
	}
	public void setImagenempresa(byte[] imagenempresa) {
		this.imagenempresa = imagenempresa;
	}
	public String getTipoImagen() {
		return tipoImagen;
	}
	public void setTipoImagen(String tipoImagen) {
		this.tipoImagen = tipoImagen;
	}
	public String getNombreEmpresa() {
		return nombreEmpresa;
	}
	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}
	public String getNumeroIdentificacion() {
		return numeroIdentificacion;
	}
	public void setNumeroIdentificacion(String numeroIdentificacion) {
		this.numeroIdentificacion = numeroIdentificacion;
	}
	public String getDigitoVerificacion() {
		return digitoVerificacion;
	}
	public void setDigitoVerificacion(String digitoVerificacion) {
		this.digitoVerificacion = digitoVerificacion;
	}
	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}
	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}

	private Instant creada;
	private Instant expira;

}