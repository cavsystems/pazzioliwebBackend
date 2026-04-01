package com.pazzioliweb.usuariosbacken.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "usuarios_clientes")
public class Usuarioclientes {
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	   private int codigo;
	   
	   @Column(nullable = false, length = 100)
	   private String nombre;
	   
	   @Column()
	   private String identificacion; 
	   
	   public String getIdentificacion() {
		return identificacion;
	}

	   public void setIdentificacion(String identificacion) {
		   this.identificacion = identificacion;
	   }

	   @Column(nullable = false, length = 100)
	   private String apellido;
	   
	   
	   @Column(nullable = false, length = 100)
	   private String estado;
	   
	   @Column()
	   private String direccion;
	   
	   @Column()
	   private String  correo;
	   
	   
	   @Column(columnDefinition = "LONGBLOB")
	   private byte[] imagenperfil;
	   
	   @Column()
	   private  String tipoimagen;
	   public String getTipoimagen() {
		return tipoimagen;
	}

	   public void setTipoimagen(String tipoimagen) {
		   this.tipoimagen = tipoimagen;
	   }

	   public byte[] getImagenperfil() {
		return imagenperfil;
	}

	   public void setImagenperfil(byte[] imagenperfil) {
		   this.imagenperfil = imagenperfil;
	   }

	   public String getCorreo() {
		return correo;
	}

	   public void setCorreo(String correo) {
		   this.correo = correo;
	   }

	   public String getDireccion() {
		return direccion;
	}

	   public void setDireccion(String direccion) {
		   this.direccion = direccion;
	   }

	   @Column()
	   private LocalDate fechacreado;
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

	   public String getApellido() {
		   return apellido;
	   }

	   public void setApellido(String apellido) {
		   this.apellido = apellido;
	   }

	   public String getEstado() {
		   return estado;
	   }

	   public void setEstado(String estado) {
		   this.estado = estado;
	   }

	   public LocalDate getFechacreado() {
		   return fechacreado;
	   }

	   public void setFechacreado(LocalDate fechacreado) {
		   this.fechacreado = fechacreado;
	   }

	   public LocalDate getFechamodificado() {
		   return fechamodificado;
	   }

	   public void setFechamodificado(LocalDate fechamodificado) {
		   this.fechamodificado = fechamodificado;
	   }

	   public Usuario getCodigousuariocreado() {
		   return codigousuariocreado;
	   }

	   public void setCodigousuariocreado(Usuario codigousuariocreado) {
		   this.codigousuariocreado = codigousuariocreado;
	   }

		@Column()
	    private LocalDate fechamodificado;
	   @ManyToOne
	    @JoinColumn(name = "codigousuariocreado")
	   private Usuario codigousuariocreado;
	   @PreUpdate
	   public void preUpdate() {
	       this.fechamodificado = LocalDate.now();
	   }

	
	   // Se ejecuta automáticamente antes de insertar en BD
	    @PrePersist
	    public void prePersist() {
	        this.fechacreado = LocalDate.now(); // Solo fecha
	        this.fechamodificado=LocalDate.parse("1990-06-06"); // Convertir String a LocalDate;
	    }
	   
	   
	
}
