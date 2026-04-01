package com.pazzioliweb.cajasmodule.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.pazzioliweb.cajasmodule.dtos.CajaDTO;
import com.pazzioliweb.cajasmodule.entity.Cajas;
import com.pazzioliweb.cajasmodule.service.CajasService;

/**
 * Servicio para la gestión de cajas.
 * Encapsula la lógica de negocio relacionada con la apertura,
 * cierre y consulta de cajas.
 */
@RestController
@RequestMapping("/api/cajas")
public class CajasController {
	private final CajasService cajaService;
	
	@Autowired
	public CajasController(CajasService cajasService) {
		this.cajaService = cajasService;
	}
	
	@GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<CajaDTO> cajasPage = cajaService.listar(page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", cajasPage.getContent());
        response.put("currentPage", cajasPage.getNumber());
        response.put("totalItems", cajasPage.getTotalElements());
        response.put("totalPages", cajasPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<Cajas> obtener(@PathVariable Integer id) {
        return cajaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cajas> crear(@RequestBody Cajas caja) {
    	Cajas guardado = cajaService.guardar(caja);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cajas> actualizar(@PathVariable Integer id, @RequestBody Cajas caja) {
    	return cajaService.buscarPorId(id)
                .map(actual -> {
                    caja.setCaja_id(id);
                    return ResponseEntity.ok(cajaService.guardar(caja));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (cajaService.buscarPorId(id).isPresent()) {
        	cajaService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
