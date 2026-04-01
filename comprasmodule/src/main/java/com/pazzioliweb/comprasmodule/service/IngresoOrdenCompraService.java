package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.comprasmodule.dtos.DetalleOrdenCompraDTO;
import com.pazzioliweb.comprasmodule.dtos.OrdenCompraDTO;
import com.pazzioliweb.comprasmodule.entity.OrdenCompra;
import java.util.List;

public interface IngresoOrdenCompraService {
    void ingresarOrdenCompra(Long ordenId, List<DetalleOrdenCompraDTO> detallesRecibidos, String numeroFacturaProveedor);
    List<OrdenCompraDTO> getOrdenesPendientesByProveedor(Integer proveedorId);
    OrdenCompraDTO getOrdenCompraByNumero(String numeroOrden);
}
