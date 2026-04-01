package com.pazzioliweb.productosmodule.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "precios_producto_variante")
public class PreciosProductoVariante {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "precios_producto_id")
	private Integer preciosProductoId;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_variantes_id", referencedColumnName = "producto_variantes_id", nullable = false)
    private ProductoVariante productoVariante;

    @ManyToOne
    @JoinColumn(name = "precio_id", nullable = false)
    private Precios precio;

    @Column(nullable = false)
    private double valor;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

	public Integer getPreciosProductoId() {
		return preciosProductoId;
	}

	public void setPreciosProductoId(Integer preciosProductoId) {
		this.preciosProductoId = preciosProductoId;
	}

	public ProductoVariante getProductoVariante() {
		return productoVariante;
	}

	public void setProductoVariante(ProductoVariante productoVariante) {
		this.productoVariante = productoVariante;
	}

	public Precios getPrecio() {
		return precio;
	}

	public void setPrecio(Precios precio) {
		this.precio = precio;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public LocalDateTime getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(LocalDateTime fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDateTime fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDateTime fechaFin) {
		this.fechaFin = fechaFin;
	}
}
