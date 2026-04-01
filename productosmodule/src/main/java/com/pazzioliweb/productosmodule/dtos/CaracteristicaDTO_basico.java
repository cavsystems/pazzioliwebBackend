package com.pazzioliweb.productosmodule.dtos;

public class CaracteristicaDTO_basico {
	private Long caracteristicaId;
    private String nombre;

    private Long tipoId;
    private String tipoNombre;

    public CaracteristicaDTO_basico(
        Long caracteristicaId,
        String nombre,
        Long tipoId,
        String tipoNombre
    ) {
        this.caracteristicaId = caracteristicaId;
        this.nombre = nombre;
        this.tipoId = tipoId;
        this.tipoNombre = tipoNombre;
    }

	public Long getCaracteristicaId() {
		return caracteristicaId;
	}

	public void setCaracteristicaId(Long caracteristicaId) {
		this.caracteristicaId = caracteristicaId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getTipoId() {
		return tipoId;
	}

	public void setTipoId(Long tipoId) {
		this.tipoId = tipoId;
	}

	public String getTipoNombre() {
		return tipoNombre;
	}

	public void setTipoNombre(String tipoNombre) {
		this.tipoNombre = tipoNombre;
	}
    
    
}
