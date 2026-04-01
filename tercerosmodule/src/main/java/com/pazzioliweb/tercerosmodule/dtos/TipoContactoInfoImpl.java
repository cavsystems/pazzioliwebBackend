package com.pazzioliweb.tercerosmodule.dtos;

import java.util.List;

import com.pazzioliweb.tercerosmodule.dtos.ContactoTerceroDTO.TipoContactoInfo;
import com.pazzioliweb.tercerosmodule.entity.ContactoTercero;

public class TipoContactoInfoImpl implements ContactoTerceroDTO.TipoContactoInfo {
	private Integer tipoContactoId;
    private String nombre;

    public TipoContactoInfoImpl() {}  // Constructor público sin argumentos para Jackson

    @Override
    public Integer getTipoContactoId() {
        return tipoContactoId;
    }

    public void setTipoContactoId(Integer tipoContactoId) {
        this.tipoContactoId = tipoContactoId;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}