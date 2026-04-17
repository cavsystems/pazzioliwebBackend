package com.pazzioliweb.productosmodule.controller;

import com.pazzioliweb.productosmodule.dtos.OtroprecioDTO;
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
import com.pazzioliweb.productosmodule.dtos.PrecioCreateDTO;
import com.pazzioliweb.productosmodule.dtos.PrecioResponseDTO;
import com.pazzioliweb.productosmodule.dtos.PrecioUpdateDTO;
import com.pazzioliweb.productosmodule.service.PrecioServiceImpl;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/precios")
public class PreciosController {

    private final PrecioServiceImpl service;

    public PreciosController(PrecioServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/listar")
    public ResponseEntity<PaginationResponse<PrecioResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
                      @RequestParam(defaultValue = "precioId") String sortField,
                      @RequestParam(defaultValue = "") String descripprecio,

            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
    	
    	Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PrecioResponseDTO> resultado = service.listar(descripprecio,pageable);
    	
        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }
    
    
    @GetMapping("/listar/{id}")
    public ResponseEntity<PaginationResponse<PrecioResponseDTO>> listarprecioproduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") String id,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "precioId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
    	
    	Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PrecioResponseDTO> resultado = service.listar("",pageable);
    	
        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }


    @GetMapping("/{id}")
    public ResponseEntity<PrecioResponseDTO> obtener(@PathVariable Integer id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("encabezados")
    public ResponseEntity< List<OtroprecioDTO> > obtenerenbezados( @RequestParam String columnas) {
        List<String> lista = Arrays.asList(columnas.split(","));
        return   ResponseEntity.ok(service.obtenerenbezados(lista));

    }

    @PostMapping("crear-por-dto")
    public ResponseEntity<PrecioResponseDTO> crear(@RequestBody PrecioCreateDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrecioResponseDTO> actualizar(@PathVariable Integer id, @RequestBody PrecioUpdateDTO dto) {
        return service.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        boolean eliminado = service.eliminar(id);
        return eliminado ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
