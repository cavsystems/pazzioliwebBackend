package com.pazzioliweb.productosmodule.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoConVariantesDTO {
    private Integer productoId;
    private String codigoContable;
    private String codigoBarras;
    private String referencia;
    private String descripcion;
    private Double costo;
    private String estado;
    private String imagen;
    private Boolean manejaVariantes;
    private String grupo;
    private String linea;
    private String impuesto;
    private String tipoProducto;
    private List<VarianteDTO> variantes;
}
