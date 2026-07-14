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
}
