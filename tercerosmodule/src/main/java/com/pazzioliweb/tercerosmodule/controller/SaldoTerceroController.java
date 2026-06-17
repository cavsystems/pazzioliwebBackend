package com.pazzioliweb.tercerosmodule.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.tercerosmodule.dtos.SaldoTerceroDTO;
import com.pazzioliweb.tercerosmodule.service.SaldoTerceroService;

@Component
@RestController
@RequestMapping("/api/terceros")
public class SaldoTerceroController {

    private final SaldoTerceroService saldoTerceroService;

    @Autowired
    public SaldoTerceroController(SaldoTerceroService saldoTerceroService) {
        this.saldoTerceroService = saldoTerceroService;
    }

    @GetMapping("/saldos")
    public ResponseEntity<List<SaldoTerceroDTO>> consultarSaldosPendientes() {
        return ResponseEntity.ok(saldoTerceroService.consultarSaldosPendientes());
    }
}
