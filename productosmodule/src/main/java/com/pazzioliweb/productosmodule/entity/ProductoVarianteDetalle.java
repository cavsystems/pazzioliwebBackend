package com.pazzioliweb.productosmodule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto_variantes_detalle")
public class ProductoVarianteDetalle {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "producto_variantes_detalle_id")
	private Long productoVariantesDetalleId;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "producto_variantes_id")
	private ProductoVariante productoVariante;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "caracteristica_id")
	private Caracteristica caracteristica;


	public Long getProductoVariantesDetalleId() {
		return productoVariantesDetalleId;
	}


	public void setProductoVariantesDetalleId(Long productoVariantesDetalleId) {
		this.productoVariantesDetalleId = productoVariantesDetalleId;
	}


	public ProductoVariante getProductoVariante() {
		return productoVariante;
	}


	public void setProductoVariante(ProductoVariante productoVariante) {
		this.productoVariante = productoVariante;
	}


	public Caracteristica getCaracteristica() {
		return caracteristica;
	}


	public void setCaracteristica(Caracteristica caracteristica) {
		this.caracteristica = caracteristica;
	}
	
	
}
