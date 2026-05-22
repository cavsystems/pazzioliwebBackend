package com.pazzioliweb.ventasmodule.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
public class DevolucionDTO {
    private Long id;
    private Long ventaId;
    private String numeroVenta;
    private String nombreCliente;
    private String numeroDevolucion;
    private String motivo;
    private String observaciones;
    private String estado;
    private String usuarioCreacion;
    private Integer cajeroId;
    private String cajeroNombre;
    private LocalDate fechaCreacion;

    /** Subtotal devuelto (sin IVA) */
    private BigDecimal totalDevuelto;

    /** IVA devuelto */
    private BigDecimal ivaDevuelto;

    /** Total neto devuelto */
    private BigDecimal totalNeto;

    private List<DetalleDevolucionDTO> items;

    /** ── Datos de la Nota Crédito Electrónica generada ante DIAN ── */
    private String numeroNc;        // ej NC-1
    private String cufeNc;          // CUDE
    private String estadoDianNc;    // AUTORIZADA / RECHAZADA / SIMULADA / null
    private String mensajeDianNc;
    private String qrDataNc;
}


