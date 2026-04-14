package com.pazzioliweb.ventasmodule.mapper;

import com.pazzioliweb.ventasmodule.dtos.DetalleVentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import com.pazzioliweb.ventasmodule.entity.DetalleVenta;
import com.pazzioliweb.ventasmodule.entity.Venta;
import com.pazzioliweb.ventasmodule.entity.VentaMetodoPago;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VentaMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "numeroVenta", source = "numeroVenta")
    @Mapping(target = "clienteId", source = "cliente.terceroId")
    @Mapping(target = "clienteNombre", expression = "java(buildNombreCompleto(venta.getCliente()))")
    @Mapping(target = "bodegaId", source = "bodega.codigo")
    @Mapping(target = "bodegaNombre", source = "bodega.nombre")
    @Mapping(target = "cajeroId", source = "cajero.cajeroId")
    @Mapping(target = "cajeroNombre", source = "cajero.nombre")
    @Mapping(target = "vendedorId", source = "vendedor.vendedor_id")
    @Mapping(target = "vendedorNombre", source = "vendedor.nombre")
    @Mapping(target = "fechaEmision", source = "fechaEmision")
    @Mapping(target = "fechaEntregaEsperada", source = "fechaEntregaEsperada")
    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "observaciones", source = "observaciones")
    @Mapping(target = "subtotal", source = "gravada")
    @Mapping(target = "iva", source = "iva")
    @Mapping(target = "total", source = "totalVenta")
    @Mapping(target = "usuarioCreacion", source = "usuarioCreacion")
    @Mapping(target = "fechaCreacion", source = "fechaCreacion")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "metodosPago", source = "metodosPago")
    VentaDTO toDto(Venta venta);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "numeroVenta", source = "numeroVenta")
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "bodega", ignore = true)
    @Mapping(target = "cajero", ignore = true)
    @Mapping(target = "vendedor", ignore = true)
    @Mapping(target = "fechaEmision", source = "fechaEmision")
    @Mapping(target = "fechaEntregaEsperada", source = "fechaEntregaEsperada")
    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "observaciones", source = "observaciones")
    @Mapping(target = "gravada", source = "subtotal")
    @Mapping(target = "iva", source = "iva")
    @Mapping(target = "totalVenta", source = "total")
    @Mapping(target = "usuarioCreacion", source = "usuarioCreacion")
    @Mapping(target = "fechaCreacion", source = "fechaCreacion")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "metodosPago", ignore = true)
    Venta toEntity(VentaDTO ventaDTO);

    // ---- Detalle Venta mappings ----

    @Mapping(target = "id", source = "id")
    @Mapping(target = "codigoProducto", source = "codigoProducto")
    @Mapping(target = "codigoBarras", source = "codigoBarras")
    @Mapping(target = "descripcionProducto", source = "descripcionProducto")
    @Mapping(target = "referenciaVariantes", source = "referenciaVariantes")
    @Mapping(target = "cantidad", source = "cantidad")
    @Mapping(target = "precioUnitario", source = "precioUnitario")
    @Mapping(target = "descuento", source = "descuento")
    @Mapping(target = "ivaPorcentaje", source = "iva")
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "total", source = "total")
    @Mapping(target = "precioAjustado", ignore = true)
    DetalleVentaDTO detalleToDto(DetalleVenta detalle);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "codigoProducto", source = "codigoProducto")
    @Mapping(target = "codigoBarras", source = "codigoBarras")
    @Mapping(target = "descripcionProducto", source = "descripcionProducto")
    @Mapping(target = "referenciaVariantes", source = "referenciaVariantes")
    @Mapping(target = "cantidad", source = "cantidad")
    @Mapping(target = "precioUnitario", source = "precioUnitario")
    @Mapping(target = "descuento", source = "descuento")
    @Mapping(target = "iva", source = "ivaPorcentaje")
    @Mapping(target = "total", source = "total")
    @Mapping(target = "venta", ignore = true)
    @Mapping(target = "observacionProducto", ignore = true)
    DetalleVenta detalleToEntity(DetalleVentaDTO detalleDTO);

    List<DetalleVentaDTO> toDetalleDtoList(List<DetalleVenta> detalles);

    // ---- VentaMetodoPago mappings ----

    @Mapping(target = "id", source = "id")
    @Mapping(target = "metodoPagoId", source = "metodoPago.metodo_pago_id")
    @Mapping(target = "metodoPagoNombre", source = "metodoPago.descripcion")
    @Mapping(target = "monto", source = "monto")
    @Mapping(target = "referencia", source = "referencia")
    @Mapping(target = "plazoEnDias", source = "plazoEnDias")
    VentaMetodoPagoDTO ventaMetodoPagoToDto(VentaMetodoPago entity);

    List<VentaMetodoPagoDTO> toVentaMetodoPagoDtoList(List<VentaMetodoPago> entities);

    @AfterMapping
    default void setVentaInDetalles(@MappingTarget Venta venta) {
        if (venta.getItems() != null) {
            venta.getItems().forEach(detalle -> detalle.setVenta(venta));
        }
    }

    default String buildNombreCompleto(Terceros cliente) {
        if (cliente == null) return null;
        StringBuilder sb = new StringBuilder();
        if (cliente.getNombre1() != null && !cliente.getNombre1().isEmpty()) {
            sb.append(cliente.getNombre1());
        }
        if (cliente.getNombre2() != null && !cliente.getNombre2().isEmpty()) {
            sb.append(" ").append(cliente.getNombre2());
        }
        return sb.length() > 0 ? sb.toString().trim() : null;
    }
}
