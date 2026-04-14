package com.pazzioliweb.ventasmodule.service.impl;

import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.ventasmodule.dtos.CuentaPorCobrarDTO;
import com.pazzioliweb.ventasmodule.entity.CuentaPorCobrar;
import com.pazzioliweb.ventasmodule.entity.Venta;
import com.pazzioliweb.ventasmodule.repository.CuentaPorCobrarRepository;
import com.pazzioliweb.ventasmodule.service.CuentaPorCobrarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CuentaPorCobrarServiceImpl implements CuentaPorCobrarService {

    private final CuentaPorCobrarRepository cxcRepository;

    @Autowired
    public CuentaPorCobrarServiceImpl(CuentaPorCobrarRepository cxcRepository) {
        this.cxcRepository = cxcRepository;
    }

    @Override
    @Transactional
    public CuentaPorCobrar crearDesdeVenta(Venta venta, Terceros cliente, BigDecimal montoCredito, int plazoDias) {
        CuentaPorCobrar cxc = new CuentaPorCobrar();
        cxc.setCliente(cliente);
        cxc.setNit(cliente.getIdentificacion() != null ? cliente.getIdentificacion() : "");
        cxc.setNombre(cliente.getNombre1() != null ? cliente.getNombre1() : "");
        cxc.setNumeroVenta(venta.getNumeroVenta());
        cxc.setVenta(venta);
        cxc.setValorNeto(montoCredito);
        cxc.setSaldo(montoCredito);
        cxc.setFechaEmision(LocalDate.now());
        cxc.setFechaVencimiento(LocalDate.now().plusDays(plazoDias));
        cxc.setPlazoDias(plazoDias);
        cxc.setEstado("PENDIENTE");

        CuentaPorCobrar saved = cxcRepository.save(cxc);
        System.out.println("✅ CxC creada — venta: " + venta.getNumeroVenta()
                + " cliente: " + cxc.getNombre()
                + " monto: " + montoCredito
                + " plazo: " + plazoDias + " días"
                + " vence: " + cxc.getFechaVencimiento());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaPorCobrarDTO> listarPendientes() {
        return cxcRepository.findByEstadoIn(List.of("PENDIENTE", "PARCIAL"))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaPorCobrarDTO> listarPorCliente(Integer clienteId) {
        return cxcRepository.findByCliente_TerceroId(clienteId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CuentaPorCobrarDTO abonar(Long cuentaId, BigDecimal montoAbono) {
        CuentaPorCobrar cxc = cxcRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta por cobrar no encontrada: " + cuentaId));

        BigDecimal nuevoSaldo = cxc.getSaldo().subtract(montoAbono);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) <= 0) {
            cxc.setSaldo(BigDecimal.ZERO);
            cxc.setEstado("PAGADA");
        } else {
            cxc.setSaldo(nuevoSaldo);
            cxc.setEstado("PARCIAL");
        }
        return toDTO(cxcRepository.save(cxc));
    }

    @Override
    @Transactional
    public void pagar(Long cuentaId) {
        CuentaPorCobrar cxc = cxcRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta por cobrar no encontrada: " + cuentaId));
        cxc.setSaldo(BigDecimal.ZERO);
        cxc.setEstado("PAGADA");
        cxcRepository.save(cxc);
    }

    private CuentaPorCobrarDTO toDTO(CuentaPorCobrar cxc) {
        CuentaPorCobrarDTO dto = new CuentaPorCobrarDTO();
        dto.setId(cxc.getId());
        dto.setClienteId(cxc.getCliente() != null ? cxc.getCliente().getTerceroId() : null);
        dto.setNit(cxc.getNit());
        dto.setNombre(cxc.getNombre());
        dto.setNumeroVenta(cxc.getNumeroVenta());
        dto.setVentaId(cxc.getVenta() != null ? cxc.getVenta().getId() : null);
        dto.setValorNeto(cxc.getValorNeto());
        dto.setSaldo(cxc.getSaldo());
        dto.setFechaEmision(cxc.getFechaEmision());
        dto.setFechaVencimiento(cxc.getFechaVencimiento());
        dto.setPlazoDias(cxc.getPlazoDias());
        dto.setEstado(cxc.getEstado());
        dto.setFechaCreacion(cxc.getFechaCreacion());
        return dto;
    }
}

