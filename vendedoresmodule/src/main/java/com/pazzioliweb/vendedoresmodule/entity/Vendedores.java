package com.pazzioliweb.vendedoresmodule.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.pazzioliweb.productosmodule.entity.Bodegas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "vendedores")
@Data
public class Vendedores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendedor_id") // Ojo: aquí respeto tu tabla, aunque parezca un error
    private Integer vendedor_id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "direccion", nullable = false, length = 50)
    private String direccion;

    @Column(name = "telefono", nullable = false, length = 50)
    private String telefono;

    @Column(name = "identificacion", length = 50)
    private String identificacion;

    @Column(name = "correo", length = 100)
    private String correo;

    @Column(name = "comision")
    private Double comision = 0.0;

    @Column(name = "meta_ventas")
    private Double meta_ventas = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vendedor")
    private TipoVendedor tipo_vendedor = TipoVendedor.INTERNO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private Estado estado = Estado.ACTIVO;

    @Column(name = "codigo_usuario_creo", nullable = false)
    private Integer codigo_usuario_creo = 0;

    @Column(name = "fechacreado", nullable = false, updatable = false)
    private LocalDate fechacreado = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "bodega_id")
    private Bodegas bodega;

    @OneToMany(mappedBy = "vendedor")
    @JsonIgnore
    private List<Usuariosvendedor> usuariosVendedores;

    public enum Estado {
        ACTIVO, INACTIVO
    }

    public enum TipoVendedor {
        INTERNO, EXTERNO
    }

    
    public Integer getVendedor_id() {
        return vendedor_id;
    }

    public void setVendedor_id(Integer vendedor_id) {
        this.vendedor_id = vendedor_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Double getComision() {
        return comision;
    }

    public void setComision(Double comision) {
        this.comision = comision;
    }

    public Double getMeta_ventas() {
        return meta_ventas;
    }

    public void setMeta_ventas(Double meta_ventas) {
        this.meta_ventas = meta_ventas;
    }

    public TipoVendedor getTipo_vendedor() {
        return tipo_vendedor;
    }

    public void setTipo_vendedor(TipoVendedor tipo_vendedor) {
        this.tipo_vendedor = tipo_vendedor;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Integer getCodigo_usuario_creo() {
        return codigo_usuario_creo;
    }

    public void setCodigo_usuario_creo(Integer codigo_usuario_creo) {
        this.codigo_usuario_creo = codigo_usuario_creo;
    }

    public LocalDate getFechacreado() {
        return fechacreado;
    }

    public void setFechacreado(LocalDate fechacreado) {
        this.fechacreado = fechacreado;
    }

    public Bodegas getBodega() {
        return bodega;
    }

    public void setBodega(Bodegas bodega) {
        this.bodega = bodega;
    }
}
