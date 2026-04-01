package com.pazzioliweb.comprasmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasProveedorDTO {
    private Long proveedorId;
    private String nombreProveedor;
    private Long cantidadOrdenes;
    private BigDecimal montoTotal;
}
