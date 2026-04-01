package com.pazzioliweb.ventasmodule.entity;

import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.cajerosmodule.entity.Cajero;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_pedido", nullable = false, unique = true)
    private String numeroPedido;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Terceros cliente;

    @ManyToOne
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodegas bodega;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_entrega_esperada", nullable = false)
    private LocalDate fechaEntregaEsperada;

    @Column(nullable = false)
    private String estado; // PENDIENTE, EN_PROCESO, DESPACHADO, ENTREGADO, CANCELADO

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    private BigDecimal gravada = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal iva = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal descuentos = BigDecimal.ZERO;

    @Column(name = "total_pedido", nullable = false)
    private BigDecimal totalPedido = BigDecimal.ZERO;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> items;

    @ManyToOne
    @JoinColumn(name = "cajero_id")
    private Cajero cajero;

    @Column(name = "cotizacion_id")
    private Long cotizacionId;

    @Column(name = "usuario_creacion", nullable = false)
    private String usuarioCreacion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();
    // Getters y setters generados por Lombok
}

