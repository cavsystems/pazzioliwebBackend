package com.pazzioliweb.comprobantesmodule.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/** Nota a los estados financieros (revelación). Texto libre por año/número. */
@Entity
@Table(name = "notas_estados_financieros")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NotaEstadoFinanciero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "numero", nullable = false)
    private Integer numero = 1;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "contenido", columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado = "ACTIVO";

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
