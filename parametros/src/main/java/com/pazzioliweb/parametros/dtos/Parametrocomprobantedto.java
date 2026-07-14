package com.pazzioliweb.parametros.dtos;

import com.pazzioliweb.parametros.enums.CategoriaComprobante;

public class Parametrocomprobantedto {
    public CategoriaComprobante getCategoriaComprobante() {
        return categoriaComprobante;
    }

    public void setCategoriaComprobante(CategoriaComprobante categoriaComprobante) {
        this.categoriaComprobante = categoriaComprobante;
    }

    public int getComprobanteContableId() {
        return comprobanteContableId;
    }

    public void setComprobanteContableId(int comprobanteContableId) {
        this.comprobanteContableId = comprobanteContableId;
    }

    public int getParametroId() {
        return parametroId;
    }

    public void setParametroId(int parametroId) {
        this.parametroId = parametroId;
    }

    private CategoriaComprobante categoriaComprobante;
    private int comprobanteContableId;
    private int parametroId;

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    private String valor;
}
