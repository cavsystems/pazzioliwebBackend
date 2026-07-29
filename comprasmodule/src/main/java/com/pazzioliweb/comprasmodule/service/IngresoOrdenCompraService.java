package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.comprasmodule.dtos.DetalleOrdenCompraDTO;
import com.pazzioliweb.comprasmodule.dtos.OrdenCompraDTO;
import com.pazzioliweb.comprasmodule.entity.OrdenCompra;
import java.util.List;

public interface IngresoOrdenCompraService {
    void ingresarOrdenCompra(Long ordenId, List<DetalleOrdenCompraDTO> detallesRecibidos, String numeroFacturaProveedor);
    List<OrdenCompraDTO> getOrdenesPendientesByProveedor(Integer proveedorId);
    OrdenCompraDTO getOrdenCompraByNumero(String numeroOrden);

    /**
     * Regenera el movimiento de inventario + kardex de una orden ya RECIBIDA cuyo
     * movimiento no se creó (p. ej. la transacción del kardex falló después del
     * ingreso). Es idempotente: si el movimiento ya existe, no duplica nada.
     */
    void regenerarMovimientoInventario(Long ordenId);
}
