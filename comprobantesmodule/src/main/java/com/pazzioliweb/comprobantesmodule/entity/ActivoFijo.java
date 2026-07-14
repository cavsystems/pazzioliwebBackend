package com.pazzioliweb.comprobantesmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Activo fijo depreciable. La depreciación se calcula por LÍNEA RECTA:
 *   cuota mensual = (valorAdquisicion - valorResidual) / vidaUtilMeses
 * y genera un asiento periódico (débito gasto depreciación / crédito depreciación acumulada).
 *
 * Las cuentas se referencian por id (Integer) de CuentaContable, sin FK dura
 * (mismo enfoque que centroCostoId / terceroId en las líneas de asiento).
 */
@Entity
@Table(name = "activos_fijos")
@Data
public class ActivoFijo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 60)
    private String codigo;

    @Column(name = "fecha_adquisicion", nullable = false)
    private LocalDate fechaAdquisicion;

    /** Desde cuándo empieza a depreciarse (normalmente el mes siguiente a la compra). */
    @Column(name = "fecha_inicio_depreciacion", nullable = false)
    private LocalDate fechaInicioDepreciacion;

    @Column(name = "valor_adquisicion", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorAdquisicion = BigDecimal.ZERO;

    @Column(name = "valor_residual", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorResidual = BigDecimal.ZERO;

    @Column(name = "vida_util_meses", nullable = false)
    private Integer vidaUtilMeses;

    @Column(name = "metodo", nullable = false, length = 20)
    private String metodo = "LINEA_RECTA";

    @Column(name = "depreciacion_acumulada", nullable = false, precision = 18, scale = 2)
    private BigDecimal depreciacionAcumulada = BigDecimal.ZERO;

    @Column(name = "meses_depreciados", nullable = false)
    private Integer mesesDepreciados = 0;

    /** Cuenta del activo (ej. 1524 Equipo de oficina). Informativa. */
    @Column(name = "cuenta_activo_id")
    private Integer cuentaActivoId;

    /** Cuenta de depreciación acumulada (ej. 1592). Se acredita. */
    @Column(name = "cuenta_depreciacion_id")
    private Integer cuentaDepreciacionId;

    /** Cuenta de gasto depreciación (ej. 5160/5260). Se debita. */
    @Column(name = "cuenta_gasto_id")
    private Integer cuentaGastoId;

    @Column(name = "centro_costo_id")
    private Integer centroCostoId;

    /** ACTIVO / DEPRECIADO / BAJA */
    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "ACTIVO";

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();
}
