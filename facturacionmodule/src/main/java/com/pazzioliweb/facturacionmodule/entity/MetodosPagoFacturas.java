package com.pazzioliweb.facturacionmodule.entity;

import java.math.BigDecimal;

import com.pazzioliweb.metodospagomodule.entity.MetodosPago;

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
@Table(name = "metodos_pago_facturas")
@Data
@EqualsAndHashCode(exclude = {"factura", "metodopago"})
public class MetodosPagoFacturas {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metodo_pago_factura_id")
    private Integer metodoPagoFacturaId;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodo_pago_id", nullable = false)
    private MetodosPago metodopago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Facturas factura;

    @Column(name = "valor", precision = 10, scale = 2)
    private BigDecimal valor = BigDecimal.ZERO;

	public Integer getMetodoPagoFacturaId() {
		return metodoPagoFacturaId;
	}

	public void setMetodoPagoFacturaId(Integer metodoPagoFacturaId) {
		this.metodoPagoFacturaId = metodoPagoFacturaId;
	}

	public MetodosPago getMetodopago() {
		return metodopago;
	}

	public void setMetodopago(MetodosPago metodopago) {
		this.metodopago = metodopago;
	}

	public Facturas getFactura() {
		return factura;
	}

	public void setFactura(Facturas factura) {
		this.factura = factura;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
}
