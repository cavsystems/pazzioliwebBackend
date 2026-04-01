package com.pazzioliweb.comprasmodule.service.impl;

import com.pazzioliweb.comprasmodule.dtos.HistorialCompraDTO;
import com.pazzioliweb.comprasmodule.entity.OrdenCompra;
import com.pazzioliweb.comprasmodule.mapper.OrdenCompraMapper;
import com.pazzioliweb.comprasmodule.repository.OrdenCompraRepository;
import com.pazzioliweb.comprasmodule.service.HistorialCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HistorialCompraServiceImpl implements HistorialCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final OrdenCompraMapper ordenCompraMapper;

    @Autowired
    public HistorialCompraServiceImpl(OrdenCompraRepository ordenCompraRepository,
                                      OrdenCompraMapper ordenCompraMapper) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.ordenCompraMapper = ordenCompraMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistorialCompraDTO> obtenerHistorial(Integer bodegaId, Integer proveedorId,
                                                     LocalDate fechaInicio, LocalDate fechaFin,
                                                     String estado, Pageable pageable) {
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusMonths(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        // Obtener el historial de compras paginado
        Page<OrdenCompra> ordenesPage = ordenCompraRepository.findHistorialCompras(
                bodegaId, proveedorId, fechaInicio, fechaFin, estado, pageable);

        // Convertir las órdenes a DTOs
        List<HistorialCompraDTO> historialDTOs = ordenesPage.getContent().stream()
                .map(orden -> {
                    HistorialCompraDTO dto = new HistorialCompraDTO();
                    dto.setId(orden.getId());
                    dto.setNumeroOrden(orden.getNumeroOrden());
                    dto.setFechaEmision(orden.getFechaEmision());
                    dto.setProveedorNombre(orden.getProveedor() != null ? orden.getProveedor().getRazonSocial() : null);
                    dto.setBodegaNombre(orden.getBodega() != null ? orden.getBodega().getNombre() : null);
                    dto.setEstado(orden.getEstado());
                    dto.setTotal(orden.getTotalOrdenCompra());
                    return dto;
                })
                .collect(Collectors.toList());

        // Obtener estadísticas
        Map<String, Object> estadisticas = (Map<String, Object>) obtenerEstadisticas(bodegaId, fechaInicio, fechaFin);

        // Agregar estadísticas al primer elemento del historial
        if (!historialDTOs.isEmpty()) {
            HistorialCompraDTO firstDto = historialDTOs.get(0);

            // Add other statistics as needed
        }

        return new PageImpl<>(historialDTOs, pageable, ordenesPage.getTotalElements());
    }

    @Override
    public Object obtenerEstadisticas(Integer bodegaId, LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusMonths(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        Map<String, Object> estadisticas = new HashMap<>();

        // Obtener total de compras
        Double totalCompras = ordenCompraRepository.sumTotalByPeriodo(bodegaId, fechaInicio, fechaFin);
        estadisticas.put("totalCompras", totalCompras != null ? totalCompras : 0);

        // Obtener cantidad de órdenes
        Long cantidadOrdenes = ordenCompraRepository.countByPeriodo(bodegaId, fechaInicio, fechaFin);
        estadisticas.put("cantidadOrdenes", cantidadOrdenes);

        // Obtener promedio de compra
        if (cantidadOrdenes > 0) {
            estadisticas.put("promedioCompra", totalCompras / cantidadOrdenes);
        } else {
            estadisticas.put("promedioCompra", 0);
        }

        // Obtener compras por estado
        estadisticas.put("porEstado", ordenCompraRepository.countByEstadoAndPeriodo(bodegaId, fechaInicio, fechaFin));

        // Obtener compras por proveedor (top 5)
        estadisticas.put("topProveedores", ordenCompraRepository.findTopProveedores(bodegaId, fechaInicio, fechaFin, 5));

        return estadisticas;
    }
}
