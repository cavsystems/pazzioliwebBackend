package com.pazzioliweb.comprobantesmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Detalle de la depreciación aplicada a un activo en un periodo (mes/año).
 * Permite reversar con exactitud la corrida de un periodo (para regenerarla)
 * y saber qué activos ya fueron depreciados en cada mes.
 */
@Entity
@Table(name = "depreciacion_detalle",
        indexes = { @Index(name = "idx_dep_periodo", columnList = "anio,mes") })
@Data
public class DepreciacionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activo_id", nullable = false)
    private Long activoId;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal cuota = BigDecimal.ZERO;

    @Column(name = "numero_asiento", length = 40)
    private String numeroAsiento;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();
}
