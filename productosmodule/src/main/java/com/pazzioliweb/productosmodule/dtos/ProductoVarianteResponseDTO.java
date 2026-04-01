package com.pazzioliweb.productosmodule.dtos;

public class ProductoVarianteResponseDTO {
	private Long productoVarianteId;
    private Integer productoId;
    private String sku;
    private String referenciaVariantes;
    private String codigoBarras;
    private Boolean activo;
    private Boolean predeterminada;
    private String estado;
    
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public Long getProductoVarianteId() {
		return productoVarianteId;
	}
	public void setProductoVarianteId(Long productoVarianteId) {
		this.productoVarianteId = productoVarianteId;
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
    
}
