package com.pazzioliweb.ventasmodule.service;

import com.pazzioliweb.ventasmodule.dtos.DevolucionDTO;
import com.pazzioliweb.ventasmodule.dtos.DevolucionRequestDTO;

import java.time.LocalDate;
import java.util.List;

public interface DevolucionService {

    DevolucionDTO registrarDevolucion(DevolucionRequestDTO request);

    /** Retorna TODAS las devoluciones del sistema. */
    List<DevolucionDTO> getAllDevoluciones();

    /** Devoluciones de una venta específica. */
    List<DevolucionDTO> getDevolucionesByVenta(Long ventaId);

    /** Devoluciones registradas por un cajero específico. */
    List<DevolucionDTO> getDevolucionesByCajero(Integer cajeroId);

    /**
     * Devoluciones con filtros combinados:
     * - cajeroId  → null = todos los cajeros
     * - fechaInicio / fechaFin → null = sin filtro de fecha
     */
    List<DevolucionDTO> getDevolucionesByFiltros(Integer cajeroId, LocalDate fechaInicio, LocalDate fechaFin);

    /** Detalle de una devolución por su ID. */
    DevolucionDTO getDevolucionById(Long devolucionId);
}

