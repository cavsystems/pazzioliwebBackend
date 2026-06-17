package com.pazzioliweb.commonbacken.events;

import org.springframework.context.ApplicationEvent;

/**
 * Publicado después de que cualquier movimiento (inventario, venta, compra, etc.)
 * queda persistido. El módulo de comprobantes lo escucha para hacer broadcast
 * WebSocket con el estado actualizado del comprobante.
 */
public class MovimientoRegistradoEvent extends ApplicationEvent {

    private final Long comprobanteId;
    private final Long movimientoId;
    private final String tipoMovimiento;

    public MovimientoRegistradoEvent(Object source, Long comprobanteId,
                                      Long movimientoId, String tipoMovimiento) {
        super(source);
        this.comprobanteId = comprobanteId;
        this.movimientoId = movimientoId;
        this.tipoMovimiento = tipoMovimiento;
    }

    public Long getComprobanteId() { return comprobanteId; }
    public Long getMovimientoId()  { return movimientoId; }
    public String getTipoMovimiento() { return tipoMovimiento; }
}
