package com.pazzioliweb.tesoreriamodule.entity;

import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "recibo_caja_medio_pago")
@EqualsAndHashCode(exclude = {"reciboCaja"})
@ToString(exclude = {"reciboCaja"})
public class ReciboCajaMedioPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recibo_caja_id", nullable = false)
    private ReciboCaja reciboCaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodo_pago_id", nullable = false)
    private MetodosPago metodoPago;

    @Column(name = "monto", precision = 18, scale = 2, nullable = false)
    private BigDecimal monto = BigDecimal.ZERO;
}

