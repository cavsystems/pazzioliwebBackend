package com.pazzioliweb.tercerosmodule.dtos;

public interface SedeTerceroDTO {
	Integer getSedeId();

    String getNombreSede();

    String getDireccion();

    String getTelefono();

    Boolean getPrincipal();

    Boolean getActivo();

    DepartamentoInfo getDepartamento();

    MunicipioInfo getMunicipio();

    interface DepartamentoInfo {
        Integer getDepartamentoId();
        String getNombre();
    }

    interface MunicipioInfo {
        Integer getMunicipioId();
        String getNombre();
    }
}
