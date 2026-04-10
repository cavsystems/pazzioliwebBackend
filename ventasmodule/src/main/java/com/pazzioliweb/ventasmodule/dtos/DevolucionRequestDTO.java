package com.pazzioliweb.ventasmodule.dtos;

import lombok.Data;

import java.util.List;

@Data
public class DevolucionRequestDTO {
    /** ID de la venta que origina la devolución */
    private Long ventaId;
    /** Motivo general de la devolución */
    private String motivo;
    /** Observaciones adicionales */
    private String observaciones;
    /** Usuario que registra la devolución */
    private String usuarioCreacion;
    /** Items que se devuelven con sus cantidades */
    private List<DevolucionItemRequestDTO> items;
}

