package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Resumen de compras por proveedor — para tabla / barras.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComprasPorProveedorDTO {
    private Integer proveedorId;
    private String proveedorNombre;
    private Long cantidadOrdenes;
    private BigDecimal totalComprado;
}

