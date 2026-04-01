package com.pazzioliweb.comprasmodule.controller;

import com.pazzioliweb.comprasmodule.dtos.LegalizacionDTO;
import com.pazzioliweb.comprasmodule.dtos.LegalizacionRequestDTO;
import com.pazzioliweb.comprasmodule.dtos.OrdenCompraDTO;
import com.pazzioliweb.comprasmodule.service.LegalizacionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compras/legalizacion")
public class LegalizacionController {

    private final LegalizacionService legalizacionService;

    @Autowired
    public LegalizacionController(LegalizacionService legalizacionService) {
        this.legalizacionService = legalizacionService;
    }

    @PostMapping
    public ResponseEntity<Void> legalizarCompra(@RequestBody LegalizacionRequestDTO request) {
        legalizacionService.legalizarCompra(request);
        return ResponseEntity.ok().build();
    }
    @GetMapping("provedor/by-proveedor")
    public ResponseEntity<List<LegalizacionDTO>> obtenerLegalizacionesPorProveedor(@RequestParam Long proveedorId) {
        return ResponseEntity.ok(legalizacionService.obtenerLegalizacionesPorProveedor(proveedorId));
    }
    @GetMapping("/siguiente-id")
    public ResponseEntity<Long> obtenerSiguienteId() {
        return ResponseEntity.ok(legalizacionService.obtenerSiguienteId());
    }

    @GetMapping("/allinfo")
    public ResponseEntity<List<LegalizacionDTO>> obtenerTodasLasOrdenes() {
        return ResponseEntity.ok(legalizacionService.obtenerTodasLasLegalizaciones());
    }


}
