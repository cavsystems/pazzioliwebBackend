package com.pazzioliweb.productosmodule.controller;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

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
import com.pazzioliweb.productosmodule.entity.Lineas;
import com.pazzioliweb.productosmodule.service.LineasServiceImpl;

@RestController
@RequestMapping("/api/lineas")
public class LineasController {
	private final LineasServiceImpl service;
	
	public LineasController(LineasServiceImpl service) {
		this.service = service;
	}
	
	@PostMapping
	public ResponseEntity<Lineas> crear(@RequestBody Lineas linea) {
        return ResponseEntity.ok(service.crear(linea));
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<Lineas> actualizar(
            @PathVariable Integer id,
            @RequestBody Lineas linea
    ) {
        return ResponseEntity.ok(service.actualizar(id, linea));
    }
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
	
	@GetMapping("/id/{nombre}")
    public ResponseEntity<Integer> getIdByNombre(@PathVariable String nombre) {
        return service.buscarPorNombre(nombre)
                .map(l -> ResponseEntity.ok(l.getId()))
                .orElse(ResponseEntity.notFound().build());
    }
	
	@GetMapping("/listar")
    public ResponseEntity<PaginationResponse<Lineas>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "") String descripcion,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Lineas> resultado = service.listar(descripcion,pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }
}
