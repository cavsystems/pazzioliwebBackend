package com.pazzioliweb.comprasmodule.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "manifestos_importacion")
public class ManifestoImportacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_declaracion", nullable = false, length = 200)
    private String numeroDeclaracion;

    @Column(name = "fecha_importacion", nullable = false)
    private LocalDate fechaImportacion;

    @Column(name = "aduana", length = 300)
    private String aduana;

    @Column(name = "pais_origen", length = 100)
    private String paisOrigen;

    @Column(name = "proveedor_internacional", length = 300)
    private String proveedorInternacional;

    @Column(name = "numero_contenedor", length = 100)
    private String numeroContenedor;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "ruta_pdf", length = 500)
    private String rutaPdf;

    @Column(name = "nombre_archivo_original", length = 200)
    private String nombreArchivoOriginal;

    @Column(name = "estado", length = 50)
    private String estado = "ACTIVO";

    @Column(name = "usuario_creacion", nullable = false, length = 100)
    private String usuarioCreacion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id")
    private OrdenCompra ordenCompra;
}
