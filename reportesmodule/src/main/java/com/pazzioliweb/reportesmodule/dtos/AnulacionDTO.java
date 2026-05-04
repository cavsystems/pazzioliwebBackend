package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Documento anulado (venta, recibo o egreso) — vista unificada.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnulacionDTO {
    private String tipoDocumento;          // VENTA / RECIBO / EGRESO
    private Long documentoId;
    private String consecutivo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAnulacion;
    private String terceroNombre;
    private BigDecimal total;
    private String motivoAnulacion;
    private Integer anuladoPorUsuarioId;
    private String anuladoPorUsuarioNombre;
}
