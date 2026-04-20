package com.pazzioliweb.productosmodule.dtos;

public class ProductoVarianteUpdateDTO {
	private String sku;
    private String referenciaVariantes;
    private String codigoBarras;
    private Boolean activo;
    private Boolean predeterminada;
    private Boolean   estadovariante;
    private String imagen;
	public String getImagen() {
		return imagen;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getReferenciaVariantes() {
		return referenciaVariantes;
	}
	public void setReferenciaVariantes(String referenciaVariantes) {
		this.referenciaVariantes = referenciaVariantes;
	}
	public String getCodigoBarras() {
		return codigoBarras;
	}
	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}
	public Boolean getActivo() {
		return activo;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	public Boolean getPredeterminada() {
		return predeterminada;
	}
	public void setPredeterminada(Boolean predeterminada) {
		this.predeterminada = predeterminada;
	}
	public Boolean getEstadovariante() {
		return estadovariante;
	}
	public void setEstadovariante(Boolean estadovariante) {
		this.estadovariante = estadovariante;
	}
    
    
}
