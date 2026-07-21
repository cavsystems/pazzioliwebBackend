package com.pazzioliweb.parametros.dtos;

public class ParametroGlobalUpdateDTO {
    private String valor;

    public ParametroGlobalUpdateDTO() {
    }

    public ParametroGlobalUpdateDTO(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
