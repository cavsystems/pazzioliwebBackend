package com.pazzioliweb.cajerosmodule.dtos;

/**
 * DTO para crear o asociar un cajero a un usuario.
 * Se recibe en el endpoint POST /api/cajeros/asociar
 */
public class CajeroRequestDTO {

    /** ID del usuario (tabla usuarios.codigo) al que se le asigna el cajero */
    private Integer usuarioId;

    /** Nombre descriptivo del puesto de caja (ej: "Caja 1", "Caja Principal") */
    private String nombre;

    /** Estado inicial: ACTIVO | INACTIVO. Por defecto ACTIVO */
    private String estado;

    /** Código del usuario que está creando el registro */
    private Integer codigoUsuarioCreo;

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getCodigoUsuarioCreo() { return codigoUsuarioCreo; }
    public void setCodigoUsuarioCreo(Integer codigoUsuarioCreo) { this.codigoUsuarioCreo = codigoUsuarioCreo; }
}

