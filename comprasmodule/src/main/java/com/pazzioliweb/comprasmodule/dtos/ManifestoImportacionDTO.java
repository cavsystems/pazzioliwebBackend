package com.pazzioliweb.comprasmodule.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ManifestoImportacionDTO {
    private Long id;
    private String numeroDeclaracion;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaImportacion;

    private String aduana;
    private String paisOrigen;
    private String proveedorInternacional;
    private String numeroContenedor;
    private String observaciones;
    private String rutaPdf;
    private String nombreArchivoOriginal;
    private String estado;
    private String usuarioCreacion;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCreacion;

    private Long ordenCompraId;
    private String numeroOrden;
}
