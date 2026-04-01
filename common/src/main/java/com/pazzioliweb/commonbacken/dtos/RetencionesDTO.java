package com.pazzioliweb.commonbacken.dtos;

public class RetencionesDTO {
	private Integer retencionId;
	private Integer codigo;
    private String nombre;
    private Double base;
	private Double porcentaje;

    public RetencionesDTO() {}
    
    public RetencionesDTO(Integer id, Integer codigo, String nombre, Double base, Double porcentaje) {
        this.retencionId = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.base = base;
        this.porcentaje = porcentaje;
    }

	public Integer getRetencionId() {
		return retencionId;
	}

	public void setRetencionId(Integer retencionId) {
		this.retencionId = retencionId;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getBase() {
		return base;
	}

	public void setBase(Double base) {
		this.base = base;
	}

	public Double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(Double porcentaje) {
		this.porcentaje = porcentaje;
	}
}
