package com.pazzioliweb.usuariosbacken.entity;

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
@Table(name = "usuarios_clientes_usuarios")
public class Usuarioclientesusuario {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int codigo;
	   
	   public int getCodigo() {
		return codigo;
	}

	 public void setCodigo(int codigo) {
		 this.codigo = codigo;
	 }

	 public Usuario getCodigousuario() {
		 return codigousuario;
	 }

	 public void setCodigousuario(Usuario codigousuario) {
		 this.codigousuario = codigousuario;
	 }

	 public Usuarioclientes getCodigocliente() {
		 return codigocliente;
	 }

	 public void setCodigocliente(Usuarioclientes codigocliente) {
		 this.codigocliente = codigocliente;
	 }

	   @ManyToOne(fetch = FetchType.EAGER)
	    @JoinColumn(name = "codigousuario")
	   private Usuario codigousuario;
	   
	   @ManyToOne
	    @JoinColumn(name = "codigocliente")
	   private  Usuarioclientes codigocliente;
}
