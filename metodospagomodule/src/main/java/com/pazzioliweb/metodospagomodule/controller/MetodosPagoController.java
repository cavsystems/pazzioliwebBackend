package com.pazzioliweb.metodospagomodule.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.metodospagomodule.dtos.MetodoPagoDTO;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import com.pazzioliweb.metodospagomodule.service.MetodosPagoService;

@RestController
@RequestMapping("/api/metodos_pago")
public class MetodosPagoController {
	private final MetodosPagoService metodopagoService;
	
	public MetodosPagoController(MetodosPagoService metodopagoService) {
		this.metodopagoService = metodopagoService;
	}
	
	@GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<MetodoPagoDTO> metodosPagoPage = metodopagoService.listar(page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", metodosPagoPage.getContent());
        response.put("currentPage", metodosPagoPage.getNumber());
        response.put("totalItems", metodosPagoPage.getTotalElements());
        response.put("totalPages", metodosPagoPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> listarPorTipo(@PathVariable String tipo) {
        try {
            return ResponseEntity.ok(metodopagoService.listarActivosPorTipo(tipo));
        } catch (RuntimeException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

	@GetMapping("/{id}")
    public ResponseEntity<MetodosPago> obtener(@PathVariable Integer id) {
        return metodopagoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MetodosPago> crear(@RequestBody MetodosPago metodoPago) {
    	MetodosPago guardado = metodopagoService.guardar(metodoPago);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetodosPago> actualizar(@PathVariable Integer id, @RequestBody MetodosPago metodoPago) {
    	return metodopagoService.buscarPorId(id)
                .map(actual -> {
                	metodoPago.setMetodo_pago_id(id);
                    return ResponseEntity.ok(metodopagoService.guardar(metodoPago));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (metodopagoService.buscarPorId(id).isPresent()) {
        	metodopagoService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
