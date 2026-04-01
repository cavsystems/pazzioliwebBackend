package com.pazzioliweb.productosmodule.entity;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto_variantes")
public class ProductoVariante {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "producto_variantes_id")
    private Long productoVarianteId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "producto_id")
	private Productos producto;
	
	@Column(unique = true)
	private String sku;
	
	@Column(name = "referencia_variantes")
	private String referenciaVariantes;

	
	@Column(name = "codigo_barras",unique = true)
	private String codigoBarras;
	private BigDecimal precio;
	private Boolean activo = true;
	
	@OneToMany(mappedBy = "productoVariante", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductoVarianteDetalle> detalles = new ArrayList<>();
	
	@OneToMany(mappedBy = "productoVariante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Existencias> existencias = new ArrayList<>();
	
	@OneToMany(mappedBy = "productoVariante", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PreciosProductoVariante> precios = new ArrayList<>();
	
	private Boolean predeterminada = false;
	
	public Long getProductoVarianteId() {
		return productoVarianteId;
	}

	public void setProductoVarianteId(Long productoVarianteId) {
		this.productoVarianteId = productoVarianteId;
	}

	public Productos getProducto() {
		return producto;
	}

	public void setProducto(Productos producto) {
		this.producto = producto;
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

	public List<ProductoVarianteDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<ProductoVarianteDetalle> detalles) {
		this.detalles = detalles;
	}
	

	public List<Existencias> getExistencias() {return existencias;}
    public void setExistencias(List<Existencias> existencias) {this.existencias = existencias;}

	public Boolean getPredeterminada() {
		return predeterminada;
	}

	public void setPredeterminada(Boolean predeterminada) {
		this.predeterminada = predeterminada;
	}

	public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}
    
    
}
