package com.pazzioliweb.ventasmodule.mapper;

import com.pazzioliweb.ventasmodule.dtos.DetallePedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.PedidoDTO;
import com.pazzioliweb.ventasmodule.entity.DetallePedido;
import com.pazzioliweb.ventasmodule.entity.Pedido;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "numeroPedido", source = "numeroPedido")
    @Mapping(target = "clienteId", source = "cliente.terceroId")
    @Mapping(target = "clienteNombre", source = "cliente.nombre1")
    @Mapping(target = "bodegaId", source = "bodega.codigo")
    @Mapping(target = "bodegaNombre", source = "bodega.nombre")
    @Mapping(target = "cajeroId", source = "cajero.cajeroId")
    @Mapping(target = "cajeroNombre", source = "cajero.nombre")
    @Mapping(target = "vendedorId", source = "vendedor.vendedor_id")
    @Mapping(target = "vendedorNombre", source = "vendedor.nombre")
    @Mapping(target = "cotizacionId", source = "cotizacionId")
    @Mapping(target = "fechaEmision", source = "fechaEmision")
    @Mapping(target = "fechaEntregaEsperada", source = "fechaEntregaEsperada")
    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "observaciones", source = "observaciones")
    @Mapping(target = "subtotal", source = "gravada")
    @Mapping(target = "iva", source = "iva")
    @Mapping(target = "total", source = "totalPedido")
    @Mapping(target = "usuarioCreacion", source = "usuarioCreacion")
    @Mapping(target = "fechaCreacion", source = "fechaCreacion")
    @Mapping(target = "items", source = "items")
    PedidoDTO toDto(Pedido pedido);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "numeroPedido", source = "numeroPedido")
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "bodega", ignore = true)
    @Mapping(target = "cajero", ignore = true)
    @Mapping(target = "vendedor", ignore = true)
    @Mapping(target = "cotizacionId", source = "cotizacionId")
    @Mapping(target = "fechaEmision", source = "fechaEmision")
    @Mapping(target = "fechaEntregaEsperada", source = "fechaEntregaEsperada")
    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "observaciones", source = "observaciones")
    @Mapping(target = "gravada", source = "subtotal")
    @Mapping(target = "iva", source = "iva")
    @Mapping(target = "totalPedido", source = "total")
    @Mapping(target = "usuarioCreacion", source = "usuarioCreacion")
    @Mapping(target = "fechaCreacion", source = "fechaCreacion")
    @Mapping(target = "items", source = "items")
    Pedido toEntity(PedidoDTO pedidoDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "codigoProducto", source = "codigoProducto")
    @Mapping(target = "codigoBarras", source = "codigoBarras")
    @Mapping(target = "descripcionProducto", source = "descripcionProducto")
    @Mapping(target = "observacionProducto", source = "observacionProducto")
    @Mapping(target = "referenciaVariantes", source = "referenciaVariantes")
    @Mapping(target = "cantidad", source = "cantidad")
    @Mapping(target = "precioUnitario", source = "precioUnitario")
    @Mapping(target = "descuento", source = "descuento")
    @Mapping(target = "ivaPorcentaje", source = "iva")
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "total", source = "total")
    DetallePedidoDTO detalleToDto(DetallePedido detalle);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "codigoProducto", source = "codigoProducto")
    @Mapping(target = "codigoBarras", source = "codigoBarras")
    @Mapping(target = "descripcionProducto", source = "descripcionProducto")
    @Mapping(target = "observacionProducto", source = "observacionProducto")
    @Mapping(target = "referenciaVariantes", source = "referenciaVariantes")
    @Mapping(target = "cantidad", source = "cantidad")
    @Mapping(target = "precioUnitario", source = "precioUnitario")
    @Mapping(target = "descuento", source = "descuento")
    @Mapping(target = "iva", source = "ivaPorcentaje")
    @Mapping(target = "total", source = "total")
    @Mapping(target = "pedido", ignore = true)
    DetallePedido detalleToEntity(DetallePedidoDTO detalleDTO);

    List<DetallePedidoDTO> toDetalleDtoList(List<DetallePedido> detalles);

    @AfterMapping
    default void setPedidoInDetalles(@MappingTarget Pedido pedido) {
        if (pedido.getItems() != null) {
            pedido.getItems().forEach(detalle -> detalle.setPedido(pedido));
        }
    }
}

