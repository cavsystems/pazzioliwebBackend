package com.pazzioliweb.parametros.dtos;

public class ComprobanteContableSimpleDTO {
    private Long id;
    private String tipoMovimiento;
    private String descripcionTipo;
    private String prefijo;
    private String descripcion;

    public ComprobanteContableSimpleDTO(Long id, String tipoMovimiento, String descripcionTipo, String prefijo, String descripcion) {
        this.id = id;
        this.tipoMovimiento = tipoMovimiento;
        this.descripcionTipo = descripcionTipo;
        this.prefijo = prefijo;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public String getDescripcionTipo() { return descripcionTipo; }
    public void setDescripcionTipo(String descripcionTipo) { this.descripcionTipo = descripcionTipo; }

    public String getPrefijo() { return prefijo; }
    public void setPrefijo(String prefijo) { this.prefijo = prefijo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
