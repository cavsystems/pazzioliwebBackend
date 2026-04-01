package com.pazzioliweb.cajerosmodule.entity;

import java.time.LocalDate;

import com.pazzioliweb.usuariosbacken.entity.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cajeros")
@Data
public class Cajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cajero_id")
    private Integer cajeroId;

    // ✅ Relación directa con Usuario (one-to-one: cada cajero pertenece a un único usuario)
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCajero estado = EstadoCajero.ACTIVO;

    @Column(name = "codigo_usuario_creo", nullable = false)
    private Integer codigoUsuarioCreo = 0;

    @Column(name = "fechacreado", nullable = false, updatable = false)
    private LocalDate fechacreado = LocalDate.now();

    public enum EstadoCajero {
        ACTIVO, INACTIVO
    }

    public Integer getCajeroId() { return cajeroId; }
    public void setCajeroId(Integer cajeroId) { this.cajeroId = cajeroId; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public EstadoCajero getEstado() { return estado; }
    public void setEstado(EstadoCajero estado) { this.estado = estado; }

    public Integer getCodigoUsuarioCreo() { return codigoUsuarioCreo; }
    public void setCodigoUsuarioCreo(Integer codigoUsuarioCreo) { this.codigoUsuarioCreo = codigoUsuarioCreo; }

    public LocalDate getFechacreado() { return fechacreado; }
    public void setFechacreado(LocalDate fechacreado) { this.fechacreado = fechacreado; }
}
