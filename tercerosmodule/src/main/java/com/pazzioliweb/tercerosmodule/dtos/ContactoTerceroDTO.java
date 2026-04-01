package com.pazzioliweb.tercerosmodule.dtos;

public interface ContactoTerceroDTO {
	Integer getContactoId();

    String getValorContacto();

    Boolean getEsPrincipal();

    // Acceso a nombre del tipo de contacto (relación con TipoContacto)
    TipoContactoInfo getTipoContacto();

    interface TipoContactoInfo {
        Integer getTipoContactoId();
        String getNombre();
    }
}
