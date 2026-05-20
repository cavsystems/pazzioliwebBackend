package com.pazzioliweb.comprasmodule.controller;

import com.pazzioliweb.comprasmodule.dtos.ConfiguracionComprasDTO;
import com.pazzioliweb.comprasmodule.service.ConfiguracionComprasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/compras/configuracion")
public class ConfiguracionComprasController {

    private final ConfiguracionComprasService service;

    public ConfiguracionComprasController(ConfiguracionComprasService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ConfiguracionComprasDTO> obtener() {
        return ResponseEntity.ok(service.obtener());
    }

    @PutMapping("/cajero-default")
    public ResponseEntity<ConfiguracionComprasDTO> actualizarCajeroDefault(@RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(service.actualizarCajeroDefault(body.get("cajeroId")));
    }
}
