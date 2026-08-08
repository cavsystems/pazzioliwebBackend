package com.pazzioliweb.commonbacken.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Notificación visible en la campana del navbar. Vive en el schema de cada
 * tenant (como el resto de tablas de negocio) — no distingue destinatario por
 * usuario, es visible para todos los usuarios de esa empresa (mismo criterio
 * simple que ya usa el resto de la UI para permisos por módulo, no por persona).
 */
@Entity
@Table(name = "notificaciones")
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ej: FACTURA_RECHAZADA, NC_PENDIENTE, NC_RECHAZADA, CARTERA_VENCIDA, LICENCIA_POR_VENCER. */
    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "mensaje", nullable = false, length = 500)
    private String mensaje;

    /** Ej: FACTURA, DEVOLUCION, CUENTA_COBRAR, CUENTA_PAGAR, EMPRESA. Null si no aplica. */
    @Column(name = "entidad_tipo", length = 30)
    private String entidadTipo;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(name = "leida", nullable = false)
    private Boolean leida = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_leida")
    private LocalDateTime fechaLeida;
}
