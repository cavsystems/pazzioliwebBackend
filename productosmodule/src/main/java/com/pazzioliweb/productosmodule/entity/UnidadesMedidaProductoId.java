package com.pazzioliweb.productosmodule.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UnidadesMedidaProductoId implements Serializable{
	 @Column(name = "producto_id")
	    private int productoId;

	    @Column(name = "unidad_medida_id")
	    private int unidadMedidaId;

	    // Constructor vacío
	    public UnidadesMedidaProductoId() {}

	    // Constructor con parámetros
	    public UnidadesMedidaProductoId(int productoId, int unidadMedidaId) {
	        this.productoId = productoId;
	        this.unidadMedidaId = unidadMedidaId;
	    }

	    // Getters y setters
	    public int getProductoId() { return productoId; }
	    public void setProductoId(int productoId) { this.productoId = productoId; }

	    public int getUnidadMedidaId() { return unidadMedidaId; }
	    public void setUnidadMedidaId(int unidadMedidaId) { this.unidadMedidaId = unidadMedidaId; }

	    // equals y hashCode son obligatorios
	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof UnidadesMedidaProductoId)) return false;
	        UnidadesMedidaProductoId that = (UnidadesMedidaProductoId) o;
	        return productoId == that.productoId &&
	               unidadMedidaId == that.unidadMedidaId;
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(productoId, unidadMedidaId);
	    }
}
