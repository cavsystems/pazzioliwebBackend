package com.pazzioliweb.usuariosbacken.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigo;
   




	@Column(nullable = false, length = 50)
       private String nombre;

    @Column(nullable = false, unique = true, length = 50)
    private String usuario;

    @Column(nullable = false)
    private String contrasena;

    @Column(length = 20)
    private String estado ;
    
    @ManyToOne
    @JoinColumn(name = "codigorol")
    private Roles codigorol;
    
    public void setCodigorol(Roles codigorol) {
		this.codigorol = codigorol;
	}


	@Column(length=20)
    private int codigousuariocreado;
       
    @Column()
    private LocalDate fechacreado;
    
    public LocalDate getFechacreado() {
		return fechacreado;
	}


	public void setFechacreado(LocalDate fechacreado) {
		this.fechacreado = fechacreado;
	}


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


	public String getUsuario() {
		return usuario;
	}


	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}


	public String getContrasena() {
		return contrasena;
	}


	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}


	public String getEstado() {
		return estado;
	}


	public void setEstado(String estado) {
		this.estado = estado;
	}





	public Roles getCodigorol() {
		return codigorol;
	}


	public int getCodigousuariocreado() {
		return codigousuariocreado;
	}


	public void setCodigousuariocreado(int codigousuariocreado) {
		this.codigousuariocreado = codigousuariocreado;
	}




	

	public LocalDate getFechamodificado() {
		return fechamodificado;
	}


	public void setFechamodificado(LocalDate fechamodificado) {
		this.fechamodificado = fechamodificado;
	}


	@Column()
    private LocalDate fechamodificado;

    @Lob
    @Column(name = "avatar", columnDefinition = "LONGBLOB")
    private byte[] avatar;

    @Column(name = "avatar_tipo", length = 100)
    private String avatarTipo;


    // Se ejecuta automáticamente antes de insertar en BD
    @PrePersist
    public void prePersist() {
        this.fechacreado = LocalDate.now(); // Solo fecha
        this.fechamodificado=LocalDate.parse("1990-06-06"); // Convertir String a LocalDate;
    }
    
    
   
    
    
   
}