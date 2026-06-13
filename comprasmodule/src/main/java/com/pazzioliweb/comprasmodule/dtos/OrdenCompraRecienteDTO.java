package com.pazzioliweb.comprasmodule.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrdenCompraRecienteDTO {
    private Long id;
    private String numeroOrden;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEmision;

    // Proveedor/Tercero
    private Integer proveedorId;
    private String proveedorNombre;
    private String proveedorRazonSocial;

    // Cajero
    private Integer cajeroId;
    private String cajeroNombre;

    // Usuario relacionado al cajero (opcional - left join)
    private Integer usuarioId;
    private String usuarioNombre;
    private String usuarioUsername;

    private String estado;

    public OrdenCompraRecienteDTO() {}

    public OrdenCompraRecienteDTO(Long id, String numeroOrden, LocalDate fechaEmision,
                                   Integer proveedorId, String proveedorNombre, String proveedorRazonSocial,
                                   Integer cajeroId, String cajeroNombre,
                                   Integer usuarioId, String usuarioNombre, String usuarioUsername,
                                   String estado) {
        this.id = id;
        this.numeroOrden = numeroOrden;
        this.fechaEmision = fechaEmision;
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
        this.proveedorRazonSocial = proveedorRazonSocial;
        this.cajeroId = cajeroId;
        this.cajeroNombre = cajeroNombre;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.usuarioUsername = usuarioUsername;
        this.estado = estado;
    }
}
