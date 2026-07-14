package com.pazzioliweb.comprasmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "detalles_devolucion_compra")
public class DetalleDevolucionCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucion_compra_id", nullable = false)
    private DevolucionCompra devolucionCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_orden_compra_id", nullable = false)
    private DetalleOrdenCompra detalleOrdenCompra;

    @Column(name = "cantidad_devuelta", nullable = false)
    private Integer cantidadDevuelta;

    /** Costo unitario con que se valoró la salida de inventario. */
    @Column(name = "costo_unitario", nullable = false)
    private BigDecimal costoUnitario = BigDecimal.ZERO;

    /** IVA (monto) de la línea devuelta. */
    @Column(name = "iva_linea", nullable = false)
    private BigDecimal ivaLinea = BigDecimal.ZERO;

    /** Total de la línea devuelta (base − descuento + IVA). */
    @Column(name = "total_linea", nullable = false)
    private BigDecimal totalLinea = BigDecimal.ZERO;

    @Column(length = 300)
    private String motivo;
}
