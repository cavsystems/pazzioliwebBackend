package com.pazzioliweb.commonbacken.events;

import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado cuando una venta se completa exitosamente.
 * El módulo de facturación electrónica lo escucha para generar
 * la factura automáticamente.
 */
public class VentaCompletadaEvent extends ApplicationEvent {

    private final Long ventaId;
    private final Integer cajeroId;

    public VentaCompletadaEvent(Object source, Long ventaId, Integer cajeroId) {
        super(source);
        this.ventaId = ventaId;
        this.cajeroId = cajeroId;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public Integer getCajeroId() {
        return cajeroId;
    }
}

