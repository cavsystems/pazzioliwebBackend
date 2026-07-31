package com.pazzioliweb.usuariosbacken.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "subpermisos_roles")
public class SubPermisoRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigorol", nullable = false)
    private Roles codigorol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codigosubpermiso", nullable = false)
    private SubPermiso codigosubpermiso;

    @Column(length = 20, nullable = false)
    private String estado = "ACTIVO";

    @Column(name = "codigousuariocreado")
    private int codigousuariocreado = 0;

    @Column(name = "fechacreado")
    private LocalDateTime fechacreado = LocalDateTime.now();

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public Roles getCodigorol() { return codigorol; }
    public void setCodigorol(Roles codigorol) { this.codigorol = codigorol; }

    public SubPermiso getCodigosubpermiso() { return codigosubpermiso; }
    public void setCodigosubpermiso(SubPermiso codigosubpermiso) { this.codigosubpermiso = codigosubpermiso; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getCodigousuariocreado() { return codigousuariocreado; }
    public void setCodigousuariocreado(int codigousuariocreado) { this.codigousuariocreado = codigousuariocreado; }

    public LocalDateTime getFechacreado() { return fechacreado; }
    public void setFechacreado(LocalDateTime fechacreado) { this.fechacreado = fechacreado; }
}
