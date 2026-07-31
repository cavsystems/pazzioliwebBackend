package com.pazzioliweb.usuariosbacken.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "subpermisos")
public class SubPermiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_padre_id", nullable = false)
    private Permiso permisoPadre;

    @Column(name = "codigo_accion", length = 50, nullable = false)
    private String codigoAccion;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private boolean activo = true;

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public Permiso getPermisoPadre() { return permisoPadre; }
    public void setPermisoPadre(Permiso permisoPadre) { this.permisoPadre = permisoPadre; }

    public String getCodigoAccion() { return codigoAccion; }
    public void setCodigoAccion(String codigoAccion) { this.codigoAccion = codigoAccion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
