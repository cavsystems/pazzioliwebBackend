package com.pazzioliweb.comprasmodule.service.impl;

import com.pazzioliweb.comprasmodule.dtos.CuentaPorPagarDTO;
import com.pazzioliweb.comprasmodule.entity.CuentaPorPagar;
import com.pazzioliweb.comprasmodule.repository.CuentaPorPagarRepository;
import com.pazzioliweb.comprasmodule.service.CuentaPorPagarService;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CuentaPorPagarServiceImpl implements CuentaPorPagarService {

    private final CuentaPorPagarRepository cuentaPorPagarRepository;
    private final TercerosRepository tercerosRepository;

    @Autowired
    public CuentaPorPagarServiceImpl(CuentaPorPagarRepository cuentaPorPagarRepository, TercerosRepository tercerosRepository) {
        this.cuentaPorPagarRepository = cuentaPorPagarRepository;
        this.tercerosRepository = tercerosRepository;
    }

    @Override
    @Transactional
    public CuentaPorPagar crear(CuentaPorPagarDTO cuentaPorPagarDTO) {
        CuentaPorPagar cuenta = new CuentaPorPagar();
        cuenta.setNit(cuentaPorPagarDTO.getNit());
        cuenta.setNombre(cuentaPorPagarDTO.getNombre());
        cuenta.setFechaVencimiento(cuentaPorPagarDTO.getFechaVencimiento());
        cuenta.setNumeroFactura(cuentaPorPagarDTO.getNumeroFactura());
        cuenta.setValorNeto(cuentaPorPagarDTO.getValorNeto());
        cuenta.setSaldo(cuentaPorPagarDTO.getValorNeto());
        cuenta.setEstado(cuentaPorPagarDTO.getEstado() != null ? cuentaPorPagarDTO.getEstado() : "PENDIENTE");
        if (cuentaPorPagarDTO.getRetefuente() != null) cuenta.setRetefuente(cuentaPorPagarDTO.getRetefuente());
        if (cuentaPorPagarDTO.getReteiva() != null) cuenta.setReteiva(cuentaPorPagarDTO.getReteiva());
        if (cuentaPorPagarDTO.getReteica() != null) cuenta.setReteica(cuentaPorPagarDTO.getReteica());

        if (cuentaPorPagarDTO.getProveedorId() != null) {
            Terceros proveedor = tercerosRepository.findById(cuentaPorPagarDTO.getProveedorId()).orElse(null);
            cuenta.setProveedor(proveedor);
        }

        return cuentaPorPagarRepository.save(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaPorPagarDTO> listarPendientes() {
        List<CuentaPorPagar> pendientes = cuentaPorPagarRepository.findAll().stream()
                .filter(c -> "PENDIENTE".equals(c.getEstado())
                        || "PARCIAL".equals(c.getEstado())
                        || "VENCIDA".equals(c.getEstado()))
                .collect(Collectors.toList());
        return pendientes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaPorPagarDTO> listarPorProveedor(Integer proveedorId) {
        // Solo CxP pagables: excluye órdenes en estado PENDIENTE (sin ingreso de mercancía)
        return cuentaPorPagarRepository
                .findPagablesByProveedor(proveedorId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void pagar(Long id) {
        // El pago de una CxP debe hacerse con un COMPROBANTE DE EGRESO (CE): reduce el saldo, mueve
        // caja/banco y genera el asiento (DR 2205 / CR bancos). Marcar aquí PAGADA sin reducir saldo
        // ni contabilizar dejaba el mayor 2205 sin bajar y sin salida de tesorería → mayor ≠ auxiliar.
        // Se deshabilita este atajo para no dejar pagos sin respaldo contable.
        throw new RuntimeException("El pago de una cuenta por pagar debe registrarse con un Comprobante de Egreso (CE), "
                + "no marcando PAGADA directamente. Use el módulo de Egresos.");
    }

    @Override
    @Transactional
    public void eliminarPorNumeroFactura(String numeroFactura) {
        List<CuentaPorPagar> cuentas = cuentaPorPagarRepository.findByNumeroFactura(numeroFactura);
        for (CuentaPorPagar cuenta : cuentas) {
            cuentaPorPagarRepository.delete(cuenta);
        }
    }

    /**
     * Anula la CxP en lugar de borrarla — preserva trazabilidad para auditoría.
     * Usar este método cuando se anula una orden de compra: la CxP queda con
     * saldo 0 y estado ANULADA, manteniendo el histórico para reportes DIAN.
     */
    @Override
    @Transactional
    public void anularPorNumeroFactura(String numeroFactura, String motivo) {
        List<CuentaPorPagar> cuentas = cuentaPorPagarRepository.findByNumeroFactura(numeroFactura);
        for (CuentaPorPagar cuenta : cuentas) {
            if (!"ANULADA".equals(cuenta.getEstado())) {
                cuenta.setEstado("ANULADA");
                cuenta.setSaldo(java.math.BigDecimal.ZERO);
                cuentaPorPagarRepository.save(cuenta);
            }
        }
    }

    @Override
    @Transactional
    public void actualizarNumeroFacturaProveedor(String numeroFactura, String numeroFacturaProveedor) {
        List<CuentaPorPagar> cuentas = cuentaPorPagarRepository.findByNumeroFactura(numeroFactura);
        for (CuentaPorPagar cuenta : cuentas) {
            cuenta.setNumeroFacturaProveedor(numeroFacturaProveedor);
            cuentaPorPagarRepository.save(cuenta);
        }
    }

    private CuentaPorPagarDTO toDTO(CuentaPorPagar cuenta) {
        CuentaPorPagarDTO dto = new CuentaPorPagarDTO();
        dto.setId(cuenta.getId());
        dto.setNit(cuenta.getNit());
        dto.setNombre(cuenta.getNombre());
        dto.setFechaVencimiento(cuenta.getFechaVencimiento());
        dto.setNumeroFactura(cuenta.getNumeroFactura());
        dto.setValorNeto(cuenta.getValorNeto());
        dto.setSaldo(cuenta.getSaldo());
        dto.setEstado(cuenta.getEstado());
        dto.setFechaCreacion(cuenta.getFechaCreacion());
        dto.setNumeroFacturaProveedor(cuenta.getNumeroFacturaProveedor());
        if (cuenta.getProveedor() != null) {
            dto.setProveedorId(cuenta.getProveedor().getTerceroId());
        }
        dto.setRetefuente(cuenta.getRetefuente() != null ? cuenta.getRetefuente() : java.math.BigDecimal.ZERO);
        dto.setReteiva(cuenta.getReteiva() != null ? cuenta.getReteiva() : java.math.BigDecimal.ZERO);
        dto.setReteica(cuenta.getReteica() != null ? cuenta.getReteica() : java.math.BigDecimal.ZERO);
        return dto;
    }
}
