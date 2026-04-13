package com.pazzioliweb.movimientosinventariomodule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.pazzioliweb.movimientosinventariomodule.enums.TipoMovimiento;
import com.pazzioliweb.movimientosinventariomodule.mapper.MovimientoInventarioMapper;
import com.pazzioliweb.movimientosinventariomodule.repository.KardexRepository;
import com.pazzioliweb.movimientosinventariomodule.repository.MovimientoInventarioDetalleRepository;
import com.pazzioliweb.movimientosinventariomodule.repository.MovimientoInventarioRepository;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.productosmodule.repositori.ProductoVarianteRepository;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

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

    @Autowired
    private ProductoVarianteRepository productoVarianteRepository;

    @Autowired
    private BodegasRepository bodegasRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public MovimientoInventarioResponseDto crearMovimiento(
            MovimientoInventarioCreateDto createDto,
            Comprobantes comprobante,
            Usuario usuario) {

        // Resolver comprobante desde ID si no viene pasado
        if (comprobante == null && createDto.getComprobanteId() != null) {
            comprobante = comprobantesRepository.findById(createDto.getComprobanteId())
                    .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado: " + createDto.getComprobanteId()));
        }

        // Resolver usuario desde ID si no viene pasado
        if (usuario == null && createDto.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(createDto.getUsuarioId().intValue())
                    .orElse(null);
        }

        // Asignar consecutivo automático si no viene
        if (createDto.getConsecutivo() == null && comprobante != null) {
            int nextConsecutivo = movimientoRepository
                    .findTopByComprobanteOrderByConsecutivoDesc(comprobante)
                    .map(m -> m.getConsecutivo() + 1)
                    .orElse(1);
            createDto.setConsecutivo(nextConsecutivo);
        }

        // Crear y guardar movimiento
        MovimientoInventario movimiento = mapper.toEntity(createDto, comprobante, usuario);
        movimiento.setFechaCreacion(LocalDateTime.now());
        if (movimiento.getEstado() == null) {
            movimiento.setEstado(EstadoMovimiento.ACTIVO);
        }
        movimientoRepository.save(movimiento);

        TipoMovimiento tipo = movimiento.getTipo();
        List<MovimientoInventarioDetalle> detalles = new ArrayList<>();

        for (MovimientoInventarioDetalleCreateDto detalleDto : createDto.getDetalles()) {

            // Resolver variante
            ProductoVariante variante = productoVarianteRepository
                    .findById(detalleDto.getProductoVarianteId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "ProductoVariante no encontrado: " + detalleDto.getProductoVarianteId()));

            // Resolver bodegas
            Bodegas bodegaOrigen = null;
            Bodegas bodegaDestino = null;

            if (detalleDto.getBodegaOrigenId() != null) {
                bodegaOrigen = bodegasRepository.findById(detalleDto.getBodegaOrigenId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Bodega origen no encontrada: " + detalleDto.getBodegaOrigenId()));
            }
            if (detalleDto.getBodegaDestinoId() != null) {
                bodegaDestino = bodegasRepository.findById(detalleDto.getBodegaDestinoId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Bodega destino no encontrada: " + detalleDto.getBodegaDestinoId()));
            }

            double costoUnitario = detalleDto.getCostoUnitario() != null ? detalleDto.getCostoUnitario() : 0.0;
            double cantidad = detalleDto.getCantidad();
            double totalDetalle = detalleDto.getTotalDetalle() != null
                    ? detalleDto.getTotalDetalle()
                    : costoUnitario * cantidad;

            MovimientoInventarioDetalle detalle = mapper.toDetalleEntity(
                    detalleDto, movimiento, variante, bodegaOrigen, bodegaDestino);
            detalle.setCostoUnitario(costoUnitario);
            detalle.setCostoPromedio(costoUnitario);
            detalle.setTotalDetalle(totalDetalle);
            detalleRepository.save(detalle);

            // Kardex: SALIDA o TRASLADO → registrar salida en bodega origen
            if (bodegaOrigen != null && (tipo == TipoMovimiento.SALIDA || tipo == TipoMovimiento.TRASLADO)) {
                crearKardexEntry(movimiento, detalle, variante, bodegaOrigen,
                        0.0, cantidad, costoUnitario, tipo);
            }

            // Kardex: ENTRADA → registrar entrada en bodega destino
            if (tipo == TipoMovimiento.ENTRADA && bodegaDestino != null) {
                crearKardexEntry(movimiento, detalle, variante, bodegaDestino,
                        cantidad, 0.0, costoUnitario, tipo);
            }
            // Kardex: TRASLADO → registrar entrada en bodega destino
            else if (tipo == TipoMovimiento.TRASLADO && bodegaDestino != null) {
                crearKardexEntry(movimiento, detalle, variante, bodegaDestino,
                        cantidad, 0.0, costoUnitario, tipo);
            }

            detalles.add(detalle);
        }

        // Actualizar total del movimiento
        double total = detalles.stream().mapToDouble(MovimientoInventarioDetalle::getTotalDetalle).sum();
        movimiento.setTotal(total);
        movimientoRepository.save(movimiento);

        return mapper.toResponse(movimiento, detalles);
    }

    private void crearKardexEntry(MovimientoInventario movimiento,
                                   MovimientoInventarioDetalle detalle,
                                   ProductoVariante variante,
                                   Bodegas bodega,
                                   double entrada,
                                   double salida,
                                   double costoUnitario,
                                   TipoMovimiento tipo) {

        double saldoAnterior = kardexRepository
                .findTopByProductoVarianteAndBodegaOrderByFechaCreacionDesc(variante, bodega)
                .map(Kardex::getSaldo)
                .orElse(0.0);

        double nuevoSaldo = saldoAnterior + entrada - salida;

        Kardex kardex = new Kardex();
        kardex.setMovimiento(movimiento);
        kardex.setDetalle(detalle);
        kardex.setProductoVariante(variante);
        kardex.setBodega(bodega);
        kardex.setFechaEmision(movimiento.getFechaEmision());
        kardex.setFechaCreacion(LocalDateTime.now());
        kardex.setEntrada(entrada);
        kardex.setSalida(salida);
        kardex.setSaldo(nuevoSaldo);
        kardex.setCostoUnitario(costoUnitario);
        kardex.setCostoPromedio(costoUnitario);
        kardex.setTotalCosto(costoUnitario * (entrada + salida));
        kardex.setTipo(tipo);
        kardex.setEstado(movimiento.getEstado());
        kardex.setObservaciones(movimiento.getObservaciones());
        kardexRepository.save(kardex);
    }

    @Override
    public MovimientoInventarioResponseDto actualizarMovimiento(Long movimientoId, MovimientoInventarioUpdateDto updateDto) {
        MovimientoInventario movimiento = movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        if (!movimiento.getEstado().equals(EstadoMovimiento.BORRADOR)) {
            throw new RuntimeException("Solo se pueden actualizar movimientos en estado BORRADOR");
        }

        if (updateDto.getComprobanteId() != null) {
            Comprobantes comprobante = comprobantesRepository.findById(updateDto.getComprobanteId())
                    .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado: " + updateDto.getComprobanteId()));
            movimiento.setComprobante(comprobante);
        }
        if (updateDto.getConsecutivo() != null) {
            movimiento.setConsecutivo(updateDto.getConsecutivo());
        }
        if (updateDto.getFechaEmision() != null) {
            movimiento.setFechaEmision(updateDto.getFechaEmision());
        }
        movimiento.setObservaciones(updateDto.getObservaciones());

        movimientoRepository.save(movimiento);

        List<MovimientoInventarioDetalle> detalles =
                detalleRepository.findByMovimiento_MovimientoId(movimiento.getMovimientoId());

        return mapper.toResponse(movimiento, detalles);
    }

    @Override
    @Transactional
    public void anularMovimiento(Long movimientoId) {
        MovimientoInventario movimiento = movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        movimiento.setEstado(EstadoMovimiento.ANULADO);
        movimientoRepository.save(movimiento);

        reversarKardex(movimientoId);
    }

    @Override
    public Page<MovimientoInventarioResponseDto> listarMovimientos(
            Pageable pageable,
            String tipo,
            LocalDate fechaEmisionDesde,
            LocalDate fechaEmisionHasta) {

        Page<MovimientoInventario> movimientos = movimientoRepository.findByFiltros(
                tipo, fechaEmisionDesde, fechaEmisionHasta, pageable);

        return movimientos.map(mov -> {
            List<MovimientoInventarioDetalle> detalles =
                    detalleRepository.findByMovimiento_MovimientoId(mov.getMovimientoId());
            return mapper.toResponse(mov, detalles);
        });
    }

    @Override
    public MovimientoInventarioResponseDto obtenerMovimientoConDetalles(Long movimientoId) {
        MovimientoInventario movimiento = movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        List<MovimientoInventarioDetalle> detalles =
                detalleRepository.findByMovimiento_MovimientoId(movimiento.getMovimientoId());
        return mapper.toResponse(movimiento, detalles);
    }

    @Override
    @Transactional
    public void reversarKardex(Long movimientoId) {
        List<Kardex> kardexList = kardexRepository.findByMovimiento_MovimientoId(movimientoId);
        for (Kardex kardex : kardexList) {
            kardex.setEntrada(0.0);
            kardex.setSalida(0.0);
            kardex.setSaldo(0.0);
            kardex.setEstado(EstadoMovimiento.ANULADO);
            kardexRepository.save(kardex);
        }
    }
}
