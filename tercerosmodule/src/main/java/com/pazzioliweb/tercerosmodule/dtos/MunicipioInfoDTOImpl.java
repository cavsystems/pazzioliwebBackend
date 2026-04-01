package com.pazzioliweb.tercerosmodule.dtos;

import com.pazzioliweb.commonbacken.entity.Municipio;

public class MunicipioInfoDTOImpl implements SedeTerceroDTO.MunicipioInfo {

    private Integer municipioId;
    private String nombre;

    public MunicipioInfoDTOImpl() {} // constructor vacío para Jackson

    public MunicipioInfoDTOImpl(Municipio mun) {
        this.municipioId = mun.getCodigo();
        this.nombre = mun.getMunicipio();
    }

    @Override
    public Integer getMunicipioId() { return municipioId; }

    @Override
    public String getNombre() { return nombre; }

    public void setMunicipioId(Integer municipioId) { this.municipioId = municipioId; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}