package com.pazzioliweb.comprobantesmodule.controller;

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

import com.pazzioli.comprobantesmodule.dtos.ComprobanteDTO;
import com.pazzioliweb.comprobantesmodule.entity.Comprobantes;
import com.pazzioliweb.comprobantesmodule.service.ComprobantesService;

@RestController
@RequestMapping("/api/comprobantes")
public class ComprobantesController {
	private final ComprobantesService comprobanteService;
	
	@Autowired
	public ComprobantesController(ComprobantesService comprobanteService) {
		this.comprobanteService = comprobanteService;
	}
	
	@GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<ComprobanteDTO> tercerosPage = comprobanteService.listar(page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", tercerosPage.getContent());
        response.put("currentPage", tercerosPage.getNumber());
        response.put("totalItems", tercerosPage.getTotalElements());
        response.put("totalPages", tercerosPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<Comprobantes> obtener(@PathVariable Integer id) {
        return comprobanteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Comprobantes> crear(@RequestBody Comprobantes tercero) {
    	Comprobantes guardado = comprobanteService.guardar(tercero);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comprobantes> actualizar(@PathVariable Integer id, @RequestBody Comprobantes comprobante) {
    	return comprobanteService.buscarPorId(id)
                .map(actual -> {
                    comprobante.setComprobante_id(id);
                    return ResponseEntity.ok(comprobanteService.guardar(comprobante));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (comprobanteService.buscarPorId(id).isPresent()) {
        	comprobanteService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
