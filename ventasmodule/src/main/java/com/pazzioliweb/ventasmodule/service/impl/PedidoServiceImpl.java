package com.pazzioliweb.ventasmodule.service.impl;

import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.ventasmodule.dtos.DetalleVentaDTO;
import com.pazzioliweb.ventasmodule.dtos.PedidoDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import com.pazzioliweb.ventasmodule.entity.DetallePedido;
import com.pazzioliweb.ventasmodule.entity.Pedido;
import com.pazzioliweb.ventasmodule.exception.PedidoException;
import com.pazzioliweb.ventasmodule.mapper.PedidoMapper;
import com.pazzioliweb.ventasmodule.repository.PedidoRepository;
import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;
import com.pazzioliweb.cajerosmodule.repositori.CajeroRepository;
import com.pazzioliweb.cajerosmodule.service.DetalleCajeroService;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.ventasmodule.repository.PedidoSpecification;
import com.pazzioliweb.ventasmodule.service.PedidoService;
import com.pazzioliweb.ventasmodule.service.VentaService;
import com.pazzioliweb.vendedoresmodule.entity.Vendedores;
import com.pazzioliweb.vendedoresmodule.repositori.VendedoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final VendedoresRepository vendedoresRepository;
    private final CajeroRepository cajeroRepository;
    private final DetalleCajeroService detalleCajeroService;
    private final RedisTemplate<String, DatosSesiones> redisTemplate;

    @Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             PedidoMapper pedidoMapper,
                             @Lazy VentaService ventaService,
                             VendedoresRepository vendedoresRepository,
                             CajeroRepository cajeroRepository,
                             DetalleCajeroService detalleCajeroService,
                             RedisTemplate<String, DatosSesiones> redisTemplate) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoMapper = pedidoMapper;
        this.ventaService = ventaService;
        this.vendedoresRepository = vendedoresRepository;
        this.cajeroRepository = cajeroRepository;
        this.detalleCajeroService = detalleCajeroService;
        this.redisTemplate = redisTemplate;
    }

    // ─── Helper: sesión activa desde Redis ───────────────────────────────────
    private DatosSesiones obtenerSesionActiva() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getCredentials() != null) {
                String sessionId = auth.getCredentials().toString();
                return redisTemplate.opsForValue().get(sessionId);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener sesión activa en pedido: " + e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional
    public PedidoDTO crearPedido(PedidoDTO pedidoDTO) {
        Pedido pedido = pedidoMapper.toEntity(pedidoDTO);
        pedido.setEstado("PENDIENTE");
        pedido.setFechaCreacion(LocalDate.now());

        // ── Asignar cajero: explícito en DTO o auto-detectar desde sesión Redis ──
        if (pedidoDTO.getCajeroId() != null) {
            Cajero cajero = cajeroRepository.findById(pedidoDTO.getCajeroId().intValue())
                    .orElseThrow(() -> new PedidoException("Cajero no encontrado: " + pedidoDTO.getCajeroId()));
            pedido.setCajero(cajero);
        } else {
            DatosSesiones sesion = obtenerSesionActiva();
            if (sesion != null && sesion.getCajeroId() != null) {
                Cajero cajero = cajeroRepository.findById(sesion.getCajeroId())
                        .orElseThrow(() -> new PedidoException("Cajero de sesión no encontrado: " + sesion.getCajeroId()));
                pedido.setCajero(cajero);
            }
        }

        // ── Asignar vendedor si viene en el DTO ──────────────────────────────
        if (pedidoDTO.getVendedorId() != null) {
            Vendedores vendedor = vendedoresRepository.findById(pedidoDTO.getVendedorId())
                    .orElseThrow(() -> new PedidoException("Vendedor no encontrado: " + pedidoDTO.getVendedorId()));
            pedido.setVendedor(vendedor);
        }

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
        Bodegas bodega=new Bodegas();
        Terceros tercero=new Terceros();
        Cajero cajero=new Cajero();
        Vendedores vendedor=new Vendedores();
                bodega.setCodigo(pedidoDTO.getBodegaId());
        tercero.setTerceroId(pedidoDTO.getClienteId().intValue());
        cajero.setCajeroId(pedidoDTO.getCajeroId().intValue());
        vendedor.setVendedor_id(pedidoDTO.getVendedorId().intValue());

                pedido.setBodega(bodega);
                pedido.setCliente(tercero);
                pedido.setCajero(cajero);
                pedido.setVendedor(vendedor);
        pedido.setBodega(bodega);
        pedido.setGravada(subtotal.subtract(ivaTotal));
        pedido.setIva(ivaTotal);
        pedido.setDescuentos(descuentosTotal);
        pedido.setTotalPedido(subtotal);

        Pedido guardado = pedidoRepository.save(pedido);

        final PedidoDTO pedidocreado= pedidoMapper.toDto(guardado);

        // ── Registrar movimiento PEDIDO en el cajero (informativo, no afecta caja) ──
        DatosSesiones sesion = obtenerSesionActiva();
        if (sesion != null && sesion.getDetalleCajeroId() != null) {
            try {
                detalleCajeroService.registrarMovimiento(
                        sesion.getDetalleCajeroId(),
                        MovimientoCajero.TipoMovimiento.PEDIDO,
                        guardado.getNumeroPedido(),
                        guardado.getId(),
                        guardado.getTotalPedido(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        "Pedido " + guardado.getNumeroPedido()
                );
            } catch (Exception e) {
                System.out.println("Error al registrar pedido en cajero: " + e.getMessage());
            }
        }
        return pedidocreado;
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
        ventaDTO.setNumeroVenta(String.valueOf(ventaService.getUltimaVentaId() + 1));
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

        // Propagar vendedor del pedido a la venta
        if (pedido.getVendedor() != null) {
            ventaDTO.setVendedorId(pedido.getVendedor().getVendedor_id());
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


/*
metodo para filtrar pedidos segun parametros
 */
    @Transactional
    @Override
    public List<PedidoDTO> getPedidosByFiltros(Long terceroId, Integer vendedorId, Integer cajeroId,
                                             LocalDate fechaInicio, LocalDate fechaFin) {
        return  pedidoRepository
                .findAll(PedidoSpecification.conFiltros(terceroId, vendedorId, cajeroId, fechaInicio, fechaFin))
                .stream()
                .map(pedidoMapper::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    @Override
    public Long getUltimopedido() {
       return  pedidoRepository.getUltimopedidoId();
    }
}


