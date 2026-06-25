package com.pazzioliweb.vendedoresmodule.dtos;

import java.time.LocalDate;

public class VendedorDTO {
	private Integer vendedor_id;
	private String nombre;
	private String direccion;
	private String telefono;
	private String identificacion;
	private String correo;
	private Double comision;
	private Double meta_ventas;
	private String tipo_vendedor;
	private String estado;
	private Integer codigo_usuario_creo;
	private LocalDate fechacreado;
	
	// Campos de usuario y rol
	private Integer usuarioCodigo;
	private String usuarioNombre;
	private String rolNombre;

	// Campo de bodega
	private Integer bodegaId;
	private String bodegaNombre;

	public VendedorDTO(Integer vendedor_id, String nombre, String direccion, String telefono,
	                   String identificacion, String correo, Double comision, Double meta_ventas, String tipo_vendedor,
	                   String estado, Integer codigo_usuario_creo, LocalDate fechacreado,
	                   Integer usuarioCodigo, String usuarioNombre, String rolNombre,
	                   Integer bodegaId, String bodegaNombre) {
		this.vendedor_id = vendedor_id;
		this.nombre = nombre;
		this.direccion = direccion;
		this.telefono = telefono;
		this.identificacion = identificacion;
		this.correo = correo;
		this.comision = comision;
		this.meta_ventas = meta_ventas;
		this.tipo_vendedor = tipo_vendedor;
		this.estado = estado;
		this.codigo_usuario_creo = codigo_usuario_creo;
		this.fechacreado = fechacreado;
		this.usuarioCodigo = usuarioCodigo;
		this.usuarioNombre = usuarioNombre;
		this.rolNombre = rolNombre;
		this.bodegaId = bodegaId;
		this.bodegaNombre = bodegaNombre;
	}

	public Integer getVendedor_id() {
		return vendedor_id;
	}

	public void setVendedor_id(Integer vendedor_id) {
		this.vendedor_id = vendedor_id;
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

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public Double getComision() {
		return comision;
	}

	public void setComision(Double comision) {
		this.comision = comision;
	}

	public Double getMeta_ventas() {
		return meta_ventas;
	}

	public void setMeta_ventas(Double meta_ventas) {
		this.meta_ventas = meta_ventas;
	}

	public String getTipo_vendedor() {
		return tipo_vendedor;
	}

	public void setTipo_vendedor(String tipo_vendedor) {
		this.tipo_vendedor = tipo_vendedor;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getCodigo_usuario_creo() {
		return codigo_usuario_creo;
	}

	public void setCodigo_usuario_creo(Integer codigo_usuario_creo) {
		this.codigo_usuario_creo = codigo_usuario_creo;
	}

	public LocalDate getFechacreado() {
		return fechacreado;
	}

	public void setFechacreado(LocalDate fechacreado) {
		this.fechacreado = fechacreado;
	}

	public Integer getUsuarioCodigo() {
		return usuarioCodigo;
	}

	public void setUsuarioCodigo(Integer usuarioCodigo) {
		this.usuarioCodigo = usuarioCodigo;
	}

	public String getUsuarioNombre() {
		return usuarioNombre;
	}

	public void setUsuarioNombre(String usuarioNombre) {
		this.usuarioNombre = usuarioNombre;
	}

	public String getRolNombre() {
		return rolNombre;
	}

	public void setRolNombre(String rolNombre) {
		this.rolNombre = rolNombre;
	}

	public Integer getBodegaId() {
		return bodegaId;
	}

	public void setBodegaId(Integer bodegaId) {
		this.bodegaId = bodegaId;
	}

	public String getBodegaNombre() {
		return bodegaNombre;
	}

	public void setBodegaNombre(String bodegaNombre) {
		this.bodegaNombre = bodegaNombre;
	}
}
