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
import com.pazzioliweb.productosmodule.entity.TipoProducto;
import com.pazzioliweb.productosmodule.service.TipoProductoServiceImpl;

@RestController
@RequestMapping("/api/tipo-producto")
public class TipoProductoController {
	private final TipoProductoServiceImpl service;
	
	public TipoProductoController(TipoProductoServiceImpl service) {
		this.service = service;
	}
	
	@PostMapping("crear")
	public ResponseEntity<TipoProducto> crear(@RequestBody TipoProducto tipoProducto){
		return ResponseEntity.ok(service.crear(tipoProducto));
	}
	
	@PutMapping("/actualizar/{id}")
	public ResponseEntity<TipoProducto> actualizar(
			@PathVariable Integer id,
			@RequestBody TipoProducto tipoProducto
	){
		return ResponseEntity.ok(service.actualizar(id, tipoProducto));
	}
	
	@DeleteMapping("/eliminar/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id){
		service.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<TipoProducto> obtener(@PathVariable Integer id){
		return ResponseEntity.ok(service.buscarPorId(id));
	}
	
	@GetMapping("/id/{nombre}")
    public ResponseEntity<Integer> getIdByNombre(@PathVariable String nombre) {
        return service.buscarPorNombre(nombre)
                .map(tp -> ResponseEntity.ok(tp.getTipoProductoId()))
                .orElse(ResponseEntity.notFound().build());
    }
	
	@GetMapping("/listar")
	public ResponseEntity<PaginationResponse<TipoProducto>> listar(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "tipoProductoId") String sortField,
			@RequestParam(defaultValue = "asc") String sortDirection
	){
		Sort sort = sortDirection.equalsIgnoreCase("asc")
				? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(page, size, sort);
		
		Page<TipoProducto> resultado = service.listar(pageable);
		
		return ResponseEntity.ok(PaginationResponse.of(resultado));
	}
}
