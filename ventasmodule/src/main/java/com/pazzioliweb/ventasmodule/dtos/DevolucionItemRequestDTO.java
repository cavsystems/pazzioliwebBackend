package com.pazzioliweb.ventasmodule.dtos;

import lombok.Data;

@Data
public class DevolucionItemRequestDTO {
    /** ID del DetalleVenta que se devuelve */
    private Long itemVentaId;
    /** Cantidad a devolver (debe ser <= cantidad vendida) */
    private Integer cantidadDevolver;
    /** Motivo específico del ítem */
    private String motivo;
}

