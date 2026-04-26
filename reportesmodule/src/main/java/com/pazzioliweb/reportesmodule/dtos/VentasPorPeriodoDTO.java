package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Un punto de datos genérico para gráficas de serie temporal.
 * Se usa para ventas por día, por semana, por mes, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentasPorPeriodoDTO {
    private String periodo;       // "2026-04-01", "Semana 15", "Abril 2026"
    private BigDecimal total;
    private Long cantidad;
    private BigDecimal costoTotal;
    private BigDecimal utilidad;
}

