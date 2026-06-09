package com.pazzioliweb.empresaback.dtos;

public class EmpresaContactoDTO {
    private String celularempresa;
    private String correoempresa;
    private String telfonofijo;

    public EmpresaContactoDTO() {
    }

    public EmpresaContactoDTO(String celularempresa, String correoempresa, String telfonofijo) {
        this.celularempresa = celularempresa;
        this.correoempresa = correoempresa;
        this.telfonofijo = telfonofijo;
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
}
