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
import com.pazzioliweb.comprasmodule.service.ProductoService;
import com.pazzioliweb.movimientosinventariomodule.service.MovimientoInventarioAutoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Autowired
    public IngresoOrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository,
                                          ProductoService productoService,
                                          ProductoClient productoClient,
                                          OrdenCompraMapper ordenCompraMapper,
                                          CuentaPorPagarService cuentaPorPagarService,
                                          MovimientoInventarioAutoService movimientoInventarioAutoService) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.productoService = productoService;
        this.productoClient = productoClient;
        this.ordenCompraMapper = ordenCompraMapper;
        this.cuentaPorPagarService = cuentaPorPagarService;
        this.movimientoInventarioAutoService = movimientoInventarioAutoService;
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
        if (!"PENDIENTE".equals(orden.getEstado())) {
            throw new OrdenCompraException("La orden de compra no está en estado PENDIENTE");
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

        // Paso 2: actualizar inventario SOLO de los items que tienen cantidad recibida > 0
        int actualizados = 0;
        int omitidos = 0;
        for (DetalleOrdenCompra detalle : orden.getItems()) {
            Integer cant = detalle.getCantidadRecibida();
            if (cant == null || cant <= 0) {
                omitidos++;
                continue;
            }
            try {
                log.info("📦 Sumando inventario: producto={}, variante={}, cantidad=+{}, bodega={}",
                        detalle.getCodigoProducto(), detalle.getReferenciaVariantes(),
                        cant, orden.getBodega().getCodigo());
                productoClient.actualizarInventario(
                        detalle.getCodigoProducto(),
                        detalle.getReferenciaVariantes(),
                        cant,
                        orden.getBodega().getCodigo()
                );
                actualizados++;
            } catch (Exception ex) {
                log.error("❌ Error sumando inventario del detalle {} (producto {}): {}",
                        detalle.getId(), detalle.getCodigoProducto(), ex.getMessage());
                throw ex;  // propagar para hacer rollback completo
            }
        }
        log.info("Inventario actualizado: {} items sumados, {} omitidos (cantidad recibida = 0)",
                actualizados, omitidos);

        // Paso 3: definir estado
        boolean allReceived = orden.getItems().stream()
                .allMatch(d -> d.getCantidadRecibida() != null && d.getCantidadRecibida().equals(d.getCantidad()));
        orden.setEstado(allReceived ? "RECIBIDA" : "RECIBIDA_PARCIAL");
        log.info("Estado final: {}", orden.getEstado());

        // Paso 4: actualizar el numero_factura_proveedor en CuentaPorPagar
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
                double costoUnit = d.getPrecioUnitario() != null ? d.getPrecioUnitario().doubleValue() : 0.0;
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
            movimientoInventarioAutoService.registrarEntradaPorCompra(
                    orden.getNumeroOrden(),
                    orden.getId(),
                    orden.getBodega().getCodigo(),
                    orden.getFechaCreacion() != null ? orden.getFechaCreacion() : LocalDate.now(),
                    items,
                    tipoComprobante
            );
        } catch (Exception ex) {
            log.error("[MovInv-Compra] Error generando movimiento (no crítico): {}", ex.getMessage());
        }
    }
}
