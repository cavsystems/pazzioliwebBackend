package com.pazzioliweb.productosmodule.dtos;

public class UnidadMedidaProductoResponseDTO {

    private Integer productoId;
    private Integer unidadMedidaId;

    private String nombreProducto;
    private String nombreUnidadMedida;
    private String siglaUnidadMedida;
	public Integer getProductoId() {
		return productoId;
	}
	public void setProductoId(Integer productoId) {
		this.productoId = productoId;
	}
	public Integer getUnidadMedidaId() {
		return unidadMedidaId;
	}
	public void setUnidadMedidaId(Integer unidadMedidaId) {
		this.unidadMedidaId = unidadMedidaId;
	}
	public String getNombreProducto() {
		return nombreProducto;
	}
	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}
	public String getNombreUnidadMedida() {
		return nombreUnidadMedida;
	}
	public void setNombreUnidadMedida(String nombreUnidadMedida) {
		this.nombreUnidadMedida = nombreUnidadMedida;
	}
	public String getSiglaUnidadMedida() {
		return siglaUnidadMedida;
	}
	public void setSiglaUnidadMedida(String siglaUnidadMedida) {
		this.siglaUnidadMedida = siglaUnidadMedida;
	}
    
    
}
