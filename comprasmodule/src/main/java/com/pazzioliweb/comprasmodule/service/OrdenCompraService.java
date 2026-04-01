package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.comprasmodule.dtos.DetalleOrdenCompraDTO;
import com.pazzioliweb.comprasmodule.dtos.ItemRecibidoDTO;
import com.pazzioliweb.comprasmodule.dtos.OrdenCompraDTO;
import com.pazzioliweb.comprasmodule.dtos.RealizarOrdenRequestDTO;
import com.pazzioliweb.comprasmodule.entity.OrdenCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrdenCompraService {
    Page<OrdenCompraDTO> buscarConFiltros(String estado, LocalDate fechaDesde, LocalDate fechaHasta,
                                          Integer proveedorId, Pageable pageable);

    Optional<OrdenCompraDTO> obtenerPorId(Long id);
    Optional<OrdenCompraDTO> obtenerPorNumeroOrden(String numeroOrden);
    List<OrdenCompraDTO> obtenerPorNumeroOrdenlist(String numeroOrden, Pageable pageable);
    OrdenCompraDTO crear(OrdenCompraDTO ordenCompraDTO);
    OrdenCompraDTO actualizar(OrdenCompraDTO ordenCompraDTO);
    void anular(Long id, String motivo);
    void recibirOrden(Long id, List<ItemRecibidoDTO> itemsRecibidos);
    List<OrdenCompraDTO> obtenerOrdenesPendientes();

    /**
     * Realiza la orden de compra: actualiza productos, crea orden y cuentas por pagar.
     */
    OrdenCompraDTO realizarOrden(RealizarOrdenRequestDTO request);

    /**
     * Cuenta el número total de órdenes de compra en el sistema.
     * @return Mapa con el conteo total de órdenes.
     */
    java.util.Map<String, Long> contarTotalOrdenes();

    List<OrdenCompraDTO> obtenerTodasLasOrdenes();
    List<OrdenCompraDTO> obtenerOrdenesPorProveedorYEstado(Integer proveedorId, String estado);
    List<DetalleOrdenCompraDTO> obtenerDetallesPorNumeroOrden(String numeroOrden);

    /**
     * Obtiene el siguiente ID disponible para una nueva orden de compra.
     * @return El siguiente ID (máximo ID actual + 1)
     */
    Long obtenerSiguienteId();

}
