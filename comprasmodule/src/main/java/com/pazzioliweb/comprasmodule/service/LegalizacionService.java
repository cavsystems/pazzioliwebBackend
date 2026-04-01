package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.comprasmodule.dtos.LegalizacionDTO;
import com.pazzioliweb.comprasmodule.dtos.LegalizacionRequestDTO;
import com.pazzioliweb.comprasmodule.dtos.OrdenCompraDTO;

import java.util.List;

public interface LegalizacionService {
    OrdenCompraDTO legalizarCompra(LegalizacionRequestDTO request);
    Long obtenerSiguienteId();
    List<LegalizacionDTO> obtenerTodasLasLegalizaciones();
    List<LegalizacionDTO> obtenerLegalizacionesPorProveedor(Long proveedorId);
}
