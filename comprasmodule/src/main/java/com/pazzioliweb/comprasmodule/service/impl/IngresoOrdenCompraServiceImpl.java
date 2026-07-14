package com.pazzioliweb.comprasmodule.service.impl;

import com.pazzioliweb.comprasmodule.client.ProductoClient;
import com.pazzioliweb.comprasmodule.dtos.DetalleOrdenCompraDTO;
import com.pazzioliweb.comprasmodule.dtos.OrdenCompraDTO;
import com.pazzioliweb.comprasmodule.dtos.ProductoActualizarCrearDTO;
import com.pazzioliweb.comprasmodule.entity.DetalleOrdenCompra;
import com.pazzioliweb.comprasmodule.entity.OrdenCompra;
import com.pazzioliweb.comprasmodule.exception.OrdenCompraException;
import com.pazzioliweb.comprasmodule.mapper.OrdenCompraMapper;
import com.pazzioliweb.comprasmodule.repository.OrdenCompraRepository;
import com.pazzioliweb.comprasmodule.service.IngresoOrdenCompraService;
import com.pazzioliweb.comprasmodule.service.CuentaPorPagarService;
import com.pazzioliweb.comprasmodule.service.ConfiguracionComprasService;
import com.pazzioliweb.comprasmodule.service.ProductoService;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService;
import com.pazzioliweb.movimientosinventariomodule.service.MovimientoInventarioAutoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngresoOrdenCompraServiceImpl implements IngresoOrdenCompraService {

    private static final Logger log = LoggerFactory.getLogger(IngresoOrdenCompraServiceImpl.class);

    private final OrdenCompraRepository ordenCompraRepository;
    private final ProductoService productoService;
    private final ProductoClient productoClient;
    private final OrdenCompraMapper ordenCompraMapper;
    private final CuentaPorPagarService cuentaPorPagarService;
    private final MovimientoInventarioAutoService movimientoInventarioAutoService;
    private final AsignacionComprobanteService asignacionComprobante;
    private final ComprobanteContableRepository comprobanteRepository;
    private final ConfiguracionComprasService configCompras;

    @Autowired
    public IngresoOrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository,
                                          ProductoService productoService,
                                          ProductoClient productoClient,
                                          OrdenCompraMapper ordenCompraMapper,
                                          CuentaPorPagarService cuentaPorPagarService,
                                          MovimientoInventarioAutoService movimientoInventarioAutoService,
                                          AsignacionComprobanteService asignacionComprobante,
                                          ComprobanteContableRepository comprobanteRepository,
                                          ConfiguracionComprasService configCompras) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.productoService = productoService;
        this.productoClient = productoClient;
        this.ordenCompraMapper = ordenCompraMapper;
        this.cuentaPorPagarService = cuentaPorPagarService;
        this.movimientoInventarioAutoService = movimientoInventarioAutoService;
        this.asignacionComprobante = asignacionComprobante;
        this.comprobanteRepository = comprobanteRepository;
        this.configCompras = configCompras;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> getOrdenesPendientesByProveedor(Integer proveedorId) {
        return ordenCompraRepository.findOrdenesPendientesByProveedorId(proveedorId)
                .stream()
                .map(ordenCompraMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenCompraDTO getOrdenCompraByNumero(String numeroOrden) {
        OrdenCompra orden = ordenCompraRepository.findByNumeroOrdenWithItems(numeroOrden)
                .orElseThrow(() -> new OrdenCompraException("Orden de compra no encontrada"));
        if (!"PENDIENTE".equals(orden.getEstado()) && !"RECIBIDA_PARCIAL".equals(orden.getEstado())) {
            throw new OrdenCompraException("La orden de compra no está en estado PENDIENTE o RECIBIDA_PARCIAL");
        }
        return ordenCompraMapper.toDto(orden);
    }

    @Override
    @Transactional
    public void ingresarOrdenCompra(Long ordenId, List<DetalleOrdenCompraDTO> detallesRecibidos, String numeroFacturaProveedor) {
        log.info("══════ INGRESO DE ORDEN DE COMPRA ID={} ══════", ordenId);
        log.info("Detalles recibidos del frontend: {}", detallesRecibidos != null ? detallesRecibidos.size() : 0);

        OrdenCompra orden = ordenCompraRepository.findById(ordenId)
                .orElseThrow(() -> new OrdenCompraException("Orden de compra no encontrada"));

        log.info("Orden encontrada: {} - Estado: {} - Items: {}",
                orden.getNumeroOrden(), orden.getEstado(),
                orden.getItems() != null ? orden.getItems().size() : 0);

        if ("RECIBIDA".equals(orden.getEstado()) || "ANULADA".equals(orden.getEstado())) {
            throw new OrdenCompraException("La orden ya ha sido recibida o anulada");
        }

        // Paso 1: actualizar las cantidades recibidas en los detalles de la orden
        if (detallesRecibidos != null) {
            for (DetalleOrdenCompraDTO dto : detallesRecibidos) {
                if (dto.getId() == null) {
                    log.warn("⚠️ DTO recibido sin id: producto={}, cantidad={} — se omite",
                            dto.getCodigoProducto(), dto.getCantidadRecibida());
                    continue;
                }
                DetalleOrdenCompra detalle = orden.getItems().stream()
                        .filter(d -> d.getId().equals(dto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new OrdenCompraException("Detalle de orden no encontrado: " + dto.getId()));

                Integer cantRecibida = dto.getCantidadRecibida() != null ? dto.getCantidadRecibida() : 0;
                detalle.setCantidadRecibida(cantRecibida);
                detalle.setRecibido(dto.isRecibido());
                if (dto.getManifiesto() != null) {
                    detalle.setManifiesto(dto.getManifiesto());
                }
                log.info("  → Detalle {} ({}/{}): pedido={}, recibido={}",
                        detalle.getId(), detalle.getCodigoProducto(), detalle.getReferenciaVariantes(),
                        detalle.getCantidad(), cantRecibida);
            }
        }

        // Paso 2: el inventario/existencias lo mantiene ÚNICAMENTE el movimiento de kardex
        // (generarMovimientoInventarioCompra, más abajo). Antes se sumaba aquí con
        // productoClient.actualizarInventario Y otra vez en el kardex → doble conteo en productos
        // sin kardex previo (el saldo base se leía ya incrementado). Un solo escritor lo evita.

        // Paso 3: definir estado
        boolean allReceived = orden.getItems().stream()
                .allMatch(d -> d.getCantidadRecibida() != null && d.getCantidadRecibida().equals(d.getCantidad()));
        orden.setEstado(allReceived ? "RECIBIDA" : "RECIBIDA_PARCIAL");

        // Actualizar fecha_recibida cuando la orden pasa a RECIBIDA o RECIBIDA_PARCIAL
        if ("RECIBIDA".equals(orden.getEstado()) || "RECIBIDA_PARCIAL".equals(orden.getEstado())) {
            orden.setFechaRecibida(LocalDateTime.now());
        }
        
        log.info("Estado final: {}", orden.getEstado());

        // Paso 4: asignar comprobante contable si la orden no tiene uno
        if (orden.getComprobante() == null) {
            // Determinar tipo de comprobante basado en métodos de pago de la orden
            boolean esCredito = tieneMetodoPagoCreditoEnOrden(orden.getMetodosPago());
            TipoMovimientoComprobante tipo = esCredito ? TipoMovimientoComprobante.CR : TipoMovimientoComprobante.CC;
            Integer cajeroId = orden.getCajeroId();
            try {
                Integer cajeroEfectivo = resolverCajeroParaComprobante(cajeroId, tipo);
                AsignacionComprobanteService.Resultado r = asignacionComprobante.asignar(cajeroEfectivo, tipo);
                orden.setComprobante(r.getComprobante());
                orden.setConsecutivoComprobante(r.getConsecutivo());
                // Preservar el número OC original si aún no se había guardado
                if (orden.getNumeroOc() == null && orden.getNumeroOrden() != null
                        && orden.getNumeroOrden().startsWith("OC-")) {
                    orden.setNumeroOc(orden.getNumeroOrden());
                }
                // numero_orden pasa a ser el número del comprobante contable (CC-X / CR-X)
                orden.setNumeroOrden(r.getNumeroDocumento());
                log.info("Comprobante asignado: {} - {}", r.getNumeroDocumento(), tipo);
            } catch (AsignacionComprobanteService.ComprobanteNoConfiguradoException ex) {
                log.warn("No se pudo asignar comprobante: {}", ex.getMessage());
            } catch (OrdenCompraException ex) {
                log.warn("No se pudo resolver cajero para comprobante: {}", ex.getMessage());
            }
        }

        // Paso 5: actualizar el numero_factura_proveedor en CuentaPorPagar
        cuentaPorPagarService.actualizarNumeroFacturaProveedor(orden.getNumeroOrden(), numeroFacturaProveedor);

        ordenCompraRepository.save(orden);

        // Paso 5: registrar el movimiento de inventario ENTRADA + Kardex (trazabilidad)
        generarMovimientoInventarioCompra(orden);

        log.info("══════ INGRESO COMPLETADO: {} ══════", orden.getNumeroOrden());
    }

    /**
     * Registra un MovimientoInventario tipo ENTRADA con cada detalle ingresado.
     * Genera Kardex para tener trazabilidad por producto/bodega.
     * Try/catch defensivo: si falla, no rompe el ingreso (las existencias ya
     * están actualizadas vía productoClient.actualizarInventario).
     */
    private void generarMovimientoInventarioCompra(OrdenCompra orden) {
        try {
            List<MovimientoInventarioAutoService.ItemMovimiento> items =
                    MovimientoInventarioAutoService.nuevaLista();
            for (DetalleOrdenCompra d : orden.getItems()) {
                if (d.getCantidadRecibida() == null || d.getCantidadRecibida() <= 0) continue;
                // Costo unitario NETO de descuento (descuento por línea = %), para que el kardex
                // coincida con el débito contable a Inventarios (gravada neta). Igual que el flujo nuevo.
                double descPct = d.getDescuento() != null ? d.getDescuento().doubleValue() : 0.0;
                double costoUnit = (d.getPrecioUnitario() != null ? d.getPrecioUnitario().doubleValue() : 0.0) * (1.0 - descPct / 100.0);
                double cant = d.getCantidadRecibida().doubleValue();
                // IVA viene como porcentaje (p.ej. 19). Si en algún registro está como valor absoluto
                // (mayor que 100 sería raro), lo tratamos como porcentaje igual.
                double ivaPct = d.getIva() != null ? d.getIva().doubleValue() : 0.0;
                double subtotalLinea = costoUnit * cant;
                double totalLineaConIva = subtotalLinea * (1.0 + ivaPct / 100.0);
                items.add(new MovimientoInventarioAutoService.ItemMovimiento(
                        d.getCodigoProducto(),
                        d.getReferenciaVariantes(),
                        cant,
                        costoUnit,
                        totalLineaConIva
                ));
            }
            if (items.isEmpty()) {
                log.info("[MovInv-Compra] Sin items con cantidad recibida > 0 para orden {}", orden.getNumeroOrden());
                return;
            }
            // Tipo real del comprobante (CC contado / CR crédito) para que el historial
            // muestre el origen correcto en lugar de hardcodear "CC".
            String tipoComprobante = "CC";
            if (orden.getComprobante() != null && orden.getComprobante().getTipoMovimiento() != null) {
                tipoComprobante = orden.getComprobante().getTipoMovimiento().name();
            }
            Integer comprobanteId = orden.getComprobante() != null ? orden.getComprobante().getId().intValue() : null;
            Integer consecutivo = orden.getConsecutivoComprobante();
            movimientoInventarioAutoService.registrarEntradaPorCompra(
                    orden.getNumeroOrden(),
                    orden.getId(),
                    orden.getBodega().getCodigo(),
                    orden.getFechaEmision() != null ? orden.getFechaEmision()
                            : (orden.getFechaCreacion() != null ? orden.getFechaCreacion() : LocalDate.now()),
                    items,
                    tipoComprobante,
                    comprobanteId,
                    consecutivo
            );
        } catch (Exception ex) {
            log.error("[MovInv-Compra] Error generando movimiento (no crítico): {}", ex.getMessage());
        }
    }

    private Integer resolverCajeroParaComprobante(Integer cajeroUsuario, TipoMovimientoComprobante tipo) {
        if (cajeroUsuario != null
                && comprobanteRepository.findActivoByCajeroAndTipo(cajeroUsuario, tipo).isPresent()) {
            return cajeroUsuario;
        }
        Integer cajeroDefault = configCompras.obtenerCajeroDefaultId();
        if (cajeroDefault != null
                && comprobanteRepository.findActivoByCajeroAndTipo(cajeroDefault, tipo).isPresent()) {
            return cajeroDefault;
        }
        throw new OrdenCompraException(
            "No hay cajero válido para asignar el comprobante de " + tipo.name() +
            ". El usuario actual " + (cajeroUsuario == null ? "no es cajero" : "no tiene comprobante " + tipo.name()) +
            " y no se ha configurado un cajero por defecto para compras. " +
            "Configúrelo en Admin → Configuración de compras."
        );
    }

    private boolean tieneMetodoPagoCreditoEnOrden(List<com.pazzioliweb.comprasmodule.entity.OrdenCompraMetodoPago> metodosPago) {
        if (metodosPago == null || metodosPago.isEmpty()) return false;
        return metodosPago.stream().anyMatch(mp -> {
            com.pazzioliweb.metodospagomodule.entity.MetodosPago m = mp.getMetodoPago();
            return m != null && m.getTipoNegociacion() != null
                    && "Credito".equalsIgnoreCase(m.getTipoNegociacion().name());
        });
    }
}
