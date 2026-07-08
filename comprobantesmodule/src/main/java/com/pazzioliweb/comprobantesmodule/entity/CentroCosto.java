package com.pazzioliweb.comprobantesmodule.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Catálogo de centros de costo. Se usa para clasificar egresos/recibos y,
 * a futuro, líneas de asiento. Tabla legacy `centrocosto` (codigo, nombre).
 */
@Entity
@Table(name = "centrocosto")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CentroCosto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @Column(name = "nombre", length = 500)
    private String nombre;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado = "ACTIVO";
}
