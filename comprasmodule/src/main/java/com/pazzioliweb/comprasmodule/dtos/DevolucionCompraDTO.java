package com.pazzioliweb.comprasmodule.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class DevolucionCompraDTO {
    private Long id;
    private Long ordenCompraId;
    private String numeroOrden;
    private String numeroDevolucion;
    private String motivo;
    private String observaciones;
    private String estado;
    private BigDecimal totalDevuelto;
    private BigDecimal ivaDevuelto;
    private BigDecimal retencionDevuelta;
    private BigDecimal totalNeto;
    private String nombreProveedor;
    private String nitProveedor;
    private String usuarioCreacion;
    private LocalDate fechaCreacion;
    private List<Item> items;

    @Data
    public static class Item {
        private Long detalleId;
        private String codigoProducto;
        private String descripcionProducto;
        private Integer cantidadDevuelta;
        private BigDecimal costoUnitario;
        private BigDecimal ivaLinea;
        private BigDecimal totalLinea;
    }
}
