package com.pazzioliweb.facturacionmodule.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "tipo_totales_facturas")
@Data
@EqualsAndHashCode(exclude = {"factura"})
public class TipoTotalesFacturas {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipo_total_factura_id")
    private Integer tipoTotalFacturaId;

    @Column(name = "tipo_total_id", nullable = false)
    private Integer tipoTotalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Facturas factura;

    @Column(name = "base", precision = 10, scale = 2)
    private BigDecimal base = BigDecimal.ZERO;

    @Column(name = "valor", precision = 10, scale = 2)
    private BigDecimal valor = BigDecimal.ZERO;

	public Integer getTipoTotalFacturaId() {
		return tipoTotalFacturaId;
	}

	public void setTipoTotalFacturaId(Integer tipoTotalFacturaId) {
		this.tipoTotalFacturaId = tipoTotalFacturaId;
	}

	public Integer getTipoTotalId() {
		return tipoTotalId;
	}

	public void setTipoTotalId(Integer tipoTotalId) {
		this.tipoTotalId = tipoTotalId;
	}

	public Facturas getFactura() {
		return factura;
	}

	public void setFactura(Facturas factura) {
		this.factura = factura;
	}

	public BigDecimal getBase() {
		return base;
	}

	public void setBase(BigDecimal base) {
		this.base = base;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}    
}
