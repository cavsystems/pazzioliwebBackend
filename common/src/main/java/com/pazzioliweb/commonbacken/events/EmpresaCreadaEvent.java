package com.pazzioliweb.commonbacken.events;

import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado cuando se crea una empresa (tenant) nueva desde cavsystems,
 * ya con su schema creado y las migraciones del template aplicadas.
 *
 * Lo escuchan:
 * - comprobantesmodule (ComprobantesPorDefectoListener): siembra los comprobantes
 *   contables por defecto (Ventas, Compras, Entradas/Salidas de almacén, Recibos
 *   de caja, Egresos, Notas contables), así la empresa puede facturar/comprar sin
 *   que el admin tenga que crearlos a mano primero.
 * - cajerosmodule (CajeroPorDefectoListener): crea un cajero por defecto asociado
 *   al usuario admin recién creado (usuarioIdAdmin) y se lo asigna a los
 *   comprobantes que lo requieren (FC/RC/CE), para que ese usuario pueda vender/
 *   recibir pagos/hacer egresos de inmediato.
 */
public class EmpresaCreadaEvent extends ApplicationEvent {

    private final String schema;
    /** Id del primer usuario creado con la empresa (heurística: no hay flag "es admin"
     *  en el wizard de cavsystems). Puede ser null si la empresa se creó sin usuarios. */
    private final Integer usuarioIdAdmin;

    public EmpresaCreadaEvent(Object source, String schema, Integer usuarioIdAdmin) {
        super(source);
        this.schema = schema;
        this.usuarioIdAdmin = usuarioIdAdmin;
    }

    public String getSchema() { return schema; }
    public Integer getUsuarioIdAdmin() { return usuarioIdAdmin; }
}
