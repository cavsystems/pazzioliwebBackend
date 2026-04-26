package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Ventas agrupadas por cliente — top clientes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentasPorClienteDTO {
    private Integer clienteId;
    private String identificacion;
    private String nombre;
    private Long cantidadVentas;
    private BigDecimal totalComprado;
    private BigDecimal saldoCartera;
}

