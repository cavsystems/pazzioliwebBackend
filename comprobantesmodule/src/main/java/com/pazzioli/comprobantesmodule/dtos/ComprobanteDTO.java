package com.pazzioli.comprobantesmodule.dtos;

public interface ComprobanteDTO {
	Integer getComprobante_id();
    String getNombre();
    CategoriaComprobanteDTO getCategoriaComprobante();
    Integer getInicio_consecutivo();
    String getAfecta_inventario();
}
