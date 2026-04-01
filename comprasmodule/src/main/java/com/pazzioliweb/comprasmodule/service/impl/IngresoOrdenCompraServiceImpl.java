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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngresoOrdenCompraServiceImpl implements IngresoOrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final ProductoService productoService;
    private final ProductoClient productoClient;
    private final OrdenCompraMapper ordenCompraMapper;
    private final CuentaPorPagarService cuentaPorPagarService;

    @Autowired
    public IngresoOrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository, ProductoService productoService, ProductoClient productoClient, OrdenCompraMapper ordenCompraMapper, CuentaPorPagarService cuentaPorPagarService) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.productoService = productoService;
        this.productoClient = productoClient;
        this.ordenCompraMapper = ordenCompraMapper;
        this.cuentaPorPagarService = cuentaPorPagarService;
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
        OrdenCompra orden = ordenCompraRepository.findById(ordenId)
                .orElseThrow(() -> new OrdenCompraException("Orden de compra no encontrada"));

        if ("RECIBIDA".equals(orden.getEstado()) || "ANULADA".equals(orden.getEstado())) {
            throw new OrdenCompraException("La orden ya ha sido recibida o anulada");
        }

        // Update details with received quantities and flags
        for (DetalleOrdenCompraDTO dto : detallesRecibidos) {
            DetalleOrdenCompra detalle = orden.getItems().stream()
                    .filter(d -> d.getId().equals(dto.getId()))
                    .findFirst()
                    .orElseThrow(() -> new OrdenCompraException("Detalle de orden no encontrado: " + dto.getId()));
            detalle.setCantidadRecibida(dto.getCantidadRecibida());
            detalle.setRecibido(dto.isRecibido());
            detalle.setManifiesto(dto.getManifiesto());
        }

        // Adjust inventory: add received quantities aqui se actualiza el inventario es decir la existencia con base a
        // la cantidad recibida
        for (DetalleOrdenCompra detalle : orden.getItems()) {
            productoClient.actualizarInventario(
                    detalle.getCodigoProducto(),
                    detalle.getReferenciaVariantes(),
                    detalle.getCantidadRecibida(),
                    orden.getBodega().getCodigo()
            );
        }

        // Determine order status: RECIBIDA if all items fully received, else RECIBIDA_PARCIAL
        boolean allReceived = orden.getItems().stream()
                .allMatch(d -> d.getCantidadRecibida().equals(d.getCantidad()));
        orden.setEstado(allReceived ? "RECIBIDA" : "RECIBIDA_PARCIAL");

        // Update numero_factura_proveedor in CuentaPorPagar
        cuentaPorPagarService.actualizarNumeroFacturaProveedor(orden.getNumeroOrden(), numeroFacturaProveedor);

        ordenCompraRepository.save(orden);
    }
}
