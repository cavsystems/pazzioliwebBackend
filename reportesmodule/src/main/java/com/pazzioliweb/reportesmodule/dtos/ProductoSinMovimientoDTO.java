package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Productos activos que NO han tenido ventas en el periodo consultado.
 * Detecta inventario inmovilizado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoSinMovimientoDTO {
    private Integer productoVarianteId;
    private String sku;
    private String descripcion;
    private String linea;
    private BigDecimal existencia;
    private BigDecimal costo;
    private BigDecimal valorInmovilizado;     // existencia × costo
    private LocalDateTime fechaUltimaVenta;
    private LocalDateTime fechaUltimaCompra;
    private Long diasSinVender;
}
