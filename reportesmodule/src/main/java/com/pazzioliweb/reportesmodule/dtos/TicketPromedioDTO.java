package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Ticket promedio agrupado por una dimensión (cajero / vendedor / día).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketPromedioDTO {
    private String etiqueta;          // ej. nombre cajero / vendedor / fecha
    private Long cantidadVentas;
    private BigDecimal totalVendido;
    private BigDecimal ticketPromedio;
    private BigDecimal unidadesPorTicket;   // UPT
    private Long unidadesTotales;
}
