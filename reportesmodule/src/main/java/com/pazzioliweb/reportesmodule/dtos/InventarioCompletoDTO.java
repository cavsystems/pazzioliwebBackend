package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Existencia individual por (variante, bodega) — usado para los reportes
 * de "Inventario completo" y "Exceso de stock".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioCompletoDTO {
    private Long productoVarianteId;
    private String sku;
    private String descripcion;
    /** Referencia de variante (talla, color, etc.) o null si es la predeterminada */
    private String referenciaVariante;
    private String linea;
    private String grupo;
    private String bodega;
    private String ubicacion;
    private BigDecimal existencia;
    private BigDecimal stockMin;
    private BigDecimal stockMax;
    private BigDecimal costo;
    private BigDecimal valorInventario;
    /** "AGOTADO", "CRITICO", "BAJO", "NORMAL", "EXCESO" */
    private String estado;
    private LocalDateTime fechaUltimoMovimiento;
    private String imagen;
}
