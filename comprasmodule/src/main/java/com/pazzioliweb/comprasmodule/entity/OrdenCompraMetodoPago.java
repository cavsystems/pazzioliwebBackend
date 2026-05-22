package com.pazzioliweb.comprasmodule.entity;

import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Método de pago utilizado en una orden de compra.
 * Una orden puede tener varios métodos (pago mixto: parte efectivo, parte transferencia).
 */
@Data
@Entity
@Table(name = "orden_compra_metodos_pago")
public class OrdenCompraMetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompra ordenCompra;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "metodo_pago_id", nullable = false)
    private MetodosPago metodoPago;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal monto = BigDecimal.ZERO;

    @Column(length = 100)
    private String referencia;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
