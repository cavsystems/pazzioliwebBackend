package com.pazzioliweb.comprasmodule.mapper;

import com.pazzioliweb.comprasmodule.dtos.DetalleOrdenCompraDTO;
import com.pazzioliweb.comprasmodule.dtos.OrdenCompraDTO;
import com.pazzioliweb.comprasmodule.entity.DetalleOrdenCompra;
import com.pazzioliweb.comprasmodule.entity.OrdenCompra;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-13T07:38:53-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class OrdenCompraMapperImpl implements OrdenCompraMapper {

    @Override
    public OrdenCompraDTO toDto(OrdenCompra ordenCompra) {
        if ( ordenCompra == null ) {
            return null;
        }

        OrdenCompraDTO ordenCompraDTO = new OrdenCompraDTO();

        ordenCompraDTO.setId( ordenCompra.getId() );
        ordenCompraDTO.setNumeroOrden( ordenCompra.getNumeroOrden() );
        Integer terceroId = ordenCompraProveedorTerceroId( ordenCompra );
        if ( terceroId != null ) {
            ordenCompraDTO.setProveedorId( terceroId.longValue() );
        }
        ordenCompraDTO.setProveedorNombre( ordenCompraProveedorRazonSocial( ordenCompra ) );
        Integer codigo = ordenCompraBodegaCodigo( ordenCompra );
        if ( codigo != null ) {
            ordenCompraDTO.setBodegaId( codigo.longValue() );
        }
        ordenCompraDTO.setBodegaNombre( ordenCompraBodegaNombre( ordenCompra ) );
        ordenCompraDTO.setFechaEmision( ordenCompra.getFechaEmision() );
        ordenCompraDTO.setFechaEntregaEsperada( ordenCompra.getFechaEntregaEsperada() );
        ordenCompraDTO.setEstado( ordenCompra.getEstado() );
        ordenCompraDTO.setObservaciones( ordenCompra.getObservaciones() );
        ordenCompraDTO.setSubtotal( ordenCompra.getGravada() );
        ordenCompraDTO.setIva( ordenCompra.getIva() );
        ordenCompraDTO.setTotal( ordenCompra.getTotalOrdenCompra() );
        ordenCompraDTO.setUsuarioCreacion( ordenCompra.getUsuarioCreacion() );
        ordenCompraDTO.setFechaCreacion( ordenCompra.getFechaCreacion() );
        ordenCompraDTO.setItems( toDetalleDtoList( ordenCompra.getItems() ) );

        return ordenCompraDTO;
    }

    @Override
    public OrdenCompra toEntity(OrdenCompraDTO ordenCompraDTO) {
        if ( ordenCompraDTO == null ) {
            return null;
        }

        OrdenCompra ordenCompra = new OrdenCompra();

        ordenCompra.setId( ordenCompraDTO.getId() );
        ordenCompra.setNumeroOrden( ordenCompraDTO.getNumeroOrden() );
        ordenCompra.setFechaEmision( ordenCompraDTO.getFechaEmision() );
        ordenCompra.setFechaEntregaEsperada( ordenCompraDTO.getFechaEntregaEsperada() );
        ordenCompra.setEstado( ordenCompraDTO.getEstado() );
        ordenCompra.setObservaciones( ordenCompraDTO.getObservaciones() );
        ordenCompra.setGravada( ordenCompraDTO.getSubtotal() );
        ordenCompra.setIva( ordenCompraDTO.getIva() );
        ordenCompra.setTotalOrdenCompra( ordenCompraDTO.getTotal() );
        ordenCompra.setUsuarioCreacion( ordenCompraDTO.getUsuarioCreacion() );
        ordenCompra.setFechaCreacion( ordenCompraDTO.getFechaCreacion() );
        ordenCompra.setItems( detalleOrdenCompraDTOListToDetalleOrdenCompraList( ordenCompraDTO.getItems() ) );

        setOrdenCompraInDetalles( ordenCompra );

        return ordenCompra;
    }

    @Override
    public DetalleOrdenCompraDTO detalleToDto(DetalleOrdenCompra detalle) {
        if ( detalle == null ) {
            return null;
        }

        DetalleOrdenCompraDTO detalleOrdenCompraDTO = new DetalleOrdenCompraDTO();

        detalleOrdenCompraDTO.setId( detalle.getId() );
        detalleOrdenCompraDTO.setCodigoProducto( detalle.getCodigoProducto() );
        detalleOrdenCompraDTO.setCodigoBarras( detalle.getCodigoBarras() );
        detalleOrdenCompraDTO.setDescripcionProducto( detalle.getDescripcionProducto() );
        detalleOrdenCompraDTO.setReferenciaVariantes( detalle.getReferenciaVariantes() );
        detalleOrdenCompraDTO.setCantidad( detalle.getCantidad() );
        detalleOrdenCompraDTO.setPrecioUnitario( detalle.getPrecioUnitario() );
        detalleOrdenCompraDTO.setDescuento( detalle.getDescuento() );
        detalleOrdenCompraDTO.setIvaPorcentaje( detalle.getIva() );
        detalleOrdenCompraDTO.setTotal( detalle.getTotal() );
        detalleOrdenCompraDTO.setRecibido( detalle.isRecibido() );
        detalleOrdenCompraDTO.setCantidadRecibida( detalle.getCantidadRecibida() );
        detalleOrdenCompraDTO.setManifiesto( detalle.getManifiesto() );

        return detalleOrdenCompraDTO;
    }

    @Override
    public DetalleOrdenCompra detalleToEntity(DetalleOrdenCompraDTO detalleDTO) {
        if ( detalleDTO == null ) {
            return null;
        }

        DetalleOrdenCompra detalleOrdenCompra = new DetalleOrdenCompra();

        detalleOrdenCompra.setId( detalleDTO.getId() );
        detalleOrdenCompra.setCodigoProducto( detalleDTO.getCodigoProducto() );
        detalleOrdenCompra.setCodigoBarras( detalleDTO.getCodigoBarras() );
        detalleOrdenCompra.setDescripcionProducto( detalleDTO.getDescripcionProducto() );
        detalleOrdenCompra.setReferenciaVariantes( detalleDTO.getReferenciaVariantes() );
        detalleOrdenCompra.setCantidad( detalleDTO.getCantidad() );
        detalleOrdenCompra.setPrecioUnitario( detalleDTO.getPrecioUnitario() );
        detalleOrdenCompra.setDescuento( detalleDTO.getDescuento() );
        detalleOrdenCompra.setIva( detalleDTO.getIvaPorcentaje() );
        detalleOrdenCompra.setTotal( detalleDTO.getTotal() );
        detalleOrdenCompra.setRecibido( detalleDTO.isRecibido() );
        detalleOrdenCompra.setCantidadRecibida( detalleDTO.getCantidadRecibida() );
        detalleOrdenCompra.setManifiesto( detalleDTO.getManifiesto() );

        return detalleOrdenCompra;
    }

    @Override
    public List<DetalleOrdenCompraDTO> toDetalleDtoList(List<DetalleOrdenCompra> detalles) {
        if ( detalles == null ) {
            return null;
        }

        List<DetalleOrdenCompraDTO> list = new ArrayList<DetalleOrdenCompraDTO>( detalles.size() );
        for ( DetalleOrdenCompra detalleOrdenCompra : detalles ) {
            list.add( detalleToDto( detalleOrdenCompra ) );
        }

        return list;
    }

    private Integer ordenCompraProveedorTerceroId(OrdenCompra ordenCompra) {
        if ( ordenCompra == null ) {
            return null;
        }
        Terceros proveedor = ordenCompra.getProveedor();
        if ( proveedor == null ) {
            return null;
        }
        Integer terceroId = proveedor.getTerceroId();
        if ( terceroId == null ) {
            return null;
        }
        return terceroId;
    }

    private String ordenCompraProveedorRazonSocial(OrdenCompra ordenCompra) {
        if ( ordenCompra == null ) {
            return null;
        }
        Terceros proveedor = ordenCompra.getProveedor();
        if ( proveedor == null ) {
            return null;
        }
        String razonSocial = proveedor.getRazonSocial();
        if ( razonSocial == null ) {
            return null;
        }
        return razonSocial;
    }

    private Integer ordenCompraBodegaCodigo(OrdenCompra ordenCompra) {
        if ( ordenCompra == null ) {
            return null;
        }
        Bodegas bodega = ordenCompra.getBodega();
        if ( bodega == null ) {
            return null;
        }
        int codigo = bodega.getCodigo();
        return codigo;
    }

    private String ordenCompraBodegaNombre(OrdenCompra ordenCompra) {
        if ( ordenCompra == null ) {
            return null;
        }
        Bodegas bodega = ordenCompra.getBodega();
        if ( bodega == null ) {
            return null;
        }
        String nombre = bodega.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    protected List<DetalleOrdenCompra> detalleOrdenCompraDTOListToDetalleOrdenCompraList(List<DetalleOrdenCompraDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<DetalleOrdenCompra> list1 = new ArrayList<DetalleOrdenCompra>( list.size() );
        for ( DetalleOrdenCompraDTO detalleOrdenCompraDTO : list ) {
            list1.add( detalleToEntity( detalleOrdenCompraDTO ) );
        }

        return list1;
    }
}
