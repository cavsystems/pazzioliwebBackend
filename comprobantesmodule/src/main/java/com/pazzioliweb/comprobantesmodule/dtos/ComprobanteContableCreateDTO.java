package com.pazzioliweb.comprobantesmodule.dtos;

import lombok.Data;

@Data
public class ComprobanteContableCreateDTO {
    private Integer cajeroId;        // null para LEGACY
    private String tipoMovimiento;   // FC, VC, CC, CR, RC, CE, DV
    private String prefijo;
    private String descripcion;
    private Integer siguienteConsecutivo;
    private Integer cuentaContableId;
    private Boolean afectaInventario;
    private Boolean activo;
}
