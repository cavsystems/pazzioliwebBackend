package com.pazzioliweb.vendedoresmodule.dtos;

import java.time.LocalDate;

public interface VendedorDTO {
	Integer getVendedor_id();
	String getNombre();
	String getDireccion();
	String getTelefono();
	String getEstado();
	Integer getCodigo_usuario_creo();
	LocalDate getFechacreado();
}
