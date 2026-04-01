package com.pazzioliweb.facturacionmodule.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "facturas")
@Data
public class Facturas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "factura_id")
    private Integer facturaId;

    @Column(nullable = false)
    private Integer consecutivo;

    @Column(name = "comprobante_id", nullable = false)
    private Integer comprobanteId;

    @Column(name = "tercero_id", nullable = false)
    private Integer terceroId;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false)
    private Integer plazo = 0;

    @Column(name = "usuario_ingreso_id", nullable = false)
    private Integer usuarioIngresoId;

    @Column(name = "fecha_anulo")
    private LocalDateTime fechaAnulo;

    @Column(name = "usuario_anulo_id")
    private Integer usuarioAnuloId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado = EstadoFactura.ACTIVO;

    @Column(name = "pedido_id")
    private Integer pedidoId;

    @Column(name = "remision_id")
    private Integer remisionId;

    @Column(name = "vendedor_id")
    private Integer vendedorId;

    @Column
    private Double descuento = 0.00;

    @Column(name = "caja_id")
    private Integer cajaId;

    @Column(length = 255)
    private String observaciones;

    @Column
    private Double saldo = 0.00;

    @Column(name = "total_factura")
    private Double totalFactura = 0.00;

    // ── Campos Facturación Electrónica DIAN ──

    @Column(name = "venta_id")
    private Long ventaId;

    @Column(name = "prefijo", length = 10)
    private String prefijo;

    @Column(name = "numero_factura", length = 20)
    private String numeroFactura; // prefijo + consecutivo (ej: FE154)

    @Column(name = "cufe", length = 96)
    private String cufe; // Código Único de Factura Electrónica

    @Column(name = "qr_data", columnDefinition = "TEXT")
    private String qrData;

    @Column(name = "xml_firmado", columnDefinition = "LONGTEXT")
    private String xmlFirmado;

    @Column(name = "pdf_base64", columnDefinition = "LONGTEXT")
    private String pdfBase64;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_dian", length = 20)
    private EstadoDian estadoDian;

    @Column(name = "mensaje_dian", length = 500)
    private String mensajeDian;

    @Column(name = "fecha_validacion_dian")
    private LocalDateTime fechaValidacionDian;

    @Column(name = "resolucion_dian", length = 50)
    private String resolucionDian;

    public enum EstadoFactura {
        ACTIVO,
        INACTIVO
    }

    public enum EstadoDian {
        PENDIENTE,
        ENVIADA,
        AUTORIZADA,
        RECHAZADA
    }
    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TipoTotalesFacturas> tipoTotales = new HashSet<>();

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MetodosPagoFacturas> metodosPago = new HashSet<>();

	public Integer getFacturaId() {
		return facturaId;
	}

	public void setFacturaId(Integer facturaId) {
		this.facturaId = facturaId;
	}

	public Integer getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(Integer consecutivo) {
		this.consecutivo = consecutivo;
	}

	public Integer getComprobanteId() {
		return comprobanteId;
	}

	public void setComprobanteId(Integer comprobanteId) {
		this.comprobanteId = comprobanteId;
	}

	public Integer getTerceroId() {
		return terceroId;
	}

	public void setTerceroId(Integer terceroId) {
		this.terceroId = terceroId;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public LocalDate getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(LocalDate fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public LocalDate getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(LocalDate fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public Integer getPlazo() {
		return plazo;
	}

	public void setPlazo(Integer plazo) {
		this.plazo = plazo;
	}

	public Integer getUsuarioIngresoId() {
		return usuarioIngresoId;
	}

	public void setUsuarioIngresoId(Integer usuarioIngresoId) {
		this.usuarioIngresoId = usuarioIngresoId;
	}

	public LocalDateTime getFechaAnulo() {
		return fechaAnulo;
	}

	public void setFechaAnulo(LocalDateTime fechaAnulo) {
		this.fechaAnulo = fechaAnulo;
	}

	public Integer getUsuarioAnuloId() {
		return usuarioAnuloId;
	}

	public void setUsuarioAnuloId(Integer usuarioAnuloId) {
		this.usuarioAnuloId = usuarioAnuloId;
	}

	public EstadoFactura getEstado() {
		return estado;
	}

	public void setEstado(EstadoFactura estado) {
		this.estado = estado;
	}

	public Integer getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(Integer pedidoId) {
		this.pedidoId = pedidoId;
	}

	public Integer getRemisionId() {
		return remisionId;
	}

	public void setRemisionId(Integer remisionId) {
		this.remisionId = remisionId;
	}

	public Integer getVendedorId() {
		return vendedorId;
	}

	public void setVendedorId(Integer vendedorId) {
		this.vendedorId = vendedorId;
	}

	public Double getDescuento() {
		return descuento;
	}

	public void setDescuento(Double descuento) {
		this.descuento = descuento;
	}

	public Integer getCajaId() {
		return cajaId;
	}

	public void setCajaId(Integer cajaId) {
		this.cajaId = cajaId;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Double getSaldo() {
		return saldo;
	}

	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}

	public Double getTotalFactura() {
		return totalFactura;
	}

	public void setTotalFactura(Double totalFactura) {
		this.totalFactura = totalFactura;
	}

	public Set<TipoTotalesFacturas> getTipoTotales() {
		return tipoTotales;
	}

	public void setTipoTotales(Set<TipoTotalesFacturas> tipoTotales) {
		this.tipoTotales = tipoTotales;
	}

	public Set<MetodosPagoFacturas> getMetodosPago() {
		return metodosPago;
	}

	public void setMetodosPago(Set<MetodosPagoFacturas> metodosPago) {
		this.metodosPago = metodosPago;
	}
    
    
}
