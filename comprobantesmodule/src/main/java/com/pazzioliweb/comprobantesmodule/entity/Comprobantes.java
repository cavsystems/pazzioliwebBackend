package com.pazzioliweb.comprobantesmodule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "comprobantes")
@Data
public class Comprobantes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comprobante_id")
    private Integer comprobante_id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "categoria_comprobante_id", nullable = false)
    private CategoriasComprobantes categoriaComprobante;

    @Column(name = "inicio_consecutivo")
    private Integer inicio_consecutivo;

    @Column(name = "afecta_inventario", columnDefinition = "ENUM('SI','NO') default 'SI'")
    private String afecta_inventario;

    public Comprobantes() {
    }
    
	public Integer getComprobante_id() {
		return comprobante_id;
	}

	public void setComprobante_id(Integer comprobanteId) {
		this.comprobante_id = comprobanteId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public CategoriasComprobantes getCategoriaComprobante() {
		return categoriaComprobante;
	}

	public void setCategoriaComprobante(CategoriasComprobantes categoriaComprobante) {
		this.categoriaComprobante = categoriaComprobante;
	}

	public Integer getInicio_consecutivo() {
		return inicio_consecutivo;
	}

	public void setInicio_consecutivo(Integer inicioConsecutivo) {
		this.inicio_consecutivo = inicioConsecutivo;
	}

	public String getAfecta_inventario() {
		return afecta_inventario;
	}

	public void setAfecta_inventario(String afectaInventario) {
		this.afecta_inventario = afectaInventario;
	}
    
    
}
