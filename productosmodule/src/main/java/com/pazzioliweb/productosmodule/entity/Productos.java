package com.pazzioliweb.productosmodule.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pazzioliweb.commonbacken.entity.Impuestos;
import com.pazzioliweb.usuariosbacken.entity.Usuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Productos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Integer productoId;
    
    @Column(name = "codigo_contable",unique = true)
    private String codigoContable;

    @Column(name = "codigo_barras",unique = true)
    private String codigoBarras;

    private String referencia;
    private String descripcion;
    private Double costo;

    @ManyToOne
    @JoinColumn(name = "impuesto_id", nullable = false)
    private Impuestos impuestos;

    @ManyToOne
    @JoinColumn(name = "linea_id", nullable = false)
    private Lineas linea;

    @ManyToOne
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupos grupo;

    @ManyToOne
    @JoinColumn(name = "usuario_creo_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
    }
    
    @Column(name = "codigo_usuario_modifico")
    private Integer codigoUsuarioModifico;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    private String estado;

    @Column(name = "fecha_ultima_venta")
    private LocalDateTime fechaUltimaVenta;

    @Column(name = "fecha_ultima_compra")
    private LocalDateTime fechaUltimaCompra;
	
    String manifiesto;
    
    @ManyToOne
    @JoinColumn(name = "tipo_producto_id", nullable = false)
    private TipoProducto tipoProducto;
	
  
	public Integer getProductoId() {
		return productoId;
	}

	public void setProductoId(Integer productoId) {
		this.productoId = productoId;
	}

	public String getCodigoContable() {
		return codigoContable;
	}

	public void setCodigoContable(String codigoContable) {
		this.codigoContable = codigoContable;
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Integer getCodigoUsuarioModifico() {
		return codigoUsuarioModifico;
	}

	public void setCodigoUsuarioModifico(Integer codigoUsuarioModifico) {
		this.codigoUsuarioModifico = codigoUsuarioModifico;
	}

	public LocalDateTime getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(LocalDateTime fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public LocalDateTime getFechaUltimaVenta() {
		return fechaUltimaVenta;
	}

	public void setFechaUltimaVenta(LocalDateTime fechaUltimaVenta) {
		this.fechaUltimaVenta = fechaUltimaVenta;
	}

	public LocalDateTime getFechaUltimaCompra() {
		return fechaUltimaCompra;
	}

	public void setFechaUltimaCompra(LocalDateTime fechaUltimaCompra) {
		this.fechaUltimaCompra = fechaUltimaCompra;
	}

	public void setImpuestos(Impuestos impuestos) {
		this.impuestos = impuestos;
	}

	public void setLinea(Lineas linea) {
		this.linea = linea;
	}

	public void setGrupo(Grupos grupo) {
		this.grupo = grupo;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public String getManifiesto() {
		return manifiesto;
	}

	public void setManifiesto(String manifiesto) {
		this.manifiesto = manifiesto;
	}

	@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductoVariante> variantes = new ArrayList<>();
    
	@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UnidadesMedidaProducto> unidadesMedidaProducto = new ArrayList<>();
	
    public Impuestos getImpuestos() { return impuestos; }
    public Lineas getLinea() { return linea; }
    public Grupos getGrupo() { return grupo; }
    public Usuario getUsuario() { return usuario; }
    
    @Column(name = "maneja_variantes")
    private Boolean manejaVariantes = false;
    

	public List<ProductoVariante> getVariantes() {
		return variantes;
	}

	public void setVariantes(List<ProductoVariante> variantes) {
		this.variantes = variantes;
	}

	public List<UnidadesMedidaProducto> getUnidadesMedidaProducto() {
		return unidadesMedidaProducto;
	}

	public void setUnidadesMedidaProducto(List<UnidadesMedidaProducto> unidadesMedidaProducto) {
		this.unidadesMedidaProducto = unidadesMedidaProducto;
	}

	public Boolean getManejaVariantes() {
		return manejaVariantes;
	}

	public void setManejaVariantes(Boolean manejaVariantes) {
		this.manejaVariantes = manejaVariantes;
	}

	public TipoProducto getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(TipoProducto tipoProducto) {
		this.tipoProducto = tipoProducto;
	}
	
}
