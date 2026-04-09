package com.pazzioliweb.ventasmodule.mapper;

import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.vendedoresmodule.entity.Vendedores;
import com.pazzioliweb.ventasmodule.dtos.DetalleVentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import com.pazzioliweb.ventasmodule.entity.DetalleVenta;
import com.pazzioliweb.ventasmodule.entity.Venta;
import com.pazzioliweb.ventasmodule.entity.VentaMetodoPago;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-01T17:21:02-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
@Component
public class VentaMapperImpl implements VentaMapper {

    @Override
    public VentaDTO toDto(Venta venta) {
        if ( venta == null ) {
            return null;
        }

        VentaDTO ventaDTO = new VentaDTO();

        ventaDTO.setId( venta.getId() );
        ventaDTO.setNumeroVenta( venta.getNumeroVenta() );
        Integer terceroId = ventaClienteTerceroId( venta );
        if ( terceroId != null ) {
            ventaDTO.setClienteId( terceroId.longValue() );
        }
        ventaDTO.setClienteNombre( ventaClienteNombre1( venta ) );
        ventaDTO.setBodegaId( ventaBodegaCodigo( venta ) );
        ventaDTO.setBodegaNombre( ventaBodegaNombre( venta ) );
        Integer cajeroId = ventaCajeroCajeroId( venta );
        if ( cajeroId != null ) {
            ventaDTO.setCajeroId( cajeroId.longValue() );
        }
        ventaDTO.setCajeroNombre( ventaCajeroNombre( venta ) );
        ventaDTO.setVendedorId( ventaVendedorVendedor_id( venta ) );
        ventaDTO.setVendedorNombre( ventaVendedorNombre( venta ) );
        ventaDTO.setFechaEmision( venta.getFechaEmision() );
        ventaDTO.setFechaEntregaEsperada( venta.getFechaEntregaEsperada() );
        ventaDTO.setEstado( venta.getEstado() );
        ventaDTO.setObservaciones( venta.getObservaciones() );
        ventaDTO.setSubtotal( venta.getGravada() );
        ventaDTO.setIva( venta.getIva() );
        ventaDTO.setTotal( venta.getTotalVenta() );
        ventaDTO.setUsuarioCreacion( venta.getUsuarioCreacion() );
        ventaDTO.setFechaCreacion( venta.getFechaCreacion() );
        ventaDTO.setItems( toDetalleDtoList( venta.getItems() ) );
        ventaDTO.setMetodosPago( toVentaMetodoPagoDtoList( venta.getMetodosPago() ) );

        return ventaDTO;
    }

    @Override
    public Venta toEntity(VentaDTO ventaDTO) {
        if ( ventaDTO == null ) {
            return null;
        }

        Venta venta = new Venta();

        venta.setId( ventaDTO.getId() );
        venta.setNumeroVenta( ventaDTO.getNumeroVenta() );
        venta.setFechaEmision( ventaDTO.getFechaEmision() );
        venta.setFechaEntregaEsperada( ventaDTO.getFechaEntregaEsperada() );
        venta.setEstado( ventaDTO.getEstado() );
        venta.setObservaciones( ventaDTO.getObservaciones() );
        venta.setGravada( ventaDTO.getSubtotal() );
        venta.setIva( ventaDTO.getIva() );
        venta.setTotalVenta( ventaDTO.getTotal() );
        venta.setUsuarioCreacion( ventaDTO.getUsuarioCreacion() );
        venta.setFechaCreacion( ventaDTO.getFechaCreacion() );
        venta.setItems( detalleVentaDTOListToDetalleVentaList( ventaDTO.getItems() ) );

        setVentaInDetalles( venta );

        return venta;
    }

    @Override
    public DetalleVentaDTO detalleToDto(DetalleVenta detalle) {
        if ( detalle == null ) {
            return null;
        }

        DetalleVentaDTO detalleVentaDTO = new DetalleVentaDTO();

        detalleVentaDTO.setId( detalle.getId() );
        detalleVentaDTO.setCodigoProducto( detalle.getCodigoProducto() );
        detalleVentaDTO.setCodigoBarras( detalle.getCodigoBarras() );
        detalleVentaDTO.setDescripcionProducto( detalle.getDescripcionProducto() );
        detalleVentaDTO.setReferenciaVariantes( detalle.getReferenciaVariantes() );
        detalleVentaDTO.setCantidad( detalle.getCantidad() );
        detalleVentaDTO.setPrecioUnitario( detalle.getPrecioUnitario() );
        detalleVentaDTO.setDescuento( detalle.getDescuento() );
        detalleVentaDTO.setIvaPorcentaje( detalle.getIva() );
        detalleVentaDTO.setTotal( detalle.getTotal() );

        return detalleVentaDTO;
    }

    @Override
    public DetalleVenta detalleToEntity(DetalleVentaDTO detalleDTO) {
        if ( detalleDTO == null ) {
            return null;
        }

        DetalleVenta detalleVenta = new DetalleVenta();

        detalleVenta.setId( detalleDTO.getId() );
        detalleVenta.setCodigoProducto( detalleDTO.getCodigoProducto() );
        detalleVenta.setCodigoBarras( detalleDTO.getCodigoBarras() );
        detalleVenta.setDescripcionProducto( detalleDTO.getDescripcionProducto() );
        detalleVenta.setReferenciaVariantes( detalleDTO.getReferenciaVariantes() );
        detalleVenta.setCantidad( detalleDTO.getCantidad() );
        detalleVenta.setPrecioUnitario( detalleDTO.getPrecioUnitario() );
        detalleVenta.setDescuento( detalleDTO.getDescuento() );
        detalleVenta.setIva( detalleDTO.getIvaPorcentaje() );
        detalleVenta.setTotal( detalleDTO.getTotal() );

        return detalleVenta;
    }

    @Override
    public List<DetalleVentaDTO> toDetalleDtoList(List<DetalleVenta> detalles) {
        if ( detalles == null ) {
            return null;
        }

        List<DetalleVentaDTO> list = new ArrayList<DetalleVentaDTO>( detalles.size() );
        for ( DetalleVenta detalleVenta : detalles ) {
            list.add( detalleToDto( detalleVenta ) );
        }

        return list;
    }

    @Override
    public VentaMetodoPagoDTO ventaMetodoPagoToDto(VentaMetodoPago entity) {
        if ( entity == null ) {
            return null;
        }

        VentaMetodoPagoDTO ventaMetodoPagoDTO = new VentaMetodoPagoDTO();

        ventaMetodoPagoDTO.setId( entity.getId() );
        Integer metodo_pago_id = entityMetodoPagoMetodo_pago_id( entity );
        if ( metodo_pago_id != null ) {
            ventaMetodoPagoDTO.setMetodoPagoId( metodo_pago_id.longValue() );
        }
        ventaMetodoPagoDTO.setMetodoPagoNombre( entityMetodoPagoDescripcion( entity ) );
        ventaMetodoPagoDTO.setMonto( entity.getMonto() );
        ventaMetodoPagoDTO.setReferencia( entity.getReferencia() );

        return ventaMetodoPagoDTO;
    }

    @Override
    public List<VentaMetodoPagoDTO> toVentaMetodoPagoDtoList(List<VentaMetodoPago> entities) {
        if ( entities == null ) {
            return null;
        }

        List<VentaMetodoPagoDTO> list = new ArrayList<VentaMetodoPagoDTO>( entities.size() );
        for ( VentaMetodoPago ventaMetodoPago : entities ) {
            list.add( ventaMetodoPagoToDto( ventaMetodoPago ) );
        }

        return list;
    }

    private Integer ventaClienteTerceroId(Venta venta) {
        if ( venta == null ) {
            return null;
        }
        Terceros cliente = venta.getCliente();
        if ( cliente == null ) {
            return null;
        }
        Integer terceroId = cliente.getTerceroId();
        if ( terceroId == null ) {
            return null;
        }
        return terceroId;
    }

    private String ventaClienteNombre1(Venta venta) {
        if ( venta == null ) {
            return null;
        }
        Terceros cliente = venta.getCliente();
        if ( cliente == null ) {
            return null;
        }
        String nombre1 = cliente.getNombre1();
        if ( nombre1 == null ) {
            return null;
        }
        return nombre1;
    }

    private Integer ventaBodegaCodigo(Venta venta) {
        if ( venta == null ) {
            return null;
        }
        Bodegas bodega = venta.getBodega();
        if ( bodega == null ) {
            return null;
        }
        int codigo = bodega.getCodigo();
        return codigo;
    }

    private String ventaBodegaNombre(Venta venta) {
        if ( venta == null ) {
            return null;
        }
        Bodegas bodega = venta.getBodega();
        if ( bodega == null ) {
            return null;
        }
        String nombre = bodega.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    private Integer ventaCajeroCajeroId(Venta venta) {
        if ( venta == null ) {
            return null;
        }
        Cajero cajero = venta.getCajero();
        if ( cajero == null ) {
            return null;
        }
        Integer cajeroId = cajero.getCajeroId();
        if ( cajeroId == null ) {
            return null;
        }
        return cajeroId;
    }

    private String ventaCajeroNombre(Venta venta) {
        if ( venta == null ) {
            return null;
        }
        Cajero cajero = venta.getCajero();
        if ( cajero == null ) {
            return null;
        }
        String nombre = cajero.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    private Integer ventaVendedorVendedor_id(Venta venta) {
        if ( venta == null ) {
            return null;
        }
        Vendedores vendedor = venta.getVendedor();
        if ( vendedor == null ) {
            return null;
        }
        Integer vendedor_id = vendedor.getVendedor_id();
        if ( vendedor_id == null ) {
            return null;
        }
        return vendedor_id;
    }

    private String ventaVendedorNombre(Venta venta) {
        if ( venta == null ) {
            return null;
        }
        Vendedores vendedor = venta.getVendedor();
        if ( vendedor == null ) {
            return null;
        }
        String nombre = vendedor.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    protected List<DetalleVenta> detalleVentaDTOListToDetalleVentaList(List<DetalleVentaDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<DetalleVenta> list1 = new ArrayList<DetalleVenta>( list.size() );
        for ( DetalleVentaDTO detalleVentaDTO : list ) {
            list1.add( detalleToEntity( detalleVentaDTO ) );
        }

        return list1;
    }

    private Integer entityMetodoPagoMetodo_pago_id(VentaMetodoPago ventaMetodoPago) {
        if ( ventaMetodoPago == null ) {
            return null;
        }
        MetodosPago metodoPago = ventaMetodoPago.getMetodoPago();
        if ( metodoPago == null ) {
            return null;
        }
        Integer metodo_pago_id = metodoPago.getMetodo_pago_id();
        if ( metodo_pago_id == null ) {
            return null;
        }
        return metodo_pago_id;
    }

    private String entityMetodoPagoDescripcion(VentaMetodoPago ventaMetodoPago) {
        if ( ventaMetodoPago == null ) {
            return null;
        }
        MetodosPago metodoPago = ventaMetodoPago.getMetodoPago();
        if ( metodoPago == null ) {
            return null;
        }
        String descripcion = metodoPago.getDescripcion();
        if ( descripcion == null ) {
            return null;
        }
        return descripcion;
    }
}
