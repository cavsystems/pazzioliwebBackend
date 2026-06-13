package com.pazzioliweb.tercerosmodule.dtos;

import java.util.List;

public class RutExtraidoDTO {
    private String nit;
    private String dv;
    private String razonSocial;
    private String nombre1;
    private String nombre2;
    private String apellido1;
    private String apellido2;
    private String tipoPersona;
    private Integer codigoTipoIdentificacion;
    private String regimen;
    private String direccion;
    private Integer departamentoId;
    private Integer ciudadId;
    private String codigoPostal;
    private Integer actividadEconomica;
    private List<Integer> retenciones;

    // Getters and Setters
    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDv() {
        return dv;
    }

    public void setDv(String dv) {
        this.dv = dv;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombre1() { return nombre1; }
    public void setNombre1(String nombre1) { this.nombre1 = nombre1; }

    public String getNombre2() { return nombre2; }
    public void setNombre2(String nombre2) { this.nombre2 = nombre2; }

    public String getApellido1() { return apellido1; }
    public void setApellido1(String apellido1) { this.apellido1 = apellido1; }

    public String getApellido2() { return apellido2; }
    public void setApellido2(String apellido2) { this.apellido2 = apellido2; }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public Integer getCodigoTipoIdentificacion() {
        return codigoTipoIdentificacion;
    }

    public void setCodigoTipoIdentificacion(Integer codigoTipoIdentificacion) {
        this.codigoTipoIdentificacion = codigoTipoIdentificacion;
    }

    public String getRegimen() {
        return regimen;
    }

    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(Integer departamentoId) { this.departamentoId = departamentoId; }

    public Integer getCiudadId() { return ciudadId; }
    public void setCiudadId(Integer ciudadId) { this.ciudadId = ciudadId; }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public Integer getActividadEconomica() {
        return actividadEconomica;
    }

    public void setActividadEconomica(Integer actividadEconomica) {
        this.actividadEconomica = actividadEconomica;
    }

    public List<Integer> getRetenciones() {
        return retenciones;
    }

    public void setRetenciones(List<Integer> retenciones) {
        this.retenciones = retenciones;
    }
}
