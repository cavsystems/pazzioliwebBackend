package com.pazzioliweb.empresasback.entity;

import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.commonbacken.entity.Pais;
import com.pazzioliweb.commonbacken.entity.Tipoidentificacion;
import com.pazzioliweb.usuariosbacken.entity.Tipopersona;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

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


	public LocalDate getFechainiciolicencia() {
		return fechainiciolicencia;
	}

	public void setFechainiciolicencia(LocalDate fechainiciolicencia) {
		this.fechainiciolicencia = fechainiciolicencia;
	}

	@Column(name = "fechainiciolicencia")
	private LocalDate fechainiciolicencia;

	public LocalDate getFechafinallicencia() {
		return fechafinallicencia;
	}

	public void setFechafinallicencia(LocalDate fechafinallicencia) {
		this.fechafinallicencia = fechafinallicencia;
	}

	@Column(name = "fechafinallicencia")
	private LocalDate fechafinallicencia;
  
 private String numeroidentificacion;
 private String digitoverificacion;
 private String primernombre;
 private String  segundonombre ;
 private String primerapellido;
 private String  segundoapellido  ;
 private String razonsocial;
 private String codigopostal;

 // ──────────────────────────────────────────────────────────────
 // Datos fiscales DIAN (exigidos en cbc:TaxLevelCode del XML UBL)
 // ──────────────────────────────────────────────────────────────
 /**
  * Responsabilidad fiscal según código DIAN.
  * Valores válidos: O-13 (Gran Contribuyente), O-15 (Autorretenedor),
  * O-23 (Agente retención IVA), O-47 (Régimen Simple),
  * R-99-PN (No aplica — persona natural simplificado), etc.
  * Se puede combinar (ej: "O-13;O-15"). Sin esto DIAN rechaza el XML.
  */
 @Column(name = "responsabilidad_fiscal", length = 100)
 private String responsabilidadFiscal;

 /** Tipo de contribuyente: PERSONA_NATURAL / PERSONA_JURIDICA */
 @Column(name = "tipo_contribuyente", length = 30)
 private String tipoContribuyente;

 /** Marca si es Gran Contribuyente (afecta retenciones aplicables). */
 @Column(name = "gran_contribuyente", nullable = false)
 private Boolean granContribuyente = false;

 /** Marca si es autorretenedor (se autoaplica retención en fuente). */
 @Column(name = "autorretenedor", nullable = false)
 private Boolean autorretenedor = false;

 /** Marca si es responsable de IVA. */
 @Column(name = "responsable_iva", nullable = false)
 private Boolean responsableIva = true;

 // ──────────────────────────────────────────────────────────────
 // Getters/setters fiscales
 public String getResponsabilidadFiscal() { return responsabilidadFiscal; }
 public void setResponsabilidadFiscal(String responsabilidadFiscal) { this.responsabilidadFiscal = responsabilidadFiscal; }
 public String getTipoContribuyente() { return tipoContribuyente; }
 public void setTipoContribuyente(String tipoContribuyente) { this.tipoContribuyente = tipoContribuyente; }
 public Boolean getGranContribuyente() { return granContribuyente; }
 public void setGranContribuyente(Boolean granContribuyente) { this.granContribuyente = granContribuyente; }
 public Boolean getAutorretenedor() { return autorretenedor; }
 public void setAutorretenedor(Boolean autorretenedor) { this.autorretenedor = autorretenedor; }
 public Boolean getResponsableIva() { return responsableIva; }
 public void setResponsableIva(Boolean responsableIva) { this.responsableIva = responsableIva; }

	public int getNumerousuarios() {
		return numerousuarios;
	}

	public void setNumerousuarios(int numerousuarios) {
		this.numerousuarios = numerousuarios;
	}

	private int plazo ;

	public int getPlazo() {
		return plazo;
	}

	public void setPlazo(int plazo) {
		this.plazo = plazo;
	}

	private int numerousuarios;
 
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

	@Enumerated(EnumType.STRING)
	private Estado estado;

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
public Estado getEstado() {
	return estado;
}
public void setEstado(Estado estado) {
	this.estado = estado;
}



}
