package com.pazzioliweb.tercerosmodule.dtos;

import com.pazzioliweb.commonbacken.entity.Departamento;

public class DepartamentoInfoDTOImpl implements SedeTerceroDTO.DepartamentoInfo {

    private Integer departamentoId;
    private String nombre;

    public DepartamentoInfoDTOImpl() {} // constructor vacío necesario para Jackson

    public DepartamentoInfoDTOImpl(Departamento dep) {
        this.departamentoId = dep.getCodigo();
        this.nombre = dep.getDepartamento();
    }

    @Override
    public Integer getDepartamentoId() { return departamentoId; }

    @Override
    public String getNombre() { return nombre; }

    public void setDepartamentoId(Integer departamentoId) { this.departamentoId = departamentoId; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}