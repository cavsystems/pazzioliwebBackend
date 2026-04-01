package com.pazzioliweb.movimientosinventariomodule.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pazzioliweb.comprobantesmodule.entity.Comprobantes;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioCreateDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioDetalleCreateDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioDetalleResponseDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioResponseDto;
import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventario;
import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventarioDetalle;
import com.pazzioliweb.movimientosinventariomodule.enums.EstadoMovimiento;
import com.pazzioliweb.movimientosinventariomodule.enums.TipoMovimiento;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.usuariosbacken.entity.Usuario;


@Component
public class MovimientoInventarioMapper {
	 public MovimientoInventario toEntity(
	            MovimientoInventarioCreateDto dto,
	            Comprobantes comprobante,
	            Usuario usuario) {

	        MovimientoInventario mov = new MovimientoInventario();

	        mov.setComprobante(comprobante);
	        mov.setConsecutivo(dto.getConsecutivo()); // puede venir null
	        mov.setTipo(TipoMovimiento.valueOf(dto.getTipo()));
	        mov.setUsuario(usuario);
	        mov.setFechaEmision(dto.getFechaEmision());
	        mov.setFechaCreacion(LocalDateTime.now());
	        mov.setEstado(dto.getEstado() == null
	                ? EstadoMovimiento.BORRADOR
	                : EstadoMovimiento.valueOf(dto.getEstado()));
	        mov.setTotal(dto.getTotal());
	        mov.setObservaciones(dto.getObservaciones());

	        return mov;
	    }


	    public MovimientoInventarioDetalle toDetalleEntity(
	            MovimientoInventarioDetalleCreateDto dto,
	            MovimientoInventario movimiento,
	            ProductoVariante variante,
	            Bodegas origen,
	            Bodegas destino) {

	        MovimientoInventarioDetalle det = new MovimientoInventarioDetalle();

	        det.setMovimiento(movimiento);
	        det.setProductoVariante(variante);
	        det.setBodegaOrigen(origen);
	        det.setBodegaDestino(destino);
	        det.setCantidad(dto.getCantidad());
	        det.setCostoUnitario(dto.getCostoUnitario());
	        det.setCostoPromedio(dto.getCostoPromedio());
	        det.setTotalDetalle(dto.getTotalDetalle());

	        return det;
	    }


	    public MovimientoInventarioResponseDto toResponse(
	            MovimientoInventario entity,
	            List<MovimientoInventarioDetalle> detalles) {

	        MovimientoInventarioResponseDto dto = new MovimientoInventarioResponseDto();

	        dto.setMovimientoId(entity.getMovimientoId());
	        dto.setComprobanteId(entity.getComprobante().getComprobante_id());
	        dto.setComprobanteNombre(entity.getComprobante().getNombre());
	        dto.setConsecutivo(entity.getConsecutivo());
	        dto.setTipo(entity.getTipo().name());
	        dto.setUsuarioId(entity.getUsuario() != null ? entity.getUsuario().getCodigo() : null);
	        dto.setUsuarioNombre(entity.getUsuario() != null ? entity.getUsuario().getNombre() : null);
	        dto.setFechaEmision(entity.getFechaEmision());
	        dto.setFechaCreacion(entity.getFechaCreacion());
	        dto.setEstado(entity.getEstado().name());
	        dto.setTotal(entity.getTotal());
	        dto.setObservaciones(entity.getObservaciones());

	        // Mapear detalles
	        dto.setDetalles(detalles.stream()
	                .map(this::toDetalleResponse)
	                .toList());

	        return dto;
	    }


	    public MovimientoInventarioDetalleResponseDto toDetalleResponse(MovimientoInventarioDetalle det) {

	        MovimientoInventarioDetalleResponseDto dto = new MovimientoInventarioDetalleResponseDto();

	        dto.setDetalleId(det.getDetalleId());
	        dto.setProductoVarianteId(det.getProductoVariante().getProductoVarianteId());
	        dto.setProductoSku(det.getProductoVariante().getSku());

	        dto.setBodegaOrigenId(det.getBodegaOrigen() != null ? det.getBodegaOrigen().getCodigo() : null);
	        dto.setBodegaOrigenNombre(det.getBodegaOrigen() != null ? det.getBodegaOrigen().getNombre() : null);

	        dto.setBodegaDestinoId(det.getBodegaDestino() != null ? det.getBodegaDestino().getCodigo() : null);
	        dto.setBodegaDestinoNombre(det.getBodegaDestino() != null ? det.getBodegaDestino().getNombre() : null);

	        dto.setCantidad(det.getCantidad());
	        dto.setCostoUnitario(det.getCostoUnitario());
	        dto.setCostoPromedio(det.getCostoPromedio());
	        dto.setTotalDetalle(det.getTotalDetalle());

	        return dto;
	    }
}
