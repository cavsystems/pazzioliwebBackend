package com.pazzioliweb.comprobantesmodule.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ComprobanteContableCreateDTO {
    /** IDs de los cajeros asignados a este comprobante. Puede ser 1 o varios.
     *  Vacío/null solo para LEGACY (que no acepta cajeros). */
    private List<Integer> cajeroIds;

    private String tipoMovimiento;   // FC, VC, CC, CR, RC, CE, DV
    private String prefijo;
    private String descripcion;
    private Integer siguienteConsecutivo;
    private Integer cuentaContableId;
    private Boolean afectaInventario;
    private Boolean activo;
}
