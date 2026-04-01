package com.pazzioliweb.movimientosinventariomodule.entity;

import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "movimientos_inventario_detalles")
public class MovimientoInventarioDetalle {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="movimiento_inventario_detalle_id")
    private Long detalleId;

    @ManyToOne
    @JoinColumn(name = "movimiento_inventario_id", nullable = false)
    private MovimientoInventario movimiento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_variante_id")
    private ProductoVariante productoVariante;

    @ManyToOne
    @JoinColumn(name = "bodega_origen_id")
    private Bodegas bodegaOrigen;

    @ManyToOne
    @JoinColumn(name = "bodega_destino_id")
    private Bodegas bodegaDestino;

    @Column(nullable = false)
    private Double cantidad;

    @Column(name = "costo_unitario", nullable = false)
    private Double costoUnitario;

    @Column(name = "costo_promedio", nullable = false)
    private Double costoPromedio;

    @Column(name = "total_detalle", nullable = false)
    private Double totalDetalle;

	public Long getDetalleId() {
		return detalleId;
	}

	public void setDetalleId(Long detalleId) {
		this.detalleId = detalleId;
	}

	public MovimientoInventario getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(MovimientoInventario movimiento) {
		this.movimiento = movimiento;
	}

	public ProductoVariante getProductoVariante() {
		return productoVariante;
	}

	public void setProductoVariante(ProductoVariante productoVariante) {
		this.productoVariante = productoVariante;
	}

	public Bodegas getBodegaOrigen() {
		return bodegaOrigen;
	}

	public void setBodegaOrigen(Bodegas bodegaOrigen) {
		this.bodegaOrigen = bodegaOrigen;
	}

	public Bodegas getBodegaDestino() {
		return bodegaDestino;
	}

	public void setBodegaDestino(Bodegas bodegaDestino) {
		this.bodegaDestino = bodegaDestino;
	}

	public Double getCantidad() {
		return cantidad;
	}

	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	public Double getCostoUnitario() {
		return costoUnitario;
	}

	public void setCostoUnitario(Double costoUnitario) {
		this.costoUnitario = costoUnitario;
	}

	public Double getCostoPromedio() {
		return costoPromedio;
	}

	public void setCostoPromedio(Double costoPromedio) {
		this.costoPromedio = costoPromedio;
	}

	public Double getTotalDetalle() {
		return totalDetalle;
	}

	public void setTotalDetalle(Double totalDetalle) {
		this.totalDetalle = totalDetalle;
	}
    
}
