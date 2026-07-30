package com.pazzioliweb.comprasmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegalizacionProgresoDTO {
    private String usuario;
    private Integer paso; // 1-6 según el proceso
    private String mensaje; // Descripción del paso actual
    private Integer porcentaje; // 0-100
    private Long ordenCompraId; // ID de la orden siendo procesada
    private String estado; // "PROCESANDO", "COMPLETADO", "ERROR"
}
