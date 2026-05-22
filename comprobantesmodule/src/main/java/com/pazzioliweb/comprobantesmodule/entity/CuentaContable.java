package com.pazzioliweb.comprobantesmodule.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "cuentas_contables")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    /** Indica si los asientos contra esta cuenta deben llevar un tercero asignado
     *  (CxC, CxP, anticipos, retenciones). Si es true, el formulario de asiento
     *  manual exige seleccionar tercero al usar esta cuenta. */
    @Column(name = "requiere_tercero", nullable = false)
    private Boolean requiereTercero = false;

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
