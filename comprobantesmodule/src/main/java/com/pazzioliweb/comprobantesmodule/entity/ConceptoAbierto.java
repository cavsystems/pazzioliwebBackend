package com.pazzioliweb.comprobantesmodule.entity;

import com.pazzioliweb.tercerosmodule.entity.Terceros;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "conceptos_abiertos")
@Data
@EqualsAndHashCode(exclude = {"cuentaContable", "tercero"})
@ToString(exclude = {"cuentaContable", "tercero"})
public class ConceptoAbierto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String descripcion;

    /** RECIBO / EGRESO / AMBOS */
    @Column(nullable = false, length = 15)
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_contable_id", nullable = false)
    private CuentaContable cuentaContable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tercero_id")
    private Terceros tercero;

    @Column(name = "beneficiario_nombre", length = 200)
    private String beneficiarioNombre;

    @Column(name = "beneficiario_documento", length = 50)
    private String beneficiarioDocumento;

    @Column(name = "info_extra", columnDefinition = "TEXT")
    private String infoExtra;

    @Column(nullable = false, length = 15)
    private String estado = "ACTIVO";

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
