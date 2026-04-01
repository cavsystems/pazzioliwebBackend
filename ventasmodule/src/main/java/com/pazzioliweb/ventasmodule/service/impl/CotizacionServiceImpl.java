package com.pazzioliweb.ventasmodule.service.impl;

import com.pazzioliweb.ventasmodule.dtos.CotizacionDTO;
import com.pazzioliweb.ventasmodule.dtos.DetallePedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.DetalleVentaDTO;
import com.pazzioliweb.ventasmodule.dtos.PedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import com.pazzioliweb.ventasmodule.entity.Cotizacion;
import com.pazzioliweb.ventasmodule.entity.DetalleCotizacion;
import com.pazzioliweb.ventasmodule.exception.CotizacionException;
import com.pazzioliweb.ventasmodule.mapper.CotizacionMapper;
import com.pazzioliweb.ventasmodule.repository.CotizacionRepository;
import com.pazzioliweb.ventasmodule.service.CotizacionService;
import com.pazzioliweb.ventasmodule.service.PedidoService;
import com.pazzioliweb.ventasmodule.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final CotizacionMapper cotizacionMapper;
    private final PedidoService pedidoService;
    private final VentaService ventaService;

    @Autowired
    public CotizacionServiceImpl(CotizacionRepository cotizacionRepository,
                                  CotizacionMapper cotizacionMapper,
                                  @Lazy PedidoService pedidoService,
                                  @Lazy VentaService ventaService) {
        this.cotizacionRepository = cotizacionRepository;
        this.cotizacionMapper = cotizacionMapper;
        this.pedidoService = pedidoService;
        this.ventaService = ventaService;
    }

    @Override
    @Transactional
    public void crearCotizacion(CotizacionDTO cotizacionDTO) {
        Cotizacion cotizacion = cotizacionMapper.toEntity(cotizacionDTO);
        cotizacion.setEstado("BORRADOR");
        cotizacion.setFechaCreacion(LocalDate.now());

        // Calcular totales
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal ivaTotal = BigDecimal.ZERO;
        BigDecimal descuentosTotal = BigDecimal.ZERO;

        for (DetalleCotizacion detalle : cotizacion.getItems()) {
            detalle.setCotizacion(cotizacion);
            subtotal = subtotal.add(detalle.getTotal());
            ivaTotal = ivaTotal.add(detalle.getIva());
            descuentosTotal = descuentosTotal.add(detalle.getDescuento());
        }

        cotizacion.setGravada(subtotal.subtract(ivaTotal));
        cotizacion.setIva(ivaTotal);
        cotizacion.setDescuentos(descuentosTotal);
        cotizacion.setTotalCotizacion(subtotal);

        cotizacionRepository.save(cotizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CotizacionDTO> getCotizacionesByCliente(Long clienteId) {
        return cotizacionRepository.findCotizacionesByClienteId(clienteId)
                .stream()
                .map(cotizacionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CotizacionDTO getCotizacionByNumero(String numeroCotizacion) {
        Cotizacion cotizacion = cotizacionRepository.findByNumeroCotizacionWithItems(numeroCotizacion)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));
        return cotizacionMapper.toDto(cotizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CotizacionDTO> getCotizacionesActivas() {
        return cotizacionRepository.findCotizacionesActivas()
                .stream()
                .map(cotizacionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void enviarCotizacion(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));

        if (!"BORRADOR".equals(cotizacion.getEstado())) {
            throw new CotizacionException("Solo se pueden enviar cotizaciones en estado BORRADOR");
        }

        cotizacion.setEstado("ENVIADA");
        cotizacionRepository.save(cotizacion);
    }

    @Override
    @Transactional
    public void aceptarCotizacion(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));

        if (!"ENVIADA".equals(cotizacion.getEstado())) {
            throw new CotizacionException("Solo se pueden aceptar cotizaciones en estado ENVIADA");
        }

        cotizacion.setEstado("ACEPTADA");
        cotizacionRepository.save(cotizacion);
    }

    @Override
    @Transactional
    public void rechazarCotizacion(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));

        if (!"ENVIADA".equals(cotizacion.getEstado())) {
            throw new CotizacionException("Solo se pueden rechazar cotizaciones en estado ENVIADA");
        }

        cotizacion.setEstado("RECHAZADA");
        cotizacionRepository.save(cotizacion);
    }

    @Override
    @Transactional
    public void anularCotizacion(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));

        if ("RECHAZADA".equals(cotizacion.getEstado()) || "VENCIDA".equals(cotizacion.getEstado())) {
            throw new CotizacionException("La cotización ya está cerrada");
        }

        cotizacion.setEstado("VENCIDA");
        cotizacionRepository.save(cotizacion);
    }

    @Override
    @Transactional
    public PedidoDTO convertirAPedido(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));

        if (!"ACEPTADA".equals(cotizacion.getEstado())) {
            throw new CotizacionException("Solo se pueden convertir cotizaciones ACEPTADAS a pedido");
        }

        // Crear PedidoDTO a partir de la cotización
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setClienteId(cotizacion.getCliente().getTerceroId().longValue());
        pedidoDTO.setBodegaId(cotizacion.getBodega().getCodigo());
        pedidoDTO.setFechaEmision(LocalDate.now());
        pedidoDTO.setFechaEntregaEsperada(LocalDate.now().plusDays(7));
        pedidoDTO.setObservaciones("Generado desde cotización: " + cotizacion.getNumeroCotizacion());
        pedidoDTO.setSubtotal(cotizacion.getGravada());
        pedidoDTO.setIva(cotizacion.getIva());
        pedidoDTO.setTotal(cotizacion.getTotalCotizacion());
        pedidoDTO.setUsuarioCreacion(cotizacion.getUsuarioCreacion());
        pedidoDTO.setCotizacionId(cotizacionId);

        if (cotizacion.getCajero() != null) {
            pedidoDTO.setCajeroId(Long.valueOf(cotizacion.getCajero().getCajeroId()));
        }

        // Mapear items
        List<DetallePedidoDTO> items = cotizacion.getItems().stream().map(dc -> {
            DetallePedidoDTO dp = new DetallePedidoDTO();
            dp.setCodigoProducto(dc.getCodigoProducto());
            dp.setCodigoBarras(dc.getCodigoBarras());
            dp.setDescripcionProducto(dc.getDescripcionProducto());
            dp.setReferenciaVariantes(dc.getReferenciaVariantes());
            dp.setCantidad(dc.getCantidad());
            dp.setPrecioUnitario(dc.getPrecioUnitario());
            dp.setDescuento(dc.getDescuento());
            dp.setIvaPorcentaje(dc.getIva());
            dp.setTotal(dc.getTotal());
            return dp;
        }).collect(Collectors.toList());
        pedidoDTO.setItems(items);

        pedidoService.crearPedido(pedidoDTO);

        return pedidoDTO;
    }

    @Override
    @Transactional
    public VentaDTO convertirAVenta(Long cotizacionId, List<VentaMetodoPagoDTO> metodosPago) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new CotizacionException("Cotización no encontrada"));

        if (!"ACEPTADA".equals(cotizacion.getEstado())) {
            throw new CotizacionException("Solo se pueden facturar cotizaciones ACEPTADAS");
        }

        // Crear VentaDTO a partir de la cotización
        VentaDTO ventaDTO = new VentaDTO();
        ventaDTO.setClienteId(cotizacion.getCliente().getTerceroId().longValue());
        ventaDTO.setBodegaId(cotizacion.getBodega().getCodigo());
        ventaDTO.setFechaEmision(LocalDate.now());
        ventaDTO.setFechaEntregaEsperada(LocalDate.now());
        ventaDTO.setObservaciones("Facturado desde cotización: " + cotizacion.getNumeroCotizacion());
        ventaDTO.setSubtotal(cotizacion.getGravada());
        ventaDTO.setIva(cotizacion.getIva());
        ventaDTO.setTotal(cotizacion.getTotalCotizacion());
        ventaDTO.setUsuarioCreacion(cotizacion.getUsuarioCreacion());
        ventaDTO.setMetodosPago(metodosPago);

        if (cotizacion.getCajero() != null) {
            ventaDTO.setCajeroId(Long.valueOf(cotizacion.getCajero().getCajeroId()));
        }

        // Mapear items de cotización a detalles de venta
        List<DetalleVentaDTO> items = cotizacion.getItems().stream().map(dc -> {
            DetalleVentaDTO dv = new DetalleVentaDTO();
            dv.setCodigoProducto(dc.getCodigoProducto());
            dv.setCodigoBarras(dc.getCodigoBarras());
            dv.setDescripcionProducto(dc.getDescripcionProducto());
            dv.setReferenciaVariantes(dc.getReferenciaVariantes());
            dv.setCantidad(dc.getCantidad());
            dv.setPrecioUnitario(dc.getPrecioUnitario());
            dv.setDescuento(dc.getDescuento());
            dv.setIvaPorcentaje(dc.getIva());
            dv.setTotal(dc.getTotal());
            return dv;
        }).collect(Collectors.toList());
        ventaDTO.setItems(items);

        // Crear la venta (descuenta inventario, ajusta precios, etc.)
        ventaService.crearVenta(ventaDTO);

        // Marcar la cotización como facturada
        cotizacion.setEstado("FACTURADA");
        cotizacionRepository.save(cotizacion);

        return ventaDTO;
    }
}





