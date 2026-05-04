package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Un comprobante de egreso registrado por el usuario logueado. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiEgresoDTO {
    private Long id;
    private Integer consecutivo;
    private LocalDateTime fechaCreacion;
    private String terceroNombre;
    private String concepto;
    private Boolean conceptoAbierto;
    private BigDecimal total;
    private String metodoPago;
    private String estado;
}
