package com.pazzioliweb.comprasmodule.dtos;

import lombok.Data;

import java.util.List;

@Data
public class DevolucionCompraRequestDTO {
    private Long ordenCompraId;
    private String motivo;
    private String observaciones;
    private String usuario;
    private List<ItemDevolucionCompra> items;

    @Data
    public static class ItemDevolucionCompra {
        private Long detalleId;       // id de DetalleOrdenCompra
        private Integer cantidadDevolver;
        private String motivo;
    }
}
