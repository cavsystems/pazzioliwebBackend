package com.pazzioliweb.empresasback.entity;

import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.commonbacken.entity.Pais;
import com.pazzioliweb.commonbacken.entity.Tipoidentificacion;
import com.pazzioliweb.usuariosbacken.entity.Tipopersona;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "empresa")
@Data
public class Empresa {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigo;
	
	 @ManyToOne
	    @JoinColumn(name = "codigopais")
	    private Pais codigopais  ;
  
  @ManyToOne
	    @JoinColumn(name = "codigomunicipio")
	    private Municipio codigomunicipio;
  
  
  @ManyToOne
	    @JoinColumn(name = "codigodepartamento")
	    private Departamento  codigodepartamento;
  
  
  @ManyToOne
  @JoinColumn(name = "codigotipopersona")
  private Tipopersona  codigotipopersona;
  
  
  @ManyToOne
  @JoinColumn(name = " codigotipoidentificacion")
  private Tipoidentificacion  codigotipoidentificacion;
  
  
 private String numeroidentificacion;
 private String digitoverificacion;
 private String primernombre;
 private String  segundonombre ;
 private String primerapellido;
 private String  segundoapellido  ;
 private String razonsocial;
 private String codigopostal;
 
public String getCodigopostal() {
	return codigopostal;
}
 public void setCodigopostal(String codigopostal) {
	this.codigopostal = codigopostal;
 }
private String nombrecomercial;

 @ManyToOne
 @JoinColumn(name = "codigoregimen")
 private Regimen codigoregimen;
  
private String correoempresa ;
 private String celularempresa;
private String telfonofijo;
@ManyToOne
@JoinColumn(name = "codigoactividadeconomica")
private Actividadeconomica codigoactividadeconomica;

public Actividadeconomica getCodigoactividadeconomica() {
	return codigoactividadeconomica;
}
public void setCodigoactividadeconomica(Actividadeconomica codigoactividadeconomica) {
	this.codigoactividadeconomica = codigoactividadeconomica;
}
public String getCelularempresa() {
	return celularempresa;
}
public void setCelularempresa(String celularempresa) {
	this.celularempresa = celularempresa;
}
public int getCodigo() {
	return codigo;
}
public void setCodigo(int codigo) {
	this.codigo = codigo;
}
public Pais getCodigopais() {
	return codigopais;
}
public void setCodigopais(Pais codigopais) {
	this.codigopais = codigopais;
}
public Municipio getCodigomunicipio() {
	return codigomunicipio;
}
public void setCodigomunicipio(Municipio codigomunicipio) {
	this.codigomunicipio = codigomunicipio;
}
public Departamento getCodigodepartamento() {
	return codigodepartamento;
}
public void setCodigodepartamento(Departamento codigodepartamento) {
	this.codigodepartamento = codigodepartamento;
}
public Tipopersona getCodigotipopersona() {
	return codigotipopersona;
}
public void setCodigotipopersona(Tipopersona codigotipopersona) {
	this.codigotipopersona = codigotipopersona;
}
public Tipoidentificacion getCodigotipoidentificacion() {
	return codigotipoidentificacion;
}
public void setCodigotipoidentificacion(Tipoidentificacion codigotipoidentificacion) {
	this.codigotipoidentificacion = codigotipoidentificacion;
}
public String getNumeroidentificacion() {
	return numeroidentificacion;
}
public void setNumeroidentificacion(String numeroidentificion) {
	this.numeroidentificacion = numeroidentificion;
}
public String getDigitoverificacion() {
	return digitoverificacion;
}
public void setDigitoverificacion(String digitoverificacion) {
	this.digitoverificacion = digitoverificacion;
}
public String getPrimernombre() {
	return primernombre;
}
public void setPrimernombre(String primernombre) {
	this.primernombre = primernombre;
}
public String getSegundonombre() {
	return segundonombre;
}
public void setSegundonombre(String segundonombre) {
	this.segundonombre = segundonombre;
}
public String getPrimerapellido() {
	return primerapellido;
}
public void setPrimerapellido(String primerapellido) {
	this.primerapellido = primerapellido;
}
public String getSegundoapellido() {
	return segundoapellido;
}
public void setSegundoapellido(String segundoapellido) {
	this.segundoapellido = segundoapellido;
}
public String getRazonsocial() {
	return razonsocial;
}
public void setRazonsocial(String razonsocial) {
	this.razonsocial = razonsocial;
}
public String getNombrecomercial() {
	return nombrecomercial;
}
public void setNombrecomercial(String nombrecomercial) {
	this.nombrecomercial = nombrecomercial;
}
public Regimen getCodigoregimen() {
	return codigoregimen;
}
public void setCodigoregimen(Regimen codigoregimen) {
	this.codigoregimen = codigoregimen;
}
public String getCorreoempresa() {
	return correoempresa;
}
public void setCorreoempresa(String correoempresa) {
	this.correoempresa = correoempresa;
}
public String getTelfonofijo() {
	return telfonofijo;
}
public void setTelfonofijo(String telfonofijo) {
	this.telfonofijo = telfonofijo;
}
  
@Lob
@Column(columnDefinition = "LONGBLOB")
private byte[] imagenEmpresa;

private String tipoImagen;

public byte[] getImagenEmpresa() {
	return imagenEmpresa;
}
public void setImagenEmpresa(byte[] imagenEmpresa) {
	this.imagenEmpresa = imagenEmpresa;
}
public String getTipoImagen() {
	return tipoImagen;
}
public void setTipoImagen(String tipoImagen) {
	this.tipoImagen = tipoImagen;
}



}
