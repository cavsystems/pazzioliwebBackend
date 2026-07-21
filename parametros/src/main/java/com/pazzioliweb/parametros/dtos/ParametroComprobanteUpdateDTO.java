package com.pazzioliweb.parametros.dtos;

public class ParametroComprobanteUpdateDTO {
    private String valor;
    private String prefijo;

    public ParametroComprobanteUpdateDTO() {
    }

    public ParametroComprobanteUpdateDTO(String valor, String prefijo) {
        this.valor = valor;
        this.prefijo = prefijo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }
}
