package com.pazzioliweb.comprobantesmodule.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ComprobanteContableDTO {
    private Long id;
    /** Lista de IDs de cajeros asignados. Vacío para LEGACY. */
    private List<Integer> cajeroIds = new ArrayList<>();
    /** Nombres de los cajeros (para mostrar en la UI sin extra fetch). */
    private List<String> cajeroNombres = new ArrayList<>();
    /** Texto resumen "Juan, María (2)" para listados. */
    private String cajerosResumen;

    private String tipoMovimiento;
    private String tipoMovimientoDescripcion;
    private String prefijo;
    private String descripcion;
    private Integer siguienteConsecutivo;
    private Integer cuentaContableId;
    private String cuentaContableCodigo;
    private String cuentaContableNombre;
    private Boolean afectaInventario;
    private Boolean activo;
    private Boolean esLegacy;
    private LocalDateTime fechaCreacion;
}
