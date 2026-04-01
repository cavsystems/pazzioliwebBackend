package com.pazzioliweb.comprasmodule.entity;

import com.pazzioliweb.tercerosmodule.entity.Terceros;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "cuentas_por_pagar")
public class CuentaPorPagar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nit;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "numero_factura", nullable = false)
    private String numeroFactura;

    @Column(name = "numero_factura_proveedor")
    private String numeroFacturaProveedor;

    @Column(name = "valor_neto", nullable = false)
    private BigDecimal valorNeto;

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Terceros proveedor;
}
