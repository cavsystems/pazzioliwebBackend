package com.pazzioliweb.comprasmodule.controller;

import com.pazzioliweb.comprasmodule.service.OrdenCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/compras/metricas")
public class ComprasMetricsController {

    private final OrdenCompraService ordenCompraService;

    @Autowired
    public ComprasMetricsController(OrdenCompraService ordenCompraService) {
        this.ordenCompraService = ordenCompraService;
    }

    /**
     * Obtiene el número total de órdenes de compra registradas en el sistema.
     * 
     * @return Un mapa con el conteo total de órdenes.
     */
    @GetMapping("/total-ordenes")
    public ResponseEntity<Map<String, Long>> obtenerTotalOrdenes() {
        return ResponseEntity.ok(ordenCompraService.contarTotalOrdenes());
    }
}

// End of file: ComprasMetricsController.java
