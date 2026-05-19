package com.pazzioliweb.comprobantesmodule.entity;

import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Tabla maestra de comprobantes contables (numeraciones).
 *
 * Modelo similar a Alegra: una numeración (prefijo + consecutivo) puede
 * estar ASIGNADA A VARIOS CAJEROS. Todos los cajeros asignados a la misma
 * numeración comparten el contador. Si querés numeraciones independientes
 * por cajero, crea un comprobante separado y asígnaselo a un solo cajero.
 *
 * Los comprobantes LEGACY no tienen cajeros asignados — son auto-creados
 * para amarrar registros previos a la implementación del módulo.
 */
@Data
@Entity
@Table(name = "comprobantes_contables")
public class ComprobanteContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 10)
    private TipoMovimientoComprobante tipoMovimiento;

    /** Ej: "FC-1", "VC-1", "CE-2", "LEGACY-FC". El mismo prefijo puede
     *  reutilizarse en distintos tipos (FC-1 para FC y FC-1 para CC), pero
     *  el frontend impide duplicados dentro del MISMO tipo de movimiento. */
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

    /** Comprobantes auto-generados por la migración LEGACY (no editables). */
    @Column(name = "es_legacy", nullable = false)
    private Boolean esLegacy = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /**
     * Cajeros asignados a este comprobante. Modelo N:M: un comprobante puede
     * ser usado por varios cajeros, y un cajero puede tener varios comprobantes
     * (uno por tipo de movimiento, aunque podrían ser compartidos).
     *
     * Se guarda como colección de Integer (no FK a la entidad Cajero) para
     * evitar dependencias circulares entre comprobantesmodule y cajerosmodule.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "comprobante_cajero",
            joinColumns = @JoinColumn(name = "comprobante_id")
    )
    @Column(name = "cajero_id")
    private Set<Integer> cajeroIds = new HashSet<>();
}
