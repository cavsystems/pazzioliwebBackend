package com.pazzioliweb.tercerosmodule.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.tercerosmodule.dtos.SaldoTerceroDTO;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;

@Service
public class SaldoTerceroService {

    private final TercerosRepository tercerosRepository;

    @Autowired
    public SaldoTerceroService(TercerosRepository tercerosRepository) {
        this.tercerosRepository = tercerosRepository;
    }

    @Transactional(readOnly = true)
    public List<SaldoTerceroDTO> consultarSaldosPendientes() {
        return tercerosRepository.consultarSaldosPendientesPorTercero();
    }
}
