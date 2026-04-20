package com.pazzioliweb.productosmodule.dtos;

import java.util.List;

public class ProductoUpdateDTO {
	private String referencia;
    private String descripcion;
    private String codigo_contable;
    private String codigo_barras;
    private Double costo;
    private String manifiesto;

    private Integer grupo_id;
    private Integer linea_id;
    private Integer impuesto_id;
    private List<Integer> unidadesMedida;
    private Integer tipo_producto_id;
    private String estado;
    private Boolean manejaVariantes;
    private String imagen;
    
	public String getImagen() {
		return imagen;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getCodigo_contable() {
		return codigo_contable;
	}
	public void setCodigo_contable(String codigo_contable) {
		this.codigo_contable = codigo_contable;
	}
	public String getCodigo_barras() {
		return codigo_barras;
	}
	public void setCodigo_barras(String codigo_barras) {
		this.codigo_barras = codigo_barras;
	}
	public Double getCosto() {
		return costo;
	}
	public void setCosto(Double costo) {
		this.costo = costo;
	}
	public String getManifiesto() {
		return manifiesto;
	}
	public void setManifiesto(String manifiesto) {
		this.manifiesto = manifiesto;
	}
	public Integer getGrupo_id() {
		return grupo_id;
	}
	public void setGrupo_id(Integer grupo_id) {
		this.grupo_id = grupo_id;
	}
	public Integer getLinea_id() {
		return linea_id;
	}
	public void setLinea_id(Integer linea_id) {
		this.linea_id = linea_id;
	}
	public Integer getImpuesto_id() {
		return impuesto_id;
	}
	public void setImpuesto_id(Integer impuesto_id) {
		this.impuesto_id = impuesto_id;
	}
	public List<Integer> getUnidadesMedida() {
		return unidadesMedida;
	}
	public void setUnidadesMedida(List<Integer> unidadesMedida) {
		this.unidadesMedida = unidadesMedida;
	}
	public Boolean getManejaVariantes() {
		return manejaVariantes;
	}
	public void setManejaVariantes(Boolean manejaVariantes) {
		this.manejaVariantes = manejaVariantes;
	}
	public Integer getTipo_producto_id() {
		return tipo_producto_id;
	}
	public void setTipo_producto_id(Integer tipo_producto_id) {
		this.tipo_producto_id = tipo_producto_id;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
    
}
