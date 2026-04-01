package com.pazzioliweb.movimientosinventariomodule.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pazzioliweb.comprobantesmodule.entity.Comprobantes;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobantesRepository;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioCreateDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioDetalleCreateDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioResponseDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioUpdateDto;
import com.pazzioliweb.movimientosinventariomodule.entity.Kardex;
import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventario;
import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventarioDetalle;
import com.pazzioliweb.movimientosinventariomodule.enums.EstadoMovimiento;
import com.pazzioliweb.movimientosinventariomodule.mapper.MovimientoInventarioMapper;
import com.pazzioliweb.movimientosinventariomodule.repository.KardexRepository;
import com.pazzioliweb.movimientosinventariomodule.repository.MovimientoInventarioDetalleRepository;
import com.pazzioliweb.movimientosinventariomodule.repository.MovimientoInventarioRepository;
import com.pazzioliweb.usuariosbacken.entity.Usuario;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService{
	@Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @Autowired
    private MovimientoInventarioDetalleRepository detalleRepository;

    @Autowired
    private KardexRepository kardexRepository;

    @Autowired
    private MovimientoInventarioMapper mapper;
    
    @Autowired
    private ComprobantesRepository comprobantesRepository;

    @Override
    public MovimientoInventarioResponseDto crearMovimiento(
            MovimientoInventarioCreateDto createDto, 
            Comprobantes comprobante, 
            Usuario usuario) {

        // Crear movimiento
        MovimientoInventario movimiento = mapper.toEntity(createDto, comprobante, usuario);

        // Guardar movimiento
        movimientoRepository.save(movimiento);

        // Crear detalles y kardex
        List<MovimientoInventarioDetalle> detalles = new ArrayList<>();
        for (MovimientoInventarioDetalleCreateDto detalleDto : createDto.getDetalles()) {
            MovimientoInventarioDetalle detalle = mapper.toDetalleEntity(detalleDto, movimiento, null, null, null);  // Agregar lógica de bodegas y variantes
            detalleRepository.save(detalle);

            // Crear Kardex
            Kardex kardex = new Kardex();
            kardex.setMovimiento(movimiento);
            kardex.setDetalle(detalle);
            kardex.setProductoVariante(detalle.getProductoVariante());
            kardex.setBodega(null);  // Agregar bodega lógica
            kardex.setEntrada(0.0);
            kardex.setSalida(detalle.getCantidad());
            kardex.setSaldo(0.0);
            kardex.setCostoUnitario(detalle.getCostoUnitario());
            kardex.setCostoPromedio(detalle.getCostoPromedio());
            kardex.setTotalCosto(detalle.getTotalDetalle());
            kardex.setTipo(movimiento.getTipo());
            kardex.setEstado(movimiento.getEstado());
            kardex.setObservaciones(movimiento.getObservaciones());
            kardexRepository.save(kardex);

            detalles.add(detalle);
        }

        return mapper.toResponse(movimiento, detalles);
    }

    @Override
    public MovimientoInventarioResponseDto actualizarMovimiento(Long movimientoId, MovimientoInventarioUpdateDto updateDto) {
        MovimientoInventario movimiento = movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        if (!movimiento.getEstado().equals(EstadoMovimiento.BORRADOR)) {
            throw new RuntimeException("Solo se pueden actualizar movimientos en estado BORRADOR");
        }

        // Actualizar campos
        if (updateDto.getComprobanteId() != null) {
            Comprobantes comprobante = comprobantesRepository.findById(updateDto.getComprobanteId())
                .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado: " + updateDto.getComprobanteId()));
            movimiento.setComprobante(comprobante);
        }
        if (updateDto.getConsecutivo() != null) {
            movimiento.setConsecutivo(updateDto.getConsecutivo());
        }
        movimiento.setFechaEmision(updateDto.getFechaEmision());
        movimiento.setObservaciones(updateDto.getObservaciones());

        // Guardar actualización
        movimientoRepository.save(movimiento);
        
        List<MovimientoInventarioDetalle> detalles =
                detalleRepository.findByMovimiento(movimiento.getMovimientoId());

        return mapper.toResponse(movimiento, detalles);
    }

    @Override
    public void anularMovimiento(Long movimientoId) {
        MovimientoInventario movimiento = movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        // Anular movimiento
        movimiento.setEstado(EstadoMovimiento.ANULADO);
        movimientoRepository.save(movimiento);

        // Reversar Kardex
        reversarKardex(movimientoId);
    }

    @Override
    public Page<MovimientoInventarioResponseDto> listarMovimientos(
            Pageable pageable,
            String tipo,
            LocalDate fechaEmisionDesde,
            LocalDate fechaEmisionHasta) {

        Page<MovimientoInventario> movimientos = movimientoRepository.findByFiltros(
                tipo,
                fechaEmisionDesde,
                fechaEmisionHasta,
                pageable
        );

        return movimientos.map(mov -> {
            List<MovimientoInventarioDetalle> detalles =
                    detalleRepository.findByMovimiento(mov.getMovimientoId());

            return mapper.toResponse(mov, detalles);
        });
    }

    @Override
    public MovimientoInventarioResponseDto obtenerMovimientoConDetalles(Long movimientoId) {
        MovimientoInventario movimiento = movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        List<MovimientoInventarioDetalle> detalles = detalleRepository.findByMovimiento(movimiento.getMovimientoId());
        return mapper.toResponse(movimiento, detalles);
    }

    @Override
    public void reversarKardex(Long movimientoId) {
        List<Kardex> kardexList = kardexRepository.findByMovimientoId(movimientoId);

        for (Kardex kardex : kardexList) {
            // Lógica para reversar el kardex (ajustar entrada/salida, costo)
            kardex.setEntrada(0.0);
            kardex.setSalida(0.0);
            kardex.setSaldo(0.0);  // Ajustar según las reglas de reverso
            kardex.setEstado(EstadoMovimiento.ANULADO);
            kardexRepository.save(kardex);
        }
    }
}
