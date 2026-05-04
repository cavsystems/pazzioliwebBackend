package com.pazzioliweb.comprobantesmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "cuentas_contables")
@Data
public class CuentaContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuenta_id")
    private Integer id;

    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    /** ACTIVO / PASIVO / PATRIMONIO / INGRESO / GASTO */
    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false)
    private Integer nivel = 5;

    @Column(name = "padre_id")
    private Integer padreId;

    @Column(name = "es_movimiento")
    private Boolean esMovimiento = true;

    @Column(nullable = false, length = 15)
    private String estado = "ACTIVO";

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /** Naturaleza derivada del tipo: ACTIVO/GASTO=DEBITO, resto=CREDITO. */
    @Transient
    public String getNaturaleza() {
        if (tipo == null) return null;
        return switch (tipo.toUpperCase()) {
            case "ACTIVO", "GASTO" -> "DEBITO";
            case "PASIVO", "PATRIMONIO", "INGRESO" -> "CREDITO";
            default -> null;
        };
    }
}
