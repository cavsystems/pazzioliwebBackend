package com.pazzioliweb.comprobantesmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categorias_comprobantes")
@Data
public class CategoriasComprobantes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_comprobante_id")
    private Integer categoria_comprobante_id;

    @Column(name = "nombre", length = 100)
    private String nombre;
}
