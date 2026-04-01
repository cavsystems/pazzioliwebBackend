package com.pazzioliweb.productosmodule.entity;

import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.commonbacken.entity.Pais;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "bodegas")
@Data
public class Bodegas {
	  	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int codigo;
	     
	   private String nombre;
	   private String codigopostal;
	   private String direccion;
	   private String telefono;
	   private String celular;
	   private String codigosucursal;
	   private String correo;
	   
	   
		 public int getCodigo() {
		return codigo;
	}


	   public void setCodigo(int codigo) {
		   this.codigo = codigo;
	   }


	   public String getNombre() {
		   return nombre;
	   }


	   public void setNombre(String nombre) {
		   this.nombre = nombre;
	   }


	   public String getCodigopostal() {
		   return codigopostal;
	   }


	   public void setCodigopostal(String codigopostal) {
		   this.codigopostal = codigopostal;
	   }


	   public String getDireccion() {
		   return direccion;
	   }


	   public void setDireccion(String direccion) {
		   this.direccion = direccion;
	   }


	   public String getTelefono() {
		   return telefono;
	   }


	   public void setTelefono(String telefono) {
		   this.telefono = telefono;
	   }


	   public String getCelular() {
		   return celular;
	   }


	   public void setCelular(String celular) {
		   this.celular = celular;
	   }


	   public String getCodigosucursal() {
		   return codigosucursal;
	   }


	   public void setCodigosucursal(String codigosucursal) {
		   this.codigosucursal = codigosucursal;
	   }


	   public String getCorreo() {
		   return correo;
	   }


	   public void setCorreo(String correo) {
		   this.correo = correo;
	   }


	   public Pais getCodigopais() {
		   return codigopais;
	   }


	   public void setCodigopais(Pais codigopais) {
		   this.codigopais = codigopais;
	   }


	   public Municipio getCodigomunicipio() {
		   return codigomunicipio;
	   }


	   public void setCodigomunicipio(Municipio codigomunicipio) {
		   this.codigomunicipio = codigomunicipio;
	   }


	   public Departamento getCodigodepartamento() {
		   return codigodepartamento;
	   }


	   public void setCodigodepartamento(Departamento codigodepartamento) {
		   this.codigodepartamento = codigodepartamento;
	   }


		 @ManyToOne
		    @JoinColumn(name = "codigopais")
		    private Pais codigopais  ;
		 
		 @ManyToOne
		 @JoinColumn(name = "codigodepartamento")
	    private Departamento  codigodepartamento;
		 
		 @ManyToOne
		 @JoinColumn(name = "codigomunicipio")
	    private Municipio codigomunicipio;
		 

}
