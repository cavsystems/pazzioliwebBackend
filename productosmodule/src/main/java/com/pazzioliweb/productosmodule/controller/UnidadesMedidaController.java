package com.pazzioliweb.productosmodule.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.pazzioliweb.commonbacken.dtos.response.ApiResponse;
import com.pazzioliweb.commonbacken.dtos.response.PaginationResponse;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaCreateDTO;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaResponseDTO;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaUpdateDTO;
import com.pazzioliweb.productosmodule.entity.Lineas;
import com.pazzioliweb.productosmodule.entity.UnidadesMedida;
import com.pazzioliweb.productosmodule.service.UnidadesMedidaService;

@RestController
@RequestMapping("/api/unidadesMedida")
public class UnidadesMedidaController {

	private UnidadesMedidaService unidadMedidaService;
	
	public UnidadesMedidaController(UnidadesMedidaService unidadMedidaService) {
		this.unidadMedidaService = unidadMedidaService;
	}
	
	@GetMapping("/listar")
	public ResponseEntity<PaginationResponse<UnidadMedidaResponseDTO>> listar(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "20") int size,
	        @RequestParam(defaultValue = "unidadMedidaId") String sortField,
	        @RequestParam(defaultValue = "") String descripcion,
	        @RequestParam(defaultValue = "asc") String sortDirection) {

	    Sort sort = sortDirection.equalsIgnoreCase("asc")
	            ? Sort.by(sortField).ascending()
	            : Sort.by(sortField).descending();

	    Pageable pageable = PageRequest.of(page, size, sort);

	    // 🔹 Obtener la página desde el service
	    Page<UnidadMedidaResponseDTO> resultado = unidadMedidaService.listar(descripcion, pageable );

	    // 🔹 Crear la respuesta estándar
	    PaginationResponse<UnidadMedidaResponseDTO> response =
	            new PaginationResponse<>(
	                    resultado.getContent(),
	                    resultado.getNumber(),
	                    resultado.getTotalElements(),
	                    resultado.getTotalPages()
	            );

	    return ResponseEntity.ok(response);
	}
	
	@PostMapping("/crear-por-dto")
	public ResponseEntity<List<UnidadMedidaResponseDTO>> guardar(@RequestBody List<UnidadMedidaCreateDTO> dtos) {
		List<UnidadMedidaResponseDTO> respuestas = unidadMedidaService.crear(dtos);
		return ResponseEntity.ok(respuestas);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<UnidadMedidaResponseDTO>> buscarPorId(@PathVariable Integer id) {
		UnidadMedidaResponseDTO dto = unidadMedidaService.obtenerPorId(id);
	    return ResponseEntity.ok(ApiResponse.success("Unidad de medida encontrada", dto));
    }
	
	@DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
		unidadMedidaService.eliminar(id);
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<UnidadMedidaResponseDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody	UnidadMedidaUpdateDTO medida
    ) {
		
	
        return ResponseEntity.ok(unidadMedidaService.actualizar(id, medida));
    }
    
    @GetMapping("/id/{codigo}")
    public ResponseEntity<Integer> getIdByCodigo(@PathVariable String codigo) {
        return unidadMedidaService.buscarPorCodigo(codigo)
                .map(um -> ResponseEntity.ok(um.getUnidadMedidaId()))
                .orElse(ResponseEntity.notFound().build());
    }
	
}
