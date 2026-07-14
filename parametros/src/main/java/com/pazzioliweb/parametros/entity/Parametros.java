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

}
