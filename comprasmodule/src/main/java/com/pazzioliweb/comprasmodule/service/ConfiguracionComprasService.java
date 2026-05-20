package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.cajerosmodule.repositori.CajeroRepository;
import com.pazzioliweb.comprasmodule.dtos.ConfiguracionComprasDTO;
import com.pazzioliweb.comprasmodule.entity.ConfiguracionCompras;
import com.pazzioliweb.comprasmodule.repository.ConfiguracionComprasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Maneja la configuración global del módulo de compras (single-row).
 * Hoy expone únicamente el cajero default usado como fallback al asignar
 * comprobantes de compra cuando el usuario que realiza la operación no
 * es cajero o su cajero no tiene comprobantes CC/CR configurados.
 */
@Service
public class ConfiguracionComprasService {

    private final ConfiguracionComprasRepository repo;
    private final CajeroRepository cajeroRepository;

    public ConfiguracionComprasService(ConfiguracionComprasRepository repo,
                                        CajeroRepository cajeroRepository) {
        this.repo = repo;
        this.cajeroRepository = cajeroRepository;
    }

    @Transactional
    public ConfiguracionComprasDTO obtener() {
        ConfiguracionCompras cfg = repo.findById(1).orElseGet(() -> {
            ConfiguracionCompras nuevo = new ConfiguracionCompras();
            nuevo.setId(1);
            nuevo.setFechaActualizacion(LocalDateTime.now());
            return repo.save(nuevo);
        });
        return toDto(cfg);
    }

    @Transactional
    public ConfiguracionComprasDTO actualizarCajeroDefault(Integer cajeroId) {
        if (cajeroId != null) {
            cajeroRepository.findById(cajeroId).orElseThrow(() ->
                new IllegalArgumentException("Cajero no encontrado: " + cajeroId));
        }
        ConfiguracionCompras cfg = repo.findById(1).orElseGet(() -> {
            ConfiguracionCompras nuevo = new ConfiguracionCompras();
            nuevo.setId(1);
            return nuevo;
        });
        cfg.setCajeroDefaultId(cajeroId);
        cfg.setFechaActualizacion(LocalDateTime.now());
        return toDto(repo.save(cfg));
    }

    /** Devuelve el cajero default configurado, o null si no hay. */
    @Transactional(readOnly = true)
    public Integer obtenerCajeroDefaultId() {
        return repo.findById(1).map(ConfiguracionCompras::getCajeroDefaultId).orElse(null);
    }

    private ConfiguracionComprasDTO toDto(ConfiguracionCompras c) {
        ConfiguracionComprasDTO dto = new ConfiguracionComprasDTO();
        dto.setCajeroDefaultId(c.getCajeroDefaultId());
        dto.setFechaActualizacion(c.getFechaActualizacion());
        if (c.getCajeroDefaultId() != null) {
            Cajero cajero = cajeroRepository.findById(c.getCajeroDefaultId()).orElse(null);
            if (cajero != null) dto.setCajeroDefaultNombre(cajero.getNombre());
        }
        return dto;
    }
}
