package com.pazzioliweb.commonbacken.entity;



import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "sesiones")
@Data
public class Sesiones {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int codigo;

	@Column(name = "codigousuario", nullable = false, length = 50)
	private int codigoUsuario;


	public long getCodigo() {
		return codigo;
	}





	public int getCodigoUsuario() {
		return codigoUsuario;
	}





	public void setCodigoUsuario(int codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
	}





	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}





	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}




	@Column(length = 20)
	private String estado;

	@Column(length = 20)

	private LocalDateTime fechainicio;

	@Column(length = 20)

	private LocalDateTime fechafin;

	@Column(length = 20)
	private String ipclient;

	@Column
	private String token;


	public LocalDateTime getFechainicio() {
		return fechainicio;
	}

	public void setFechainicio(LocalDateTime fechainicio) {
		this.fechainicio = fechainicio;
	}

	public LocalDateTime getFechafin() {
		return fechafin;
	}

	public void setFechafin(LocalDateTime fechafin) {
		this.fechafin = fechafin;
	}

	public String getIpclient() {
		return ipclient;
	}

	public void setIpclient(String ipclient) {
		this.ipclient = ipclient;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
