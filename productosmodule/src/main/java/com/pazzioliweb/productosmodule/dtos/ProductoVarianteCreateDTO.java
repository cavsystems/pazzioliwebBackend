package com.pazzioliweb.productosmodule.dtos;

public class ProductoVarianteCreateDTO {
	private Integer productoId;
    private String sku;
    private String referenciaVariantes;
    private String codigoBarras;
    private Boolean predeterminada;
    private Boolean   estadovariante;
    
	public Boolean getEstadovariante() {
		return estadovariante;
	}
	public void setEstadovariante(Boolean estadovariante) {
		this.estadovariante = estadovariante;
	}
	public Boolean getPredeterminada() {
		return predeterminada;
	}
	public void setPredeterminada(Boolean predeterminada) {
		this.predeterminada = predeterminada;
	}
	public Integer getProductoId() {
		return productoId;
	}
	public void setProductoId(Integer productoId) {
		this.productoId = productoId;
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
    
    
}
