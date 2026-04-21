package com.pazzioliweb.ventasmodule.controller;

import com.pazzioliweb.ventasmodule.dtos.CuentaPorCobrarDTO;
import com.pazzioliweb.ventasmodule.repository.CuentaPorCobrarRepository;
import com.pazzioliweb.ventasmodule.service.CuentaPorCobrarService;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas-por-cobrar")
public class CuentaPorCobrarController {

    @Autowired
    private CuentaPorCobrarService cxcService;

    @Autowired
    private CuentaPorCobrarRepository cxcRepository;

    @Autowired
    private TercerosRepository tercerosRepository;

    /** Lista todas las CxC pendientes y parciales */
    @GetMapping("/pendientes")
    public ResponseEntity<List<CuentaPorCobrarDTO>> listarPendientes() {
        return ResponseEntity.ok(cxcService.listarPendientes());
    }

    /** Lista CxC de un cliente específico con cupo disponible */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Map<String, Object>> listarPorCliente(@PathVariable Integer clienteId) {
        List<CuentaPorCobrarDTO> cuentas = cxcService.listarPorCliente(clienteId);
        BigDecimal saldoPendiente = cxcRepository.sumSaldoPendienteByClienteId(clienteId);
        
        Terceros tercero = tercerosRepository.findById(clienteId).orElse(null);
        BigDecimal cupoTotal = tercero != null ? BigDecimal.valueOf(tercero.getCupo()) : BigDecimal.ZERO;
        BigDecimal cupoDisponible = cupoTotal.subtract(saldoPendiente);

        Map<String, Object> response = new HashMap<>();
        response.put("cuentas", cuentas);
        response.put("cupoTotal", cupoTotal);
        response.put("saldoPendiente", saldoPendiente);
        response.put("cupoDisponible", cupoDisponible.max(BigDecimal.ZERO));
        
        return ResponseEntity.ok(response);
    }

    /** Registra un abono parcial o total. Body: { "monto": 50000 } */
    @PostMapping("/{cuentaId}/abonar")
    public ResponseEntity<?> abonar(@PathVariable Long cuentaId, @RequestBody Map<String, BigDecimal> body) {
        try {
            BigDecimal monto = body.getOrDefault("monto", BigDecimal.ZERO);
            CuentaPorCobrarDTO resultado = cxcService.abonar(cuentaId, monto);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Marca la cuenta como pagada completamente */
    @PostMapping("/{cuentaId}/pagar")
    public ResponseEntity<?> pagar(@PathVariable Long cuentaId) {
        try {
            cxcService.pagar(cuentaId);
            return ResponseEntity.ok(Map.of("mensaje", "Cuenta pagada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

