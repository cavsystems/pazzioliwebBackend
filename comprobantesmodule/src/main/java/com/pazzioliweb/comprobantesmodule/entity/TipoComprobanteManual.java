package com.pazzioliweb.comprobantesmodule.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Catálogo de tipos de comprobante MANUAL (notas de contabilidad,
 * depreciaciones, ajustes, etc.). Separado del enum transaccional
 * TipoMovimientoComprobante: estos no tienen cajero, DIAN ni inventario;
 * se usan para numerar asientos contables manuales.
 */
@Entity
@Table(name = "tipos_comprobante_manual")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TipoComprobanteManual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "codigo", nullable = false, length = 10)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "prefijo", length = 20)
    private String prefijo;

    @Column(name = "siguiente_consecutivo", nullable = false)
    private Integer siguienteConsecutivo = 1;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado = "ACTIVO";

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
