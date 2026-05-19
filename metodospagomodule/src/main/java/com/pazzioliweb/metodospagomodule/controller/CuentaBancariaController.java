package com.pazzioliweb.metodospagomodule.controller;

import com.pazzioliweb.metodospagomodule.dtos.CuentaBancariaDTO;
import com.pazzioliweb.metodospagomodule.entity.CuentaBancaria;
import com.pazzioliweb.metodospagomodule.service.CuentaBancariaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cuentas-bancarias")
@CrossOrigin(origins = "*")
public class CuentaBancariaController {

    private final CuentaBancariaService service;

    public CuentaBancariaController(CuentaBancariaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CuentaBancariaDTO>> listar(
            @RequestParam(required = false, defaultValue = "false") boolean soloActivas) {
        return ResponseEntity.ok(soloActivas ? service.listarActivas() : service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaBancariaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<CuentaBancariaDTO> crear(@RequestBody CuentaBancariaDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaBancariaDTO> actualizar(@PathVariable Long id, @RequestBody CuentaBancariaDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Lista los tipos de cuenta disponibles para el selector del frontend. */
    @GetMapping("/tipos")
    public ResponseEntity<List<Map<String, String>>> tipos() {
        return ResponseEntity.ok(
            Arrays.stream(CuentaBancaria.TipoCuenta.values())
                .map(t -> Map.of("codigo", t.name(), "descripcion", t.name()))
                .collect(Collectors.toList())
        );
    }
}
