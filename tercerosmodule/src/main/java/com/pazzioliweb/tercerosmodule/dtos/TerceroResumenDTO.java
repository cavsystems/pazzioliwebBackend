package com.pazzioliweb.tercerosmodule.dtos;

public class TerceroResumenDTO {
    private Integer terceroId;
    private String identificacion;
    private String razonSocial;
    private String tipoIdentificacion;
    private String clasificacion;
    private String regimen;

    public TerceroResumenDTO(
        Integer terceroId,
        String identificacion,
        String razonSocial,
        String tipoIdentificacion,
        String clasificacion,
        String regimen
    ) {
        this.terceroId = terceroId;
        this.identificacion = identificacion;
        this.razonSocial = razonSocial;
        this.tipoIdentificacion = tipoIdentificacion;
        this.clasificacion = clasificacion;
        this.regimen = regimen;
    }

    // Getters y setters
    public Integer getTerceroId() { return terceroId; }
    public String getIdentificacion() { return identificacion; }
    public String getRazonSocial() { return razonSocial; }
    public String getTipoIdentificacion() { return tipoIdentificacion; }
    public String getClasificacion() { return clasificacion; }
    public String getRegimen() { return regimen; }
}