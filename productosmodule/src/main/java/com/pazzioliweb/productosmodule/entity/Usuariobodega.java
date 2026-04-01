package com.pazzioliweb.productosmodule.entity;

import com.pazzioliweb.usuariosbacken.entity.Usuario;

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
@Data
@Table(name = "usuariobodega")
public class Usuariobodega {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_usuariobodega") // Sigue relacionando con la BD ✅
	private int idUsuariobodega;
	
	@ManyToOne
    @JoinColumn(name = "usuarioid")
    private Usuario usuarioid;
	
	@ManyToOne
    @JoinColumn(name = "bodegaid")
    private Bodegas bodegaid;
	
	public void setIddUsuariobodega(int id_usuariobodega ) {
		this.idUsuariobodega= id_usuariobodega;
	}
	
	public void setUsuarioid(Usuario usuarioid ) {
		this.usuarioid=usuarioid;
	}
	
	public void setBodegaid(Bodegas bodegaid ) {
		this.bodegaid= bodegaid;
	}
	
	

}
