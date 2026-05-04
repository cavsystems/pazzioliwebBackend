package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * KPI del día para el cajero/usuario logueado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiResumenHoyDTO {
    private BigDecimal misVentas         = BigDecimal.ZERO;
    private Long cantidadVentas          = 0L;
    private BigDecimal ticketPromedio    = BigDecimal.ZERO;
    private BigDecimal totalEfectivo     = BigDecimal.ZERO;
    private BigDecimal totalElectronico  = BigDecimal.ZERO;
    private BigDecimal totalCredito      = BigDecimal.ZERO;
    private BigDecimal misRecibosCaja    = BigDecimal.ZERO;
    private Long cantidadRecibos         = 0L;
    private BigDecimal misEgresos        = BigDecimal.ZERO;
    private Long cantidadEgresos         = 0L;
    private BigDecimal misDevoluciones   = BigDecimal.ZERO;
    private Long cantidadDevoluciones    = 0L;
    private BigDecimal misAnulaciones    = BigDecimal.ZERO;
    private Long cantidadAnulaciones     = 0L;
    /** Si tiene una sesión de cajero activa, se incluyen sus datos. */
    private Boolean tieneSesionAbierta   = false;
    private Long detalleCajeroId;
    private BigDecimal baseCaja          = BigDecimal.ZERO;
    private BigDecimal saldoEsperadoCaja = BigDecimal.ZERO;
}
