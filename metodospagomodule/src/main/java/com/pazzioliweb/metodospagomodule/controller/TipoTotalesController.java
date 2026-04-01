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

import com.pazzioliweb.metodospagomodule.dtos.TipoTotalDTO;
import com.pazzioliweb.metodospagomodule.entity.TipoTotales;
import com.pazzioliweb.metodospagomodule.service.TipoTotalesService;

@RestController
@RequestMapping("/api/tipo_totales")
public class TipoTotalesController {
	private final TipoTotalesService tipoTotalService;
	
	public TipoTotalesController(TipoTotalesService tipoTotalService) {
		this.tipoTotalService = tipoTotalService;
	}
	
	@GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<TipoTotalDTO> tipoTotalesPage = tipoTotalService.listar(page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", tipoTotalesPage.getContent());
        response.put("currentPage", tipoTotalesPage.getNumber());
        response.put("totalItems", tipoTotalesPage.getTotalElements());
        response.put("totalPages", tipoTotalesPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<TipoTotales> obtener(@PathVariable Integer id) {
        return tipoTotalService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoTotales> crear(@RequestBody TipoTotales tipoTotal) {
    	TipoTotales guardado = tipoTotalService.guardar(tipoTotal);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoTotales> actualizar(@PathVariable Integer id, @RequestBody TipoTotales tipoTotal) {
    	return tipoTotalService.buscarPorId(id)
                .map(actual -> {
                	tipoTotal.setTipo_total_id(id);
                    return ResponseEntity.ok(tipoTotalService.guardar(tipoTotal));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (tipoTotalService.buscarPorId(id).isPresent()) {
        	tipoTotalService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
