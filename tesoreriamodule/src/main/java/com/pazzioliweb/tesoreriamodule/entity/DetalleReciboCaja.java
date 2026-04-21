package com.pazzioliweb.tesoreriamodule.entity;

import com.pazzioliweb.ventasmodule.entity.CuentaPorCobrar;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "detalle_recibo_caja")
@EqualsAndHashCode(exclude = {"reciboCaja"})
public class DetalleReciboCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recibo_caja_id", nullable = false)
    private ReciboCaja reciboCaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_por_cobrar_id", nullable = false)
    private CuentaPorCobrar cuentaPorCobrar;

    @Column(name = "monto_abonado", precision = 15, scale = 2)
    private BigDecimal montoAbonado = BigDecimal.ZERO;
}

