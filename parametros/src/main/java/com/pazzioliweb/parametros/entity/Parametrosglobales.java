package com.pazzioliweb.parametros.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parametrosglobales")
public class Parametrosglobales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "parametroid")
    private Parametros parametros;

    @Column(name = "valor", nullable = false)
    private String valor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Parametros getParametros() {
        return parametros;
    }

    public void setParametros(Parametros parametros) {
        this.parametros = parametros;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
