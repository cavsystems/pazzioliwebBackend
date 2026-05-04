package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Devolución de venta detallada para reportes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionDetalladaDTO {
    private Long devolucionId;
    private LocalDateTime fechaCreacion;
    private String numeroVenta;
    private String clienteNombre;
    private String cajeroNombre;
    private BigDecimal totalNeto;
    private Long cantidadItems;
    private String motivo;
}
