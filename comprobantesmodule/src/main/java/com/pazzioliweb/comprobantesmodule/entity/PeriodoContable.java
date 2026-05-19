package com.pazzioliweb.comprobantesmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Cierre contable mensual. Cuando un periodo está CERRADO, no se pueden
 * registrar nuevos asientos contables con fecha dentro de él. Esto
 * protege la integridad de los reportes ya entregados a Hacienda /
 * a la auditoría.
 *
 * Reabrir un periodo cerrado requiere acción manual del admin.
 */
@Entity
@Table(name = "periodos_contables")
@Data
public class PeriodoContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    /** Mes (1-12). */
    @Column(name = "mes", nullable = false)
    private Integer mes;

    /** ABIERTO o CERRADO. */
    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "ABIERTO";

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "usuario_cierre", length = 80)
    private String usuarioCierre;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
