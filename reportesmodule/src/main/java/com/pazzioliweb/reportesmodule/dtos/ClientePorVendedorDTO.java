package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Drill-down del reporte "Ventas por vendedor": detalle de los clientes a los
 * que un vendedor les vendió en el periodo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientePorVendedorDTO {
    private Integer clienteId;
    private String identificacion;
    private String clienteNombre;
    private Long cantidadVentas;
    private BigDecimal totalVendido;
    private LocalDate ultimaVenta;
}
