package com.pazzioliweb.ventasmodule.entity;

import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "venta_metodos_pago")
public class VentaMetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "metodo_pago_id", nullable = false)
    private MetodosPago metodoPago;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column
    private String referencia; // Número de referencia del pago (transferencia, cheque, etc.)
    // Getters y setters generados por Lombok
}

