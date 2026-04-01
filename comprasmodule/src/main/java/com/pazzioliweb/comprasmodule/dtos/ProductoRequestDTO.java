package com.pazzioliweb.comprasmodule.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductoRequestDTO {
    private Integer tipoProductoId;
    private String descripcion;
    private String referencia;
    private Integer unidadMedidaId;
    private Integer impuesto;
    private BigDecimal costo;
    private Integer lineaId;
    private Integer grupoId;
    private String codigoBarras;
    private String ubicacion;
    private StockDTO stock;
    private List<PrecioDTO> precios;
    private List<VarianteDTO> variantes;

    @Data
    public static class StockDTO {
        private Integer minimo;
        private Integer maximo;
    }

    @Data
    public static class PrecioDTO {
        private Integer idTipoPrecio;
        private BigDecimal valor;
    }

    @Data
    public static class VarianteDTO {
        private String codigoBarraVariante;
        private Integer cantidad;
        private List<AtributoDTO> atributos;
        private List<String> notas;
    }

    @Data
    public static class AtributoDTO {
        private String nombre;
        private String valor;
    }
}
