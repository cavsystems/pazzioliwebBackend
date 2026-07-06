package com.pazzioliweb.empresaback.dtos;

public class EmpresaContactoDTO {
    private String celularempresa;
    private String correoempresa;
    private String telfonofijo;
    private String direccion;

    public EmpresaContactoDTO() {
    }

    public EmpresaContactoDTO(String celularempresa, String correoempresa, String telfonofijo, String direccion) {
        this.celularempresa = celularempresa;
        this.correoempresa = correoempresa;
        this.telfonofijo = telfonofijo;
        this.direccion = direccion;
    }

    public String getCelularempresa() {
        return celularempresa;
    }

    public void setCelularempresa(String celularempresa) {
        this.celularempresa = celularempresa;
    }

    public String getCorreoempresa() {
        return correoempresa;
    }

    public void setCorreoempresa(String correoempresa) {
        this.correoempresa = correoempresa;
    }

    public String getTelfonofijo() {
        return telfonofijo;
    }

    public void setTelfonofijo(String telfonofijo) {
        this.telfonofijo = telfonofijo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
