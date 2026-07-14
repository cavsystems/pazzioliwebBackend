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

    /** Clase PUC: ACTIVO / PASIVO / PATRIMONIO / INGRESO / GASTO /
     *  COSTO / COSTO_PRODUCCION / ORDEN_DEUDORAS / ORDEN_ACREEDORAS */
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

    /** "Documento de cruce": si es true, al digitar esta cuenta en un asiento
     *  manual se exige tercero + documento de cruce (factura/CxC/CxP). Se usa
     *  para cuentas de cartera (Ej. 130505 clientes, 220505 proveedores) para
     *  contabilizar el saldo discriminado por documento y no un total global. */
    @Column(name = "requiere_documento_cruce", nullable = false)
    private Boolean requiereDocumentoCruce = false;

    /** Si es true, al digitar esta cuenta en un asiento se exige centro de costo.
     *  Útil para cuentas de gasto/costo (5/6/7) que se reportan por centro de costo. */
    @Column(name = "requiere_centro_costo", nullable = false)
    private Boolean requiereCentroCosto = false;

    @Column(nullable = false, length = 15)
    private String estado = "ACTIVO";

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /** Naturaleza derivada del tipo (débito o crédito según la clase PUC). */
    @Transient
    public String getNaturaleza() {
        if (tipo == null) return null;
        return switch (tipo.toUpperCase()) {
            case "ACTIVO", "GASTO", "COSTO", "COSTO_PRODUCCION", "ORDEN_DEUDORAS" -> "DEBITO";
            case "PASIVO", "PATRIMONIO", "INGRESO", "ORDEN_ACREEDORAS" -> "CREDITO";
            default -> null;
        };
    }
}
