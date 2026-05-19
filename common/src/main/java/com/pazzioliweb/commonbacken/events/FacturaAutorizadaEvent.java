package com.pazzioliweb.commonbacken.events;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Evento publicado cuando facturacionmodule recibe respuesta de la DIAN
 * (autorización, rechazo o simulación) sobre una factura electrónica.
 *
 * El comprobantesmodule lo escucha para actualizar el asiento contable
 * asociado a esa venta — marca el estado DIAN, guarda el CUFE y la fecha
 * de autorización. Así desde el libro mayor se ve qué ventas ya están
 * avaladas por la DIAN y cuáles no.
 *
 * Estados posibles:
 *   - AUTORIZADA  → DIAN aceptó la factura
 *   - RECHAZADA   → DIAN rechazó (con mensaje de error)
 *   - SIMULADA    → modo simulación (sin envío real a DIAN)
 *   - PENDIENTE   → enviada pero esperando respuesta
 */
public class FacturaAutorizadaEvent extends ApplicationEvent {

    private final Long ventaId;
    private final Integer facturaId;
    private final String cufe;
    private final String estadoDian;
    private final String mensajeDian;
    private final LocalDateTime fechaAutorizacion;

    public FacturaAutorizadaEvent(Object source,
                                   Long ventaId,
                                   Integer facturaId,
                                   String cufe,
                                   String estadoDian,
                                   String mensajeDian,
                                   LocalDateTime fechaAutorizacion) {
        super(source);
        this.ventaId = ventaId;
        this.facturaId = facturaId;
        this.cufe = cufe;
        this.estadoDian = estadoDian;
        this.mensajeDian = mensajeDian;
        this.fechaAutorizacion = fechaAutorizacion;
    }

    public Long getVentaId() { return ventaId; }
    public Integer getFacturaId() { return facturaId; }
    public String getCufe() { return cufe; }
    public String getEstadoDian() { return estadoDian; }
    public String getMensajeDian() { return mensajeDian; }
    public LocalDateTime getFechaAutorizacion() { return fechaAutorizacion; }
}
