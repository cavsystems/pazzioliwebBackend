package com.pazzioliweb.ventasmodule.mapper;

import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.ventasmodule.dtos.CotizacionDTO;
import com.pazzioliweb.ventasmodule.dtos.DetalleCotizacionDTO;
import com.pazzioliweb.ventasmodule.entity.Cotizacion;
import com.pazzioliweb.ventasmodule.entity.DetalleCotizacion;
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
public class CotizacionMapperImpl implements CotizacionMapper {

    @Override
    public CotizacionDTO toDto(Cotizacion cotizacion) {
        if ( cotizacion == null ) {
            return null;
        }

        CotizacionDTO cotizacionDTO = new CotizacionDTO();

        cotizacionDTO.setId( cotizacion.getId() );
        cotizacionDTO.setNumeroCotizacion( cotizacion.getNumeroCotizacion() );
        Integer terceroId = cotizacionClienteTerceroId( cotizacion );
        if ( terceroId != null ) {
            cotizacionDTO.setClienteId( terceroId.longValue() );
        }
        cotizacionDTO.setClienteNombre( cotizacionClienteNombre1( cotizacion ) );
        cotizacionDTO.setBodegaId( cotizacionBodegaCodigo( cotizacion ) );
        cotizacionDTO.setBodegaNombre( cotizacionBodegaNombre( cotizacion ) );
        Integer cajeroId = cotizacionCajeroCajeroId( cotizacion );
        if ( cajeroId != null ) {
            cotizacionDTO.setCajeroId( cajeroId.longValue() );
        }
        cotizacionDTO.setCajeroNombre( cotizacionCajeroNombre( cotizacion ) );
        cotizacionDTO.setFechaEmision( cotizacion.getFechaEmision() );
        cotizacionDTO.setFechaVencimiento( cotizacion.getFechaVencimiento() );
        cotizacionDTO.setEstado( cotizacion.getEstado() );
        cotizacionDTO.setObservaciones( cotizacion.getObservaciones() );
        cotizacionDTO.setSubtotal( cotizacion.getGravada() );
        cotizacionDTO.setIva( cotizacion.getIva() );
        cotizacionDTO.setTotal( cotizacion.getTotalCotizacion() );
        cotizacionDTO.setUsuarioCreacion( cotizacion.getUsuarioCreacion() );
        cotizacionDTO.setFechaCreacion( cotizacion.getFechaCreacion() );
        cotizacionDTO.setItems( toDetalleDtoList( cotizacion.getItems() ) );

        return cotizacionDTO;
    }

    @Override
    public Cotizacion toEntity(CotizacionDTO cotizacionDTO) {
        if ( cotizacionDTO == null ) {
            return null;
        }

        Cotizacion cotizacion = new Cotizacion();

        cotizacion.setId( cotizacionDTO.getId() );
        cotizacion.setNumeroCotizacion( cotizacionDTO.getNumeroCotizacion() );
        cotizacion.setFechaEmision( cotizacionDTO.getFechaEmision() );
        cotizacion.setFechaVencimiento( cotizacionDTO.getFechaVencimiento() );
        cotizacion.setEstado( cotizacionDTO.getEstado() );
        cotizacion.setObservaciones( cotizacionDTO.getObservaciones() );
        cotizacion.setGravada( cotizacionDTO.getSubtotal() );
        cotizacion.setIva( cotizacionDTO.getIva() );
        cotizacion.setTotalCotizacion( cotizacionDTO.getTotal() );
        cotizacion.setUsuarioCreacion( cotizacionDTO.getUsuarioCreacion() );
        cotizacion.setFechaCreacion( cotizacionDTO.getFechaCreacion() );
        cotizacion.setItems( detalleCotizacionDTOListToDetalleCotizacionList( cotizacionDTO.getItems() ) );

        setCotizacionInDetalles( cotizacion );

        return cotizacion;
    }

    @Override
    public DetalleCotizacionDTO detalleToDto(DetalleCotizacion detalle) {
        if ( detalle == null ) {
            return null;
        }

        DetalleCotizacionDTO detalleCotizacionDTO = new DetalleCotizacionDTO();

        detalleCotizacionDTO.setId( detalle.getId() );
        detalleCotizacionDTO.setCodigoProducto( detalle.getCodigoProducto() );
        detalleCotizacionDTO.setCodigoBarras( detalle.getCodigoBarras() );
        detalleCotizacionDTO.setDescripcionProducto( detalle.getDescripcionProducto() );
        detalleCotizacionDTO.setReferenciaVariantes( detalle.getReferenciaVariantes() );
        detalleCotizacionDTO.setCantidad( detalle.getCantidad() );
        detalleCotizacionDTO.setPrecioUnitario( detalle.getPrecioUnitario() );
        detalleCotizacionDTO.setDescuento( detalle.getDescuento() );
        detalleCotizacionDTO.setIvaPorcentaje( detalle.getIva() );
        detalleCotizacionDTO.setTotal( detalle.getTotal() );

        return detalleCotizacionDTO;
    }

    @Override
    public DetalleCotizacion detalleToEntity(DetalleCotizacionDTO detalleDTO) {
        if ( detalleDTO == null ) {
            return null;
        }

        DetalleCotizacion detalleCotizacion = new DetalleCotizacion();

        detalleCotizacion.setId( detalleDTO.getId() );
        detalleCotizacion.setCodigoProducto( detalleDTO.getCodigoProducto() );
        detalleCotizacion.setCodigoBarras( detalleDTO.getCodigoBarras() );
        detalleCotizacion.setDescripcionProducto( detalleDTO.getDescripcionProducto() );
        detalleCotizacion.setReferenciaVariantes( detalleDTO.getReferenciaVariantes() );
        detalleCotizacion.setCantidad( detalleDTO.getCantidad() );
        detalleCotizacion.setPrecioUnitario( detalleDTO.getPrecioUnitario() );
        detalleCotizacion.setDescuento( detalleDTO.getDescuento() );
        detalleCotizacion.setIva( detalleDTO.getIvaPorcentaje() );
        detalleCotizacion.setTotal( detalleDTO.getTotal() );

        return detalleCotizacion;
    }

    @Override
    public List<DetalleCotizacionDTO> toDetalleDtoList(List<DetalleCotizacion> detalles) {
        if ( detalles == null ) {
            return null;
        }

        List<DetalleCotizacionDTO> list = new ArrayList<DetalleCotizacionDTO>( detalles.size() );
        for ( DetalleCotizacion detalleCotizacion : detalles ) {
            list.add( detalleToDto( detalleCotizacion ) );
        }

        return list;
    }

    private Integer cotizacionClienteTerceroId(Cotizacion cotizacion) {
        if ( cotizacion == null ) {
            return null;
        }
        Terceros cliente = cotizacion.getCliente();
        if ( cliente == null ) {
            return null;
        }
        Integer terceroId = cliente.getTerceroId();
        if ( terceroId == null ) {
            return null;
        }
        return terceroId;
    }

    private String cotizacionClienteNombre1(Cotizacion cotizacion) {
        if ( cotizacion == null ) {
            return null;
        }
        Terceros cliente = cotizacion.getCliente();
        if ( cliente == null ) {
            return null;
        }
        String nombre1 = cliente.getNombre1();
        if ( nombre1 == null ) {
            return null;
        }
        return nombre1;
    }

    private Integer cotizacionBodegaCodigo(Cotizacion cotizacion) {
        if ( cotizacion == null ) {
            return null;
        }
        Bodegas bodega = cotizacion.getBodega();
        if ( bodega == null ) {
            return null;
        }
        int codigo = bodega.getCodigo();
        return codigo;
    }

    private String cotizacionBodegaNombre(Cotizacion cotizacion) {
        if ( cotizacion == null ) {
            return null;
        }
        Bodegas bodega = cotizacion.getBodega();
        if ( bodega == null ) {
            return null;
        }
        String nombre = bodega.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    private Integer cotizacionCajeroCajeroId(Cotizacion cotizacion) {
        if ( cotizacion == null ) {
            return null;
        }
        Cajero cajero = cotizacion.getCajero();
        if ( cajero == null ) {
            return null;
        }
        Integer cajeroId = cajero.getCajeroId();
        if ( cajeroId == null ) {
            return null;
        }
        return cajeroId;
    }

    private String cotizacionCajeroNombre(Cotizacion cotizacion) {
        if ( cotizacion == null ) {
            return null;
        }
        Cajero cajero = cotizacion.getCajero();
        if ( cajero == null ) {
            return null;
        }
        String nombre = cajero.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    protected List<DetalleCotizacion> detalleCotizacionDTOListToDetalleCotizacionList(List<DetalleCotizacionDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<DetalleCotizacion> list1 = new ArrayList<DetalleCotizacion>( list.size() );
        for ( DetalleCotizacionDTO detalleCotizacionDTO : list ) {
            list1.add( detalleToEntity( detalleCotizacionDTO ) );
        }

        return list1;
    }
}
