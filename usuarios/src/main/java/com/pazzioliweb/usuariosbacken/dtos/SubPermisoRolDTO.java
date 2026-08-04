package com.pazzioliweb.usuariosbacken.dtos;

// Subpermiso ya asignado a un rol (equivalente a PermisosrolesDTOS pero para subpermisos).
public class SubPermisoRolDTO {
    private int codigo;
    private int codigoSubpermiso;
    private String nombreSubpermiso;
    private int codigoPermisoPadre;
    private String nombrePermisoPadre;

    public SubPermisoRolDTO(int codigo, int codigoSubpermiso, String nombreSubpermiso,
                             int codigoPermisoPadre, String nombrePermisoPadre) {
        this.codigo = codigo;
        this.codigoSubpermiso = codigoSubpermiso;
        this.nombreSubpermiso = nombreSubpermiso;
        this.codigoPermisoPadre = codigoPermisoPadre;
        this.nombrePermisoPadre = nombrePermisoPadre;
    }

    public int getCodigo() { return codigo; }
    public int getCodigoSubpermiso() { return codigoSubpermiso; }
    public String getNombreSubpermiso() { return nombreSubpermiso; }
    public int getCodigoPermisoPadre() { return codigoPermisoPadre; }
    public String getNombrePermisoPadre() { return nombrePermisoPadre; }
}
