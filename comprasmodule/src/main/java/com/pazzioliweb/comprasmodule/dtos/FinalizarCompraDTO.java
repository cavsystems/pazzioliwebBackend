package com.pazzioliweb.comprasmodule.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO usado en el endpoint POST /compras/ordenes-compra/{id}/finalizar-ingreso.
 * Contiene los datos definitivos tal como llegaron en la factura del proveedor:
 * precios reales, IVA, retenciones ajustadas, métodos de pago y número de factura.
 */
@Data
public class FinalizarCompraDTO {

    /** Items con cantidades recibidas y precios finales según factura del proveedor. */
    private List<ItemFinalDTO> items;

    /** Retefuente aplicada al proveedor (monto final, ajustable manualmente). */
    private BigDecimal retefuente;

    /** Retención de IVA aplicada al proveedor (monto final, ajustable manualmente). */
    private BigDecimal reteiva;

    /** Retención de ICA aplicada al proveedor (monto final, ajustable manualmente). */
    private BigDecimal reteica;

    /** Descuentos a nivel de orden (monto total de descuentos). */
    private BigDecimal descuentos;

    /** Base gravable final (suma de subtotales sin IVA de los items recibidos). */
    private BigDecimal gravada;

    /** IVA total de la factura. */
    private BigDecimal iva;

    /** Total final de la factura (gravada + iva - retenciones - descuentos). */
    private BigDecimal totalFinal;

    /**
     * Métodos de pago de la compra.
     * Si alguno tiene tipoNegociacion=Credito → comprobante CR y se crea CxP por el saldo.
     * Métodos de pago de contado → comprobante CC y se acredita directamente la cuenta.
     * Si todos son crédito → todo va a CxP.
     */
    private List<MetodoPagoDTO> metodosPago;

    /** Número de factura del proveedor. */
    private String numeroFacturaProveedor;

    /** Fecha de la factura del proveedor en formato MM/dd/yyyy. */
    private String fechaFactura;

    /** ID del cajero que registra el ingreso (opcional: la compra ya no está atada al cajero). */
    private Integer cajeroId;

    /** Comprobante/prefijo elegido en el selector de compra (opcional). Si viene, se usa ese. */
    private Long comprobanteId;

    /** ID del proveedor (Terceros). Necesario para los créditos del asiento. */
    private Integer proveedorId;

    @Data
    public static class ItemFinalDTO {
        /** ID del DetalleOrdenCompra a actualizar. */
        private Long detalleId;
        /** Cantidad efectivamente recibida. */
        private Integer cantidadRecibida;
        /** true si el item está completamente recibido. */
        private Boolean recibido;
        /** Precio unitario final según factura del proveedor. */
        private BigDecimal precioUnitario;
        /** Porcentaje de IVA del item (p.ej. 19 para 19%). */
        private BigDecimal ivaPorcentaje;
        /** Descuento por línea. */
        private BigDecimal descuento;
        /** Número de manifiesto o guía de transporte. */
        private String manifiesto;
    }

    @Data
    public static class MetodoPagoDTO {
        private Integer metodoPagoId;
        private BigDecimal monto;
        private String referencia;
    }
}
