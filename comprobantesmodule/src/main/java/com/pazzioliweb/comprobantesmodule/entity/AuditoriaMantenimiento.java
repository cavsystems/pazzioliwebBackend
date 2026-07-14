package com.pazzioliweb.comprobantesmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Registro de auditoría de las herramientas de mantenimiento de documentos
 * (duplicar, cambio masivo de cuenta, renumerar). Deja rastro de quién y cuándo.
 */
@Entity
@Table(name = "auditoria_mantenimiento")
@Data
public class AuditoriaMantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operacion", nullable = false, length = 40)
    private String operacion;

    @Column(name = "detalle", length = 500)
    private String detalle;

    @Column(name = "usuario", length = 100)
    private String usuario;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora = LocalDateTime.now();
}
