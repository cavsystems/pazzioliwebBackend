package com.pazzioliweb.ventasmodule.service.impl;

import com.pazzioliweb.ventasmodule.dtos.DetalleVentaDTO;
import com.pazzioliweb.ventasmodule.dtos.PedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import com.pazzioliweb.ventasmodule.entity.DetallePedido;
import com.pazzioliweb.ventasmodule.entity.Pedido;
import com.pazzioliweb.ventasmodule.exception.PedidoException;
import com.pazzioliweb.ventasmodule.mapper.PedidoMapper;
import com.pazzioliweb.ventasmodule.repository.PedidoRepository;
import com.pazzioliweb.ventasmodule.service.PedidoService;
import com.pazzioliweb.ventasmodule.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final VentaService ventaService;

    @Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                              PedidoMapper pedidoMapper,
                              @Lazy VentaService ventaService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoMapper = pedidoMapper;
        this.ventaService = ventaService;
    }

    @Override
    @Transactional
    public void crearPedido(PedidoDTO pedidoDTO) {
        Pedido pedido = pedidoMapper.toEntity(pedidoDTO);
        pedido.setEstado("PENDIENTE");
        pedido.setFechaCreacion(LocalDate.now());

        // Calcular totales
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal ivaTotal = BigDecimal.ZERO;
        BigDecimal descuentosTotal = BigDecimal.ZERO;

        for (DetallePedido detalle : pedido.getItems()) {
            detalle.setPedido(pedido);
            subtotal = subtotal.add(detalle.getTotal());
            ivaTotal = ivaTotal.add(detalle.getIva());
            descuentosTotal = descuentosTotal.add(detalle.getDescuento());
        }

        pedido.setGravada(subtotal.subtract(ivaTotal));
        pedido.setIva(ivaTotal);
        pedido.setDescuentos(descuentosTotal);
        pedido.setTotalPedido(subtotal);

        pedidoRepository.save(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> getPedidosByCliente(Long clienteId) {
        return pedidoRepository.findPedidosByClienteId(clienteId)
                .stream()
                .map(pedidoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDTO getPedidoByNumero(String numeroPedido) {
        Pedido pedido = pedidoRepository.findByNumeroPedidoWithItems(numeroPedido)
                .orElseThrow(() -> new PedidoException("Pedido no encontrado"));
        return pedidoMapper.toDto(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> getPedidosActivos() {
        return pedidoRepository.findPedidosActivos()
                .stream()
                .map(pedidoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long pedidoId, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new PedidoException("Pedido no encontrado"));

        List<String> estadosValidos = Arrays.asList("PENDIENTE", "EN_PROCESO", "DESPACHADO", "ENTREGADO", "CANCELADO");
        if (!estadosValidos.contains(nuevoEstado)) {
            throw new PedidoException("Estado no válido: " + nuevoEstado);
        }

        if ("CANCELADO".equals(pedido.getEstado()) || "ENTREGADO".equals(pedido.getEstado())) {
            throw new PedidoException("No se puede cambiar el estado de un pedido " + pedido.getEstado());
        }

        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public void cancelarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new PedidoException("Pedido no encontrado"));

        if ("ENTREGADO".equals(pedido.getEstado()) || "CANCELADO".equals(pedido.getEstado())) {
            throw new PedidoException("No se puede cancelar un pedido " + pedido.getEstado());
        }

        pedido.setEstado("CANCELADO");
        pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public VentaDTO convertirAVenta(Long pedidoId, List<VentaMetodoPagoDTO> metodosPago) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new PedidoException("Pedido no encontrado"));

        if (!"PENDIENTE".equals(pedido.getEstado()) && !"EN_PROCESO".equals(pedido.getEstado())) {
            throw new PedidoException("Solo se pueden convertir pedidos PENDIENTES o EN_PROCESO a venta");
        }

        // Crear VentaDTO a partir del pedido
        VentaDTO ventaDTO = new VentaDTO();
        ventaDTO.setClienteId(pedido.getCliente().getTerceroId().longValue());
        ventaDTO.setBodegaId(pedido.getBodega().getCodigo());
        ventaDTO.setFechaEmision(LocalDate.now());
        ventaDTO.setFechaEntregaEsperada(pedido.getFechaEntregaEsperada());
        ventaDTO.setObservaciones("Generado desde pedido: " + pedido.getNumeroPedido());
        ventaDTO.setSubtotal(pedido.getGravada());
        ventaDTO.setIva(pedido.getIva());
        ventaDTO.setTotal(pedido.getTotalPedido());
        ventaDTO.setUsuarioCreacion(pedido.getUsuarioCreacion());
        ventaDTO.setMetodosPago(metodosPago);

        if (pedido.getCajero() != null) {
            ventaDTO.setCajeroId(Long.valueOf(pedido.getCajero().getCajeroId()));
        }

        // Mapear items
        List<DetalleVentaDTO> items = pedido.getItems().stream().map(dp -> {
            DetalleVentaDTO dv = new DetalleVentaDTO();
            dv.setCodigoProducto(dp.getCodigoProducto());
            dv.setCodigoBarras(dp.getCodigoBarras());
            dv.setDescripcionProducto(dp.getDescripcionProducto());
            dv.setReferenciaVariantes(dp.getReferenciaVariantes());
            dv.setCantidad(dp.getCantidad());
            dv.setPrecioUnitario(dp.getPrecioUnitario());
            dv.setDescuento(dp.getDescuento());
            dv.setIvaPorcentaje(dp.getIva());
            dv.setTotal(dp.getTotal());
            return dv;
        }).collect(Collectors.toList());
        ventaDTO.setItems(items);

        ventaService.crearVenta(ventaDTO);

        // Marcar pedido como entregado
        pedido.setEstado("ENTREGADO");
        pedidoRepository.save(pedido);

        return ventaDTO;
    }
}


