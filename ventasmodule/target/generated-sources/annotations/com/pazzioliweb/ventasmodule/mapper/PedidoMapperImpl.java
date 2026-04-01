package com.pazzioliweb.ventasmodule.mapper;

import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.ventasmodule.dtos.DetallePedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.PedidoDTO;
import com.pazzioliweb.ventasmodule.entity.DetallePedido;
import com.pazzioliweb.ventasmodule.entity.Pedido;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-27T16:52:23-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
@Component
public class PedidoMapperImpl implements PedidoMapper {

    @Override
    public PedidoDTO toDto(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }

        PedidoDTO pedidoDTO = new PedidoDTO();

        pedidoDTO.setId( pedido.getId() );
        pedidoDTO.setNumeroPedido( pedido.getNumeroPedido() );
        Integer terceroId = pedidoClienteTerceroId( pedido );
        if ( terceroId != null ) {
            pedidoDTO.setClienteId( terceroId.longValue() );
        }
        pedidoDTO.setClienteNombre( pedidoClienteNombre1( pedido ) );
        pedidoDTO.setBodegaId( pedidoBodegaCodigo( pedido ) );
        pedidoDTO.setBodegaNombre( pedidoBodegaNombre( pedido ) );
        Integer cajeroId = pedidoCajeroCajeroId( pedido );
        if ( cajeroId != null ) {
            pedidoDTO.setCajeroId( cajeroId.longValue() );
        }
        pedidoDTO.setCajeroNombre( pedidoCajeroNombre( pedido ) );
        pedidoDTO.setCotizacionId( pedido.getCotizacionId() );
        pedidoDTO.setFechaEmision( pedido.getFechaEmision() );
        pedidoDTO.setFechaEntregaEsperada( pedido.getFechaEntregaEsperada() );
        pedidoDTO.setEstado( pedido.getEstado() );
        pedidoDTO.setObservaciones( pedido.getObservaciones() );
        pedidoDTO.setSubtotal( pedido.getGravada() );
        pedidoDTO.setIva( pedido.getIva() );
        pedidoDTO.setTotal( pedido.getTotalPedido() );
        pedidoDTO.setUsuarioCreacion( pedido.getUsuarioCreacion() );
        pedidoDTO.setFechaCreacion( pedido.getFechaCreacion() );
        pedidoDTO.setItems( toDetalleDtoList( pedido.getItems() ) );

        return pedidoDTO;
    }

    @Override
    public Pedido toEntity(PedidoDTO pedidoDTO) {
        if ( pedidoDTO == null ) {
            return null;
        }

        Pedido pedido = new Pedido();

        pedido.setId( pedidoDTO.getId() );
        pedido.setNumeroPedido( pedidoDTO.getNumeroPedido() );
        pedido.setCotizacionId( pedidoDTO.getCotizacionId() );
        pedido.setFechaEmision( pedidoDTO.getFechaEmision() );
        pedido.setFechaEntregaEsperada( pedidoDTO.getFechaEntregaEsperada() );
        pedido.setEstado( pedidoDTO.getEstado() );
        pedido.setObservaciones( pedidoDTO.getObservaciones() );
        pedido.setGravada( pedidoDTO.getSubtotal() );
        pedido.setIva( pedidoDTO.getIva() );
        pedido.setTotalPedido( pedidoDTO.getTotal() );
        pedido.setUsuarioCreacion( pedidoDTO.getUsuarioCreacion() );
        pedido.setFechaCreacion( pedidoDTO.getFechaCreacion() );
        pedido.setItems( detallePedidoDTOListToDetallePedidoList( pedidoDTO.getItems() ) );

        setPedidoInDetalles( pedido );

        return pedido;
    }

    @Override
    public DetallePedidoDTO detalleToDto(DetallePedido detalle) {
        if ( detalle == null ) {
            return null;
        }

        DetallePedidoDTO detallePedidoDTO = new DetallePedidoDTO();

        detallePedidoDTO.setId( detalle.getId() );
        detallePedidoDTO.setCodigoProducto( detalle.getCodigoProducto() );
        detallePedidoDTO.setCodigoBarras( detalle.getCodigoBarras() );
        detallePedidoDTO.setDescripcionProducto( detalle.getDescripcionProducto() );
        detallePedidoDTO.setReferenciaVariantes( detalle.getReferenciaVariantes() );
        detallePedidoDTO.setCantidad( detalle.getCantidad() );
        detallePedidoDTO.setPrecioUnitario( detalle.getPrecioUnitario() );
        detallePedidoDTO.setDescuento( detalle.getDescuento() );
        detallePedidoDTO.setIvaPorcentaje( detalle.getIva() );
        detallePedidoDTO.setTotal( detalle.getTotal() );

        return detallePedidoDTO;
    }

    @Override
    public DetallePedido detalleToEntity(DetallePedidoDTO detalleDTO) {
        if ( detalleDTO == null ) {
            return null;
        }

        DetallePedido detallePedido = new DetallePedido();

        detallePedido.setId( detalleDTO.getId() );
        detallePedido.setCodigoProducto( detalleDTO.getCodigoProducto() );
        detallePedido.setCodigoBarras( detalleDTO.getCodigoBarras() );
        detallePedido.setDescripcionProducto( detalleDTO.getDescripcionProducto() );
        detallePedido.setReferenciaVariantes( detalleDTO.getReferenciaVariantes() );
        detallePedido.setCantidad( detalleDTO.getCantidad() );
        detallePedido.setPrecioUnitario( detalleDTO.getPrecioUnitario() );
        detallePedido.setDescuento( detalleDTO.getDescuento() );
        detallePedido.setIva( detalleDTO.getIvaPorcentaje() );
        detallePedido.setTotal( detalleDTO.getTotal() );

        return detallePedido;
    }

    @Override
    public List<DetallePedidoDTO> toDetalleDtoList(List<DetallePedido> detalles) {
        if ( detalles == null ) {
            return null;
        }

        List<DetallePedidoDTO> list = new ArrayList<DetallePedidoDTO>( detalles.size() );
        for ( DetallePedido detallePedido : detalles ) {
            list.add( detalleToDto( detallePedido ) );
        }

        return list;
    }

    private Integer pedidoClienteTerceroId(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }
        Terceros cliente = pedido.getCliente();
        if ( cliente == null ) {
            return null;
        }
        Integer terceroId = cliente.getTerceroId();
        if ( terceroId == null ) {
            return null;
        }
        return terceroId;
    }

    private String pedidoClienteNombre1(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }
        Terceros cliente = pedido.getCliente();
        if ( cliente == null ) {
            return null;
        }
        String nombre1 = cliente.getNombre1();
        if ( nombre1 == null ) {
            return null;
        }
        return nombre1;
    }

    private Integer pedidoBodegaCodigo(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }
        Bodegas bodega = pedido.getBodega();
        if ( bodega == null ) {
            return null;
        }
        int codigo = bodega.getCodigo();
        return codigo;
    }

    private String pedidoBodegaNombre(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }
        Bodegas bodega = pedido.getBodega();
        if ( bodega == null ) {
            return null;
        }
        String nombre = bodega.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    private Integer pedidoCajeroCajeroId(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }
        Cajero cajero = pedido.getCajero();
        if ( cajero == null ) {
            return null;
        }
        Integer cajeroId = cajero.getCajeroId();
        if ( cajeroId == null ) {
            return null;
        }
        return cajeroId;
    }

    private String pedidoCajeroNombre(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }
        Cajero cajero = pedido.getCajero();
        if ( cajero == null ) {
            return null;
        }
        String nombre = cajero.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    protected List<DetallePedido> detallePedidoDTOListToDetallePedidoList(List<DetallePedidoDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<DetallePedido> list1 = new ArrayList<DetallePedido>( list.size() );
        for ( DetallePedidoDTO detallePedidoDTO : list ) {
            list1.add( detalleToEntity( detallePedidoDTO ) );
        }

        return list1;
    }
}
