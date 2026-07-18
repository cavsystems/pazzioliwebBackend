package com.pazzioliweb.comprobantesmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * modoPOS — configuración de la CAPA CONTABLE por empresa (tenant). Tabla single-row (id=1).
 *
 * Si {@code contabilidadActiva} es false, la empresa opera en modo POS puro: NO se generan
 * asientos ni se gestionan periodos. Se mantienen numeración, comprobantes, DIAN, cajeros,
 * inventario, caja y cartera. Default = true (comportamiento actual).
 */
@Entity
@Table(name = "configuracion_contabilidad")
@Data
public class ConfiguracionContabilidad {

    @Id
    @Column(name = "id")
    private Integer id = 1;

    /** true = lleva contabilidad (actual); false = modo POS puro (sin asientos ni periodos). */
    @Column(name = "contabilidad_activa")
    private Boolean contabilidadActiva = true;

    /** Fecha de corte: solo se contabilizan documentos con fecha >= esta. null = sin corte. */
    @Column(name = "contabilidad_desde")
    private LocalDate contabilidadDesde;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
