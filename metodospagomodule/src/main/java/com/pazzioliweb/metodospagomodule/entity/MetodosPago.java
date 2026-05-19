package com.pazzioliweb.metodospagomodule.entity;

import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "metodos_pago")
@Data
public class MetodosPago {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metodo_pago_id")
    private Integer metodo_pago_id;
	
	@Column(name = "descripcion", nullable = false, length = 50)
    private String descripcion;
	
	@Column(name = "sigla", nullable = false, length = 50)
    private String sigla;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private Estado estado = Estado.ACTIVO;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "tipo_negociacion", nullable = false)
	private TipoNegociacion tipoNegociacion = TipoNegociacion.Contado;

	/** CSV con los módulos donde aplica este método: "RECIBO,EGRESO,VENTA". */
	@Column(name = "tipos", nullable = false, length = 100)
	private String tipos = "RECIBO,EGRESO,VENTA";

	/**
	 * Cuenta bancaria a la que entra/sale el dinero cuando se usa este método.
	 * Aplica solo a métodos electrónicos (tarjeta, transferencia). Para efectivo
	 * queda NULL — se asume que va a la cuenta contable de caja directamente.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cuenta_bancaria_id")
	private CuentaBancaria cuentaBancaria;

	/**
	 * Cuenta contable a la que afecta este método de pago.
	 * Si tiene cuenta bancaria, esta cuenta puede heredarse de la cuenta bancaria.
	 * Si es efectivo, apunta a la cuenta de caja del PUC (típicamente 1105).
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cuenta_contable_id")
	private CuentaContable cuentaContable;

	public enum Estado {
        ACTIVO, INACTIVO
    }
	
	public enum TipoNegociacion{
		Contado,Credito
	}

	public Integer getMetodo_pago_id() {
		return metodo_pago_id;
	}

	public void setMetodo_pago_id(Integer metodo_pago_id) {
		this.metodo_pago_id = metodo_pago_id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public TipoNegociacion getTipoNegociacion() {
		return tipoNegociacion;
	}

	public void setTipoNegociacion(TipoNegociacion tipoNegociacion) {
		this.tipoNegociacion = tipoNegociacion;
	}

	public String getTipos() {
		return tipos;
	}

	public void setTipos(String tipos) {
		this.tipos = tipos;
	}
}
