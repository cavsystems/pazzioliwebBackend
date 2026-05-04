package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Una venta individual del cajero/usuario logueado. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiVentaDTO {
    private Long ventaId;
    private String numeroVenta;
    private LocalDateTime fechaEmision;
    private String clienteNombre;
    private String clienteIdentificacion;
    private BigDecimal totalVenta;
    private String estado;
    private String metodoPago;
    private Long cantidadItems;
}
