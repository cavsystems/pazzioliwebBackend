package com.pazzioliweb.metodospagomodule.dtos;

public interface MetodoPagoDTO {
	Integer getMetodo_pago_id();
	String getDescripcion();
	String getSigla();
	String getEstado();
	String getTipoNegociacion();
	String getTipos();
	Long getCuentaBancariaId();
	String getCuentaBancariaNombre();
	Integer getCuentaContableId();
	String getCuentaContableCodigo();
	String getCuentaContableNombre();
}
