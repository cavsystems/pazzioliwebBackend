package com.pazzioliweb.ventasmodule.service;

import com.pazzioliweb.ventasmodule.dtos.CuentaPorCobrarDTO;
import com.pazzioliweb.ventasmodule.entity.CuentaPorCobrar;
import com.pazzioliweb.ventasmodule.entity.Venta;
import com.pazzioliweb.tercerosmodule.entity.Terceros;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaPorCobrarService {

    /**
     * Crea una cuenta por cobrar a partir de una venta con método de pago Crédito.
     */
    CuentaPorCobrar crearDesdeVenta(Venta venta, Terceros cliente, BigDecimal montoCredito, int plazoDias);

    /**
     * Lista todas las cuentas por cobrar pendientes (PENDIENTE o PARCIAL).
     */
    List<CuentaPorCobrarDTO> listarPendientes();

    /**
     * Lista cuentas por cobrar de un cliente específico.
     */
    List<CuentaPorCobrarDTO> listarPorCliente(Integer clienteId);

    /**
     * Registra un abono parcial o total a una cuenta por cobrar.
     */
    CuentaPorCobrarDTO abonar(Long cuentaId, BigDecimal montoAbono);

    /**
     * Marca una cuenta como pagada completamente.
     */
    void pagar(Long cuentaId);
}

