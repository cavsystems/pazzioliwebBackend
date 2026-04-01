package com.pazzioliweb.comprasmodule.dtos;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductoActualizarCrearDTO {
    private String codigo;
    @JsonProperty("tipo_producto")
    private String tipoProducto;
    private String descripcion;
    private String referencia;
    @JsonProperty("unidad_medida")
    private String unidadMedida;
    private Integer impuesto;
    private BigDecimal costo;
    private String linea;
    private String grupo;
    @JsonProperty("codigobarras")
    private String codigoBarras;
    private String ubicacion;
    @JsonProperty("maneja_variantes")
    private String manejaVariantes;
    private List<PrecioDTO> precios;
    private List<VarianteDTO> variantes;

    @Data
    public static class VarianteDTO {
        @JsonProperty("codigobarravariante")
        private String codigoBarraVariante;
        private String sku;
        private String referenciaVariantes;
        private Integer cantidad;
        private List<AtributoDTO> atributos;
        private List<String> notas;
        private List<ExistenciaDTO> existencias;
        private BigDecimal descuento;
        private Boolean predeterminada;
        private List<PrecioDTO> precios;
    }

    @Data
    public static class ExistenciaDTO {
        private Long existenciaId;
        private Integer bodegaId;
        private String bodega;
        private Integer minimo;
        private Integer maximo;
        private Integer cantidad;
    }

    @Data
    public static class AtributoDTO {
        private String nombre;
        private String valor;
    }

    @Data
    public static class PrecioDTO {
        @JsonProperty("id_tipo_precio")
        private Integer idTipoPrecio;
        private BigDecimal valor;
    }
}
