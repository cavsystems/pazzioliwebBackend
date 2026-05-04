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
                .filter(c -> "PENDIENTE".equals(c.getEstado()))
                .collect(Collectors.toList());
        return pendientes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaPorPagarDTO> listarPorProveedor(Integer proveedorId) {
        return cuentaPorPagarRepository
                .findByProveedor_TerceroIdAndEstadoIn(proveedorId, List.of("PENDIENTE", "PARCIAL"))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void pagar(Long id) {
        CuentaPorPagar cuenta = cuentaPorPagarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta por pagar no encontrada"));
        cuenta.setEstado("PAGADA");
        cuentaPorPagarRepository.save(cuenta);
    }

    @Override
    @Transactional
    public void eliminarPorNumeroFactura(String numeroFactura) {
        List<CuentaPorPagar> cuentas = cuentaPorPagarRepository.findByNumeroFactura(numeroFactura);
        for (CuentaPorPagar cuenta : cuentas) {
            cuentaPorPagarRepository.delete(cuenta);
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
        return dto;
    }
}
