package com.pazzioliweb.tesoreriamodule.entity;

import com.pazzioliweb.comprasmodule.entity.CuentaPorPagar;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "detalle_comprobante_egreso")
@EqualsAndHashCode(exclude = {"comprobanteEgreso"})
public class DetalleComprobanteEgreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_egreso_id", nullable = false)
    private ComprobanteEgreso comprobanteEgreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_por_pagar_id", nullable = false)
    private CuentaPorPagar cuentaPorPagar;

    @Column(name = "monto_abonado", precision = 15, scale = 2)
    private BigDecimal montoAbonado = BigDecimal.ZERO;
}

