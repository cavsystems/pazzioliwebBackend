package com.pazzioliweb.comprasmodule.dtos;

import lombok.Data;

@Data
public class LegalizacionRequestDTO {
    private RealizarOrdenRequestDTO ordenCompraData;  // Reutiliza toda la estructura de orden
    private String numeroFactura;
    private String fechaFactura;
}