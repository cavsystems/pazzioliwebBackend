package com.pazzioliweb.comprobantesmodule.enums;

/**
 * Tipos de movimiento que requieren un comprobante contable.
 * Cada (cajero × tipo) debe tener un comprobante con prefijo único.
 */
public enum TipoMovimientoComprobante {
    /** Factura / Venta de contado */
    FC("Venta contado"),
    /** Venta a crédito (genera cuenta por cobrar) */
    VC("Venta crédito"),
    /** Compra de contado */
    CC("Compra contado"),
    /** Compra a crédito (genera cuenta por pagar) */
    CR("Compra crédito"),
    /** Recibo de caja (cobro / abono recibido) */
    RC("Recibo de caja"),
    /** Comprobante de egreso (pago / abono entregado) */
    CE("Comprobante de egreso"),
    /** Devolución de venta */
    DV("Devolución de venta"),
    /** Entrada manual de inventario (ajuste positivo / inicial) */
    EI("Entrada de inventario"),
    /** Salida manual de inventario (pérdida / daño / consumo interno) */
    SI("Salida de inventario"),
    /** Traslado entre bodegas — no genera asiento contable (es neto cero) */
    TI("Traslado de inventario"),
    /** Nota Crédito Electrónica (DIAN) — anula/corrige factura electrónica */
    NC("Nota Crédito Electrónica"),
    /** Nota Débito Electrónica (DIAN) — aumenta valor de factura electrónica */
    ND("Nota Débito Electrónica"),
    /** Tiquete POS Electrónico (DIAN) — documento equivalente para ventas sin identificar cliente */
    TPOS("Tiquete POS Electrónico"),
    /** Documento Soporte (DIAN) — compras a NO obligados a facturar */
    DS("Documento Soporte");

    private final String descripcion;

    TipoMovimientoComprobante(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Indica si este tipo de movimiento DEBE tener al menos un cajero asignado
     * para poder operar (porque la asignación de consecutivo se hace por cajero).
     *
     * Tipos que SÍ requieren cajero:
     *   FC, VC (ventas), CC, CR (compras), RC, CE (movimientos de caja), DV (devolución)
     *
     * Tipos que NO requieren cajero (se vinculan a bodega o son auto-generados):
     *   EI, SI, TI (movimientos manuales de inventario)
     *   NC, ND, TPOS, DS (documentos DIAN generados automáticamente)
     */
    public boolean requiereCajero() {
        switch (this) {
            case FC: case VC: case CC: case CR: case RC: case CE: case DV:
                return true;
            default:
                return false;
        }
    }
}
