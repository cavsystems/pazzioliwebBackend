package com.pazzioliweb.vendedoresmodule.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "vendedores")
@Data
public class Vendedores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendedor_id") // Ojo: aquí respeto tu tabla, aunque parezca un error
    private Integer vendedor_id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "direccion", nullable = false, length = 50)
    private String direccion;

    @Column(name = "telefono", nullable = false, length = 50)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private Estado estado = Estado.ACTIVO;

    @Column(name = "codigo_usuario_creo", nullable = false)
    private Integer codigo_usuario_creo = 0;

    @Column(name = "fechacreado", nullable = false, updatable = false)
    private LocalDate fechacreado = LocalDate.now();

    public enum Estado {
        ACTIVO, INACTIVO
    }

    
    public Integer getVendedor_id() {
        return vendedor_id;
    }

    public void setVendedor_id(Integer vendedor_id) {
        this.vendedor_id = vendedor_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Integer getCodigo_usuario_creo() {
        return codigo_usuario_creo;
    }

    public void setCodigo_usuario_creo(Integer codigo_usuario_creo) {
        this.codigo_usuario_creo = codigo_usuario_creo;
    }

    public LocalDate getFechacreado() {
        return fechacreado;
    }

    public void setFechacreado(LocalDate fechacreado) {
        this.fechacreado = fechacreado;
    }
}
