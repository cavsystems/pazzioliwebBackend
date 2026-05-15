package com.pazzioliweb.comprobantesmodule.entity;

import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Tabla maestra de comprobantes contables.
 * Cada combinación (cajero × tipo de movimiento) tiene un comprobante propio
 * con su prefijo y consecutivo. Todo movimiento del sistema (venta, compra,
 * recibo, egreso, devolución) queda amarrado al comprobante usado para
 * generar su número.
 *
 * Los comprobantes LEGACY tienen cajero null y son creados automáticamente
 * para amarrar registros existentes antes de la migración.
 */
@Data
@Entity
@Table(
    name = "comprobantes_contables",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_comp_cajero_tipo", columnNames = {"cajero_id", "tipo_movimiento"})
    }
)
public class ComprobanteContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Cajero al que pertenece este comprobante. NULL solo para LEGACY del sistema.
     * Se guarda como Integer plano (no FK relacional) para evitar dependencias circulares
     * entre módulos. La integridad referencial se valida a nivel de servicio.
     */
    @Column(name = "cajero_id")
    private Integer cajeroId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 10)
    private TipoMovimientoComprobante tipoMovimiento;

    /** Ej: "FC-1", "VC-1", "CE-2", "LEGACY-FC" */
    @Column(name = "prefijo", nullable = false, length = 20)
    private String prefijo;

    @Column(name = "descripcion", length = 150)
    private String descripcion;

    /** Próximo consecutivo a usar (se incrementa al asignar). */
    @Column(name = "siguiente_consecutivo", nullable = false)
    private Integer siguienteConsecutivo = 1;

    /** Cuenta contable asociada (opcional, para contabilización automática). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_contable_id")
    private CuentaContable cuentaContable;

    /** Si afecta el inventario al ejecutar el movimiento. */
    @Column(name = "afecta_inventario", nullable = false)
    private Boolean afectaInventario = true;

    /** Indica si está activo y disponible para asignar a movimientos nuevos. */
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /**
     * Marca comprobantes creados automáticamente por la migración inicial.
     * Estos amarran los registros que existían antes de implementar el módulo.
     */
    @Column(name = "es_legacy", nullable = false)
    private Boolean esLegacy = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
