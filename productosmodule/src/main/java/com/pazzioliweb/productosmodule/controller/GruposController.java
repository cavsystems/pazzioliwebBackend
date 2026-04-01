package com.pazzioliweb.productosmodule.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.pazzioliweb.commonbacken.dtos.response.PaginationResponse;
import com.pazzioliweb.productosmodule.entity.Grupos;
import com.pazzioliweb.productosmodule.service.GruposServiceImpl;

@RestController
@RequestMapping("/api/grupos")
public class GruposController {
private final GruposServiceImpl service;
	
	public GruposController(GruposServiceImpl service) {
		this.service = service;
	}
	
	@PostMapping
	public ResponseEntity<Grupos> crear(@RequestBody Grupos grupo) {
        return ResponseEntity.ok(service.crear(grupo));
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<Grupos> actualizar(
            @PathVariable Integer id,
            @RequestBody Grupos grupo
    ) {
        return ResponseEntity.ok(service.actualizar(id, grupo));
    }
	
	@GetMapping("/id/{nombre}")
    public ResponseEntity<Integer> getIdByNombre(@PathVariable String nombre) {
        return service.buscarPorNombre(nombre)
                .map(g -> ResponseEntity.ok(g.getId()))
                .orElse(ResponseEntity.notFound().build());
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<Grupos> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

	@GetMapping("/listar")
    public ResponseEntity<PaginationResponse<Grupos>> listar(
    		@RequestParam(defaultValue = "") String descripcion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Grupos> resultado = service.listar(descripcion,pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }
}
