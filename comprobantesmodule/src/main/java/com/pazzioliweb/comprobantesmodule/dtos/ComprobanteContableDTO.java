package com.pazzioliweb.comprobantesmodule.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComprobanteContableDTO {
    private Long id;
    private Integer cajeroId;
    private String cajeroNombre;
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
