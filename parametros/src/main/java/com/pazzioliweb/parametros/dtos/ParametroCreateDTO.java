package com.pazzioliweb.parametros.dtos;

import com.pazzioliweb.parametros.enums.CategoriaComprobante;

public class ParametroCreateDTO {
    private Long parametroId;
    private String categoriaParametro;
    private CategoriaComprobante categoriaComprobante;
    private String valor;
    private String tipoDato;
    private Integer comprobanteContableId;

    public Long getParametroId() {
        return parametroId;
    }

    public void setParametroId(Long parametroId) {
        this.parametroId = parametroId;
    }

    public String getCategoriaParametro() {
        return categoriaParametro;
    }

    public void setCategoriaParametro(String categoriaParametro) {
        this.categoriaParametro = categoriaParametro;
    }

    public CategoriaComprobante getCategoriaComprobante() {
        return categoriaComprobante;
    }

    public void setCategoriaComprobante(CategoriaComprobante categoriaComprobante) {
        this.categoriaComprobante = categoriaComprobante;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public Integer getComprobanteContableId() {
        return comprobanteContableId;
    }

    public void setComprobanteContableId(Integer comprobanteContableId) {
        this.comprobanteContableId = comprobanteContableId;
    }
}
