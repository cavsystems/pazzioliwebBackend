package com.pazzioliweb.facturacionmodule.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO que recibe el frontend para solicitar la generación
 * de una factura electrónica desde una venta completada.
 */
@Data
public class GenerarFacturaRequestDTO {
    private Long ventaId;           // ID de la venta COMPLETADA
    private Integer comprobanteId;  // Tipo de comprobante (Factura de Venta)
    private Integer cajaId;         // Caja desde donde se factura (opcional)
    private String observaciones;   // Observaciones adicionales (opcional)
}

