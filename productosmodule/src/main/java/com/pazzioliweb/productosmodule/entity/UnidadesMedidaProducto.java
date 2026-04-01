package com.pazzioliweb.productosmodule.entity;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "unidades_medida_producto")
public class UnidadesMedidaProducto {
	
	// llave primaria compuesta
	@EmbeddedId
    private UnidadesMedidaProductoId id;
	
	//Relacion conn productos
    @ManyToOne
    @MapsId("productoId") // Mapea el productoId de la PK
    @JoinColumn(name = "producto_id", nullable = false)
    private Productos producto;
    
    //Relacion con UnidadesMedida
    @ManyToOne
    @MapsId("unidadMedidaId") // Mapea el unidadMedidaId de la PK
    @JoinColumn(name = "unidad_medida_id", nullable = false)
    private UnidadesMedida unidadMedida;

	public UnidadesMedidaProductoId getId() {
		return id;
	}

	public void setId(UnidadesMedidaProductoId id) {
		this.id = id;
	}

	public Productos getProducto() {
		return producto;
	}

	public void setProducto(Productos producto) {
		this.producto = producto;
	}

	public UnidadesMedida getUnidadMedida() {
		return unidadMedida;
	}

	public void setUnidadMedida(UnidadesMedida unidadMedida) {
		this.unidadMedida = unidadMedida;
	}
    
    
}
