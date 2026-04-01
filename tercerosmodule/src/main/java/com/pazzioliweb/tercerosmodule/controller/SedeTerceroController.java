package com.pazzioliweb.tercerosmodule.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.tercerosmodule.dtos.SedeTerceroDTO;
import com.pazzioliweb.tercerosmodule.dtos.SedeTerceroDTOImpl;
import com.pazzioliweb.tercerosmodule.service.SedeTerceroService;
import com.pazzioliweb.tercerosmodule.entity.SedeTercero;
import com.pazzioliweb.tercerosmodule.repositori.SedeTerceroRepository;

@RestController
@RequestMapping("/api/sedeTercero")
public class SedeTerceroController {
	private final SedeTerceroService service;
	@Autowired
	 private SedeTerceroRepository sederepo;
	@Autowired
	public SedeTerceroController(SedeTerceroService service) {
		this.service = service;
	}
	
	/*
     * Trae las sedes de tercero con datos completos, por id de tercero.
     * 
     */
	@GetMapping("/listarPorTerceroId/{id}")
	public ResponseEntity<Map<String, Object>> listarPorTerceroId(
			@PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sedeId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection){
		
		Page<SedeTerceroDTO> sedesTerceroPage = service.listar(id,page, size, sortField, sortDirection);
		
		Map<String, Object> response = new HashMap<>();
        response.put("content", sedesTerceroPage.getContent());
        response.put("currentPage", sedesTerceroPage.getNumber());
        response.put("totalItems", sedesTerceroPage.getTotalElements());
        response.put("totalPages", sedesTerceroPage.getTotalPages());

        return ResponseEntity.ok(response);
	}
	
	/*
     * Actualiza una sedeTercero con los datos que se envien, por id.
     * 
     */  
	@PutMapping("/actualizar/{id}")
	public ResponseEntity<SedeTerceroDTOImpl> actualizar(@PathVariable Integer id, @RequestBody SedeTerceroDTOImpl dto){
		return ResponseEntity.ok(service.actualizar(id, dto));
	}
	
	@DeleteMapping("/eliminar/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
	    service.eliminar(id);
	    return ResponseEntity.noContent().build();
	}
	
	
	@PutMapping("/actualizar/{idtercero}/{idsede}/{esprincipal}")
	public ResponseEntity<Void> actualizarprincipal(@PathVariable Integer idtercero, @PathVariable Integer idsede,@PathVariable Boolean esprincipal){
		
	   List<SedeTercero> sedeter=sederepo.findBysedetercero(idtercero);
	   for(SedeTercero ter:sedeter) {
		   if(idsede==ter.getSedeId()) {
		   ter.setPrincipal(esprincipal);
		   }else {
			   ter.setPrincipal(false);
		   }
		   
		   sederepo.save(ter);
	   }
	   
	   return ResponseEntity.noContent().build();
	   }
	
	
}
