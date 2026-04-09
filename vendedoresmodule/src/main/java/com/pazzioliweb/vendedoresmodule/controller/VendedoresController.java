package com.pazzioliweb.vendedoresmodule.controller;

import java.util.HashMap;
import java.util.List;
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

import com.pazzioliweb.vendedoresmodule.dtos.VendedorDTO;
import com.pazzioliweb.vendedoresmodule.entity.Vendedores;
import com.pazzioliweb.vendedoresmodule.service.VendedoresService;

@RestController
@RequestMapping("/api/vendedores")
public class VendedoresController {
	private final VendedoresService vendedorService;
	
	public VendedoresController(VendedoresService vendedorService) {
		this.vendedorService = vendedorService;
	}
	
	@GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "vendedor_id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<VendedorDTO> vendedoresPage = vendedorService.listar(page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", vendedoresPage.getContent());
        response.put("currentPage", vendedoresPage.getNumber());
        response.put("totalItems", vendedoresPage.getTotalElements());
        response.put("totalPages", vendedoresPage.getTotalPages());

        return ResponseEntity.ok(response);
    }


    /*
    controlador que me permite buscor vendedores pertenecientes a una cede
     */

    @GetMapping("/bodega/{bodegaId}")
    public ResponseEntity<List<Vendedores>> buscarVendedoresPorBodegaId(@PathVariable Integer bodegaId) {
        List<Vendedores> vendedores = vendedorService.findByBodegaId(bodegaId);
        return ResponseEntity.ok(vendedores);
    }
	@GetMapping("/{id}")
    public ResponseEntity<Vendedores> obtener(@PathVariable Integer id) {
        return vendedorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Vendedores> crear(@RequestBody Vendedores vendedor) {
    	Vendedores guardado = vendedorService.guardar(vendedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vendedores> actualizar(@PathVariable Integer id, @RequestBody Vendedores vendedor) {
    	return vendedorService.buscarPorId(id)
                .map(actual -> {
                	vendedor.setVendedor_id(id);
                    return ResponseEntity.ok(vendedorService.guardar(vendedor));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (vendedorService.buscarPorId(id).isPresent()) {
        	vendedorService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
