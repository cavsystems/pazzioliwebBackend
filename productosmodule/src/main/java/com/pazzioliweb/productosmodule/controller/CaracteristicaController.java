package com.pazzioliweb.productosmodule.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.pazzioliweb.commonbacken.dtos.response.ApiResponse;
import com.pazzioliweb.commonbacken.dtos.response.PaginationResponse;
import com.pazzioliweb.productosmodule.dtos.CaracteristicaDTO;
import com.pazzioliweb.productosmodule.dtos.CaracteristicaDTO_basico;
import com.pazzioliweb.productosmodule.entity.Caracteristica;
import com.pazzioliweb.productosmodule.repositori.CaracteristicaRepository;
import com.pazzioliweb.productosmodule.service.CaracteristicaService;

@RestController
@RequestMapping("/api/caracteristicas")
public class CaracteristicaController {
	 @Autowired
	    private CaracteristicaRepository repoca;
	private final CaracteristicaService service;

    public CaracteristicaController(CaracteristicaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Caracteristica>> crear(@RequestBody Caracteristica c) {
    	ApiResponse<Caracteristica> respuesta = service.crear(c); 
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Caracteristica>> actualizar(
            @PathVariable Long id,
            @RequestBody Caracteristica c
    ) {
    	ApiResponse<Caracteristica> respuesta = service.actualizar(id,c); 
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Caracteristica> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
    
    @GetMapping("/listar/tipocaracte")
    public ResponseEntity<PaginationResponse<CaracteristicaDTO>> obtenercaractritica(   @RequestParam(name = "ca") String ca,
            @RequestParam(name = "tipo") String tipo, @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "size",defaultValue = "10") int size,
            @RequestParam(name = "sortField",defaultValue = "nombre") String sortField,
            @RequestParam(name = "sortDirection",defaultValue = "asc") String sortDirection) {
    	  Sort sort = sortDirection.equalsIgnoreCase("asc")
                  ? Sort.by(sortField).ascending()
                  : Sort.by(sortField).descending();

          Pageable pageable = PageRequest.of(page, size, sort);
          Page<CaracteristicaDTO> resultado = service.buscarPornombretipo( ca,
        	     tipo,pageable);
        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }

    @GetMapping("/listar")
    public ResponseEntity<PaginationResponse<CaracteristicaDTO_basico>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "caracteristicaId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CaracteristicaDTO_basico> resultado = service.listar(pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }

    
    
    
 
    
    @GetMapping("/tipo/{tipoId}")
    public ResponseEntity<PaginationResponse<CaracteristicaDTO_basico>> listarPorTipo(
            @PathVariable Long tipoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "caracteristicaId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CaracteristicaDTO_basico> resultado = service.listarPorTipo(tipoId, pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }
    
    
    @PostMapping("/buscarIds")
    public ResponseEntity<List<Long>> obtenerIds(@RequestBody List<String> valores) {
        return  ResponseEntity.ok( repoca.findByNombreIn(valores)       // Busca por lista
                .stream()
                .map(Caracteristica::getCaracteristicaId)         // Extrae IDs
                .toList());
    }
   /* @GetMapping("/listar-detalle")
    public ResponseEntity<PaginationResponse<CaracteristicaDTO>> listarCaracteristicasDetalle(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "caracteristicaId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CaracteristicaDTO> resultado = service.traerCaracteristicasDetale(pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }*/
}
