package com.pazzioliweb.usuariosbacken.dtos;

// Catálogo de subpermisos (acciones finas dentro de un permiso padre, p. ej.
// "Ventas" -> "anular_factura"). Incluye el nombre del permiso padre para que el
// frontend pueda agrupar el checklist por módulo sin pedirlo aparte.
public class SubPermisoDTO {
    private int codigo;
    private String nombre;
    private String codigoAccion;
    private int permisoPadreId;
    private String permisoPadreNombre;

    public SubPermisoDTO(int codigo, String nombre, String codigoAccion, int permisoPadreId, String permisoPadreNombre) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.codigoAccion = codigoAccion;
        this.permisoPadreId = permisoPadreId;
        this.permisoPadreNombre = permisoPadreNombre;
    }

    public int getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getCodigoAccion() { return codigoAccion; }
    public int getPermisoPadreId() { return permisoPadreId; }
    public String getPermisoPadreNombre() { return permisoPadreNombre; }
}
