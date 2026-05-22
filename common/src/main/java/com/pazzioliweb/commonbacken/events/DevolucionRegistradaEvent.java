package com.pazzioliweb.commonbacken.events;

import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado cuando se registra una devolución de venta exitosamente.
 * El módulo de facturación electrónica lo escucha para generar
 * la Nota Crédito Electrónica (NC) ante DIAN.
 */
public class DevolucionRegistradaEvent extends ApplicationEvent {

    private final Long devolucionId;
    private final Long ventaId;
    private final Integer cajeroId;
    private final Integer codigoConcepto;  // DIAN: 1=Devolución, 2=Anulación, 3=Rebaja, 4=Descuento, 5=Otro

    public DevolucionRegistradaEvent(Object source, Long devolucionId, Long ventaId,
                                      Integer cajeroId, Integer codigoConcepto) {
        super(source);
        this.devolucionId = devolucionId;
        this.ventaId = ventaId;
        this.cajeroId = cajeroId;
        this.codigoConcepto = codigoConcepto;
    }

    public Long getDevolucionId() { return devolucionId; }
    public Long getVentaId() { return ventaId; }
    public Integer getCajeroId() { return cajeroId; }
    public Integer getCodigoConcepto() { return codigoConcepto; }
}
