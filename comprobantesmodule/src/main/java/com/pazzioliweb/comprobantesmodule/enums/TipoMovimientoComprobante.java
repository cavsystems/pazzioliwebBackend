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
    DV("Devolución de venta");

    private final String descripcion;

    TipoMovimientoComprobante(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
