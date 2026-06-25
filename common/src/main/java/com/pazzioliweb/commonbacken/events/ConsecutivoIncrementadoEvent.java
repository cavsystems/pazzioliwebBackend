package com.pazzioliweb.commonbacken.events;

import org.springframework.context.ApplicationEvent;

/**
 * Publicado después de que el consecutivo de un comprobante es incrementado.
 * Contiene un booleano indicando que el consecutivo aumentó.
 */
public class ConsecutivoIncrementadoEvent extends ApplicationEvent {

    private final Long comprobanteId;
    private final Boolean consecutivoAumentado;
    private final String tipoMovimiento;
    private final Integer nuevoConsecutivo;

    public ConsecutivoIncrementadoEvent(Object source, Long comprobanteId, 
                                        String tipoMovimiento, Integer nuevoConsecutivo) {
        super(source);
        this.comprobanteId = comprobanteId;
        this.consecutivoAumentado = true;
        this.tipoMovimiento = tipoMovimiento;
        this.nuevoConsecutivo = nuevoConsecutivo;
    }

    public Long getComprobanteId() { 
        return comprobanteId; 
    }
    
    public Boolean getConsecutivoAumentado() { 
        return consecutivoAumentado; 
    }
    
    public String getTipoMovimiento() { 
        return tipoMovimiento; 
    }
    
    public Integer getNuevoConsecutivo() { 
        return nuevoConsecutivo; 
    }
}
