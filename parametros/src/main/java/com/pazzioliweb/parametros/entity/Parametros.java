package com.pazzioliweb.parametros.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parametros")
public class Parametros {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   @Column(name = "nombre",nullable = false)
    private String nombre;
   @Column(name="categoriaParametro")
   private String categoriaparametro;
   @Column(name="categoriaComprobante")
    private String  categoriacomprobante;
   @Column(name="estado")
   private String valor;
    @Column(name="tipoDato")
    private String tipodato;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoriaparametro() {
        return categoriaparametro;
    }

    public void setCategoriaparametro(String categoriaparametro) {
        this.categoriaparametro = categoriaparametro;
    }

    public String getCategoriacomprobante() {
        return categoriacomprobante;
    }

    public void setCategoriacomprobante(String categoriacomprobante) {
        this.categoriacomprobante = categoriacomprobante;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTipodato() {
        return tipodato;
    }

    public void setTipodato(String tipodato) {
        this.tipodato = tipodato;
    }

}
