package com.pazzioliweb.comprasmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Configuración global del módulo de compras. Tabla single-row (id=1).
 * Hoy guarda únicamente el cajero por defecto usado para asignar comprobantes
 * de compra (CC/CR) cuando el usuario que realiza la operación no es cajero
 * o su cajero no tiene comprobantes de compra configurados.
 */
@Entity
@Table(name = "configuracion_compras")
@Data
public class ConfiguracionCompras {

    @Id
    @Column(name = "id")
    private Integer id = 1;

    /** Cajero default para asignar comprobantes de compra cuando el usuario no es cajero. */
    @Column(name = "cajero_default_id")
    private Integer cajeroDefaultId;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
