package com.pazzioliweb.ventasmodule.mapper;

import com.pazzioliweb.ventasmodule.dtos.CotizacionDTO;
import com.pazzioliweb.ventasmodule.dtos.DetalleCotizacionDTO;
import com.pazzioliweb.ventasmodule.entity.Cotizacion;
import com.pazzioliweb.ventasmodule.entity.DetalleCotizacion;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CotizacionMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "numeroCotizacion", source = "numeroCotizacion")
    @Mapping(target = "clienteId", source = "cliente.terceroId")
    @Mapping(target = "clienteNombre", source = "cliente.nombre1")
    @Mapping(target = "bodegaId", source = "bodega.codigo")
    @Mapping(target = "bodegaNombre", source = "bodega.nombre")
    @Mapping(target = "cajeroId", source = "cajero.cajeroId")
    @Mapping(target = "cajeroNombre", source = "cajero.nombre")
    @Mapping(target = "vendedorId", source = "vendedor.vendedor_id")
    @Mapping(target = "vendedorNombre", source = "vendedor.nombre")
    @Mapping(target = "fechaEmision", source = "fechaEmision")
    @Mapping(target = "fechaVencimiento", source = "fechaVencimiento")
    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "observaciones", source = "observaciones")
    @Mapping(target = "subtotal", source = "gravada")
    @Mapping(target = "iva", source = "iva")
    @Mapping(target = "total", source = "totalCotizacion")
    @Mapping(target = "usuarioCreacion", source = "usuarioCreacion")
    @Mapping(target = "fechaCreacion", source = "fechaCreacion")
    @Mapping(target = "items", source = "items")
    CotizacionDTO toDto(Cotizacion cotizacion);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "numeroCotizacion", source = "numeroCotizacion")
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "bodega", ignore = true)
    @Mapping(target = "cajero", ignore = true)
    @Mapping(target = "vendedor", ignore = true)
    @Mapping(target = "fechaEmision", source = "fechaEmision")
    @Mapping(target = "fechaVencimiento", source = "fechaVencimiento")
    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "observaciones", source = "observaciones")
    @Mapping(target = "gravada", source = "subtotal")
    @Mapping(target = "iva", source = "iva")
    @Mapping(target = "totalCotizacion", source = "total")
    @Mapping(target = "usuarioCreacion", source = "usuarioCreacion")
    @Mapping(target = "fechaCreacion", source = "fechaCreacion")
    @Mapping(target = "items", source = "items")
    Cotizacion toEntity(CotizacionDTO cotizacionDTO);

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
    DetalleCotizacionDTO detalleToDto(DetalleCotizacion detalle);

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
    @Mapping(target = "cotizacion", ignore = true)
    @Mapping(target = "observacionProducto", ignore = true)
    DetalleCotizacion detalleToEntity(DetalleCotizacionDTO detalleDTO);

    List<DetalleCotizacionDTO> toDetalleDtoList(List<DetalleCotizacion> detalles);

    @AfterMapping
    default void setCotizacionInDetalles(@MappingTarget Cotizacion cotizacion) {
        if (cotizacion.getItems() != null) {
            cotizacion.getItems().forEach(detalle -> detalle.setCotizacion(cotizacion));
        }
    }
}

