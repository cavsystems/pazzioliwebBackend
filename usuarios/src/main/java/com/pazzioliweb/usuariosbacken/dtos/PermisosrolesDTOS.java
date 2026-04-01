package com.pazzioliweb.usuariosbacken.dtos;

public class PermisosrolesDTOS {
    private Integer codigo;
    private  Integer codigopermiso;
    private String nombrePermiso;
    private String nombreRol;

    public PermisosrolesDTOS(Integer codigo, Integer codigopermiso, String nombrePermiso, String nombreRol) {
        this.codigo = codigo;
        this.codigopermiso = codigopermiso;
        this.nombrePermiso = nombrePermiso;
        this.nombreRol = nombreRol;
    }

	public Integer getCodigo() {
		return codigo;
	}

	public Integer getCodigopermiso() {
		return codigopermiso;
	}

	public String getNombrePermiso() {
		return nombrePermiso;
	}

	public String getNombreRol() {
		return nombreRol;
	}

    // getters
}