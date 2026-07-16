package com.pazzioliweb.commonbacken.events;

import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Evento publicado cuando se registra un ASIENTO CONTABLE MANUAL que afecta cuentas de cartera
 * (CxC 1305 / CxP 2205). Los módulos de ventas (CxC) y compras (CxP) lo escuchan para
 * sincronizar el subledger (cuentas_por_cobrar / cuentas_por_pagar), ya que el asiento manual
 * por sí solo únicamente afecta el mayor y el dashboard lee el subledger.
 *
 * Vive en common (sin dependencias de ventas/compras/comprobantes) para evitar ciclos: lo publica
 * comprobantesmodule y lo consumen ventasmodule/comprasmodule vía @TransactionalEventListener.
 */
public class AsientoManualRegistradoEvent extends ApplicationEvent {

    private final Long asientoId;
    private final String numeroAsiento;
    private final LocalDate fecha;
    private final List<LineaCartera> lineas;

    public AsientoManualRegistradoEvent(Object source, Long asientoId, String numeroAsiento,
                                        LocalDate fecha, List<LineaCartera> lineas) {
        super(source);
        this.asientoId = asientoId;
        this.numeroAsiento = numeroAsiento;
        this.fecha = fecha;
        this.lineas = lineas;
    }

    public Long getAsientoId() { return asientoId; }
    public String getNumeroAsiento() { return numeroAsiento; }
    public LocalDate getFecha() { return fecha; }
    public List<LineaCartera> getLineas() { return lineas; }

    /** Línea de un asiento manual que toca una cuenta de cartera (1305/2205). */
    public static class LineaCartera {
        private final String cuentaCodigo;
        private final Integer terceroId;
        private final String terceroNombre;
        private final BigDecimal debito;
        private final BigDecimal credito;
        private final String documentoCruce;

        public LineaCartera(String cuentaCodigo, Integer terceroId, String terceroNombre,
                            BigDecimal debito, BigDecimal credito, String documentoCruce) {
            this.cuentaCodigo = cuentaCodigo;
            this.terceroId = terceroId;
            this.terceroNombre = terceroNombre;
            this.debito = debito;
            this.credito = credito;
            this.documentoCruce = documentoCruce;
        }

        public String getCuentaCodigo() { return cuentaCodigo; }
        public Integer getTerceroId() { return terceroId; }
        public String getTerceroNombre() { return terceroNombre; }
        public BigDecimal getDebito() { return debito; }
        public BigDecimal getCredito() { return credito; }
        public String getDocumentoCruce() { return documentoCruce; }
    }
}
