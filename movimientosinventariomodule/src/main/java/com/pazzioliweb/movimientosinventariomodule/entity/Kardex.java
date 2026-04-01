package com.pazzioliweb.movimientosinventariomodule.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pazzioliweb.movimientosinventariomodule.enums.EstadoMovimiento;
import com.pazzioliweb.movimientosinventariomodule.enums.TipoMovimiento;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "kardex")
public class Kardex {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "kardex_id")
    private Long kardexId;

    // Movimiento padre
    @ManyToOne(optional = false)
    @JoinColumn(name = "movimiento_inventario_id")
    private MovimientoInventario movimiento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "movimiento_inventario_detalle_id")
    private MovimientoInventarioDetalle detalle;

    // Variante del producto
    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_variante_id")
    private ProductoVariante productoVariante;

    // Bodega afectada
    @ManyToOne(optional = false)
    @JoinColumn(name = "bodega_id")
    private Bodegas bodega;

    // Fechas
    @Column(name ="fecha_emision", nullable = false)
    private LocalDate fechaEmision; // Fecha contable del movimiento

    @Column(name ="fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion; // Registro en el sistema

    // Movimientos de cantidades
    @Column(nullable = false)
    private Double entrada = 0.0;

    @Column(nullable = false)
    private Double salida = 0.0;

    @Column(nullable = false)
    private Double saldo = 0.0; // saldo resultante después del movimiento

    // Costos
    @Column(name = "costo_unitario", nullable = false)
    private Double costoUnitario; // costo del detalle

    @Column(name = "costo_promedio", nullable = false)
    private Double costoPromedio; // costo promedio recalculado

    @Column(name = "total_costo", nullable = false)
    private Double totalCosto; // entrada*unitario o salida*promedio

    // Tipo de movimiento (reflejo del MovimientoInventario)
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipo;

    // Estado del registro
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_movimiento",nullable = false)
    private EstadoMovimiento estado;

    // Observaciones opcionales
    private String observaciones;

	public Long getKardexId() {
		return kardexId;
	}

	public void setKardexId(Long kardexId) {
		this.kardexId = kardexId;
	}

	public MovimientoInventario getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(MovimientoInventario movimiento) {
		this.movimiento = movimiento;
	}

	public MovimientoInventarioDetalle getDetalle() {
		return detalle;
	}

	public void setDetalle(MovimientoInventarioDetalle detalle) {
		this.detalle = detalle;
	}

	public ProductoVariante getProductoVariante() {
		return productoVariante;
	}

	public void setProductoVariante(ProductoVariante productoVariante) {
		this.productoVariante = productoVariante;
	}
	
	// Esta bodega saldrá de bodegaOrigen o bodegaDestino según el tipo de movimiento
	public Bodegas getBodega() {
		return bodega;
	}

	public void setBodega(Bodegas bodega) {
		this.bodega = bodega;
	}

	public LocalDate getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(LocalDate fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Double getEntrada() {
		return entrada;
	}

	public void setEntrada(Double entrada) {
		this.entrada = entrada;
	}

	public Double getSalida() {
		return salida;
	}

	public void setSalida(Double salida) {
		this.salida = salida;
	}

	public Double getSaldo() {
		return saldo;
	}

	public void setSaldo(Double saldo) {
		this.saldo = saldo;
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

	public Double getTotalCosto() {
		return totalCosto;
	}

	public void setTotalCosto(Double totalCosto) {
		this.totalCosto = totalCosto;
	}

	public TipoMovimiento getTipo() {
		return tipo;
	}

	public void setTipo(TipoMovimiento tipo) {
		this.tipo = tipo;
	}

	public EstadoMovimiento getEstado() {
		return estado;
	}

	public void setEstado(EstadoMovimiento estado) {
		this.estado = estado;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
    
}