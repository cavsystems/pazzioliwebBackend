package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Variante específica vendida — útil para drill-down desde un producto padre.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarianteVendidaDTO {
    private String sku;
    private String descripcionPadre;
    private String referenciaVariante;
    private String linea;
    private BigDecimal cantidadVendida;
    private BigDecimal totalVendido;
    private BigDecimal costoTotal;
    private BigDecimal utilidad;
    private BigDecimal stockActual;
}
