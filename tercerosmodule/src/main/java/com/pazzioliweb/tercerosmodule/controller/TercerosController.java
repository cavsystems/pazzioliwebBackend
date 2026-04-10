package com.pazzioliweb.tercerosmodule.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.tercerosmodule.dtos.TerceroDTO;
import com.pazzioliweb.tercerosmodule.dtos.TerceroDTOImpl;
import com.pazzioliweb.tercerosmodule.dtos.TerceroDtoresponse;
import com.pazzioliweb.tercerosmodule.dtos.TerceroResumenDTO;
import com.pazzioliweb.tercerosmodule.service.TercerosService;
@Component
@RestController
@RequestMapping("/api/terceros")
public class TercerosController {
    private final TercerosService terceroService;

    @Autowired
    public TercerosController(TercerosService terceroService) {
        this.terceroService = terceroService;
    }

    /*
     * Listado Basico para panel consulta terceros.
     *
     */
    @GetMapping("/listarTercerosBasicos")
    public ResponseEntity<Map<String, Object>> listarTodosBasico(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "terceroId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        System.out.println("metodo listar tercero");

        Page<TerceroResumenDTO> tercerosPage = terceroService.listarTerceroBasicos(page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", tercerosPage.getContent());
        response.put("currentPage", tercerosPage.getNumber());
        response.put("totalItems", tercerosPage.getTotalElements());
        response.put("totalPages", tercerosPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/idtercero/{terceroid}")
    public TerceroDTOImpl obtenerporid(@PathVariable int terceroid){
        return terceroService.buscarconconctatoid(terceroid);

    }


    /*
     * Listado Completo para consulta de terceros, trae todo los detalles.
     *
     */
    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar() {
        System.out.println("Método listar terceros ejecutado");

        List<TerceroDTOImpl> terceros = terceroService.listarTercerosConDetalles();

        Map<String, Object> response = new HashMap<>();
        response.put("content", terceros);
        response.put("totalItems", terceros.size());

        return ResponseEntity.ok(response);
    }

    /*
     * Listado de terceros basicos, por filtro que aplica para identificacion o razonSocial.
     *
     */
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscar(
            @RequestParam String termino,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "terceroId") String sortField,

            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int tipoprovedor) {

        Page<TerceroResumenDTO> tercerosPage = terceroService.buscar(termino, tipoprovedor, page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", tercerosPage.getContent());
        response.put("currentPage", tercerosPage.getNumber());
        response.put("totalItems", tercerosPage.getTotalElements());
        response.put("totalPages", tercerosPage.getTotalPages());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/buscarconconcta")
    public ResponseEntity<Map<String, Object>> buscarconconacto(
            @RequestParam String termino,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "terceroId") String sortField,

            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int tipoprovedor) {

        Page<TerceroDTOImpl> tercerosPage = terceroService.buscarconconcta(termino, tipoprovedor, page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", tercerosPage.getContent());
        response.put("currentPage", tercerosPage.getNumber());
        response.put("totalItems", tercerosPage.getTotalElements());
        response.put("totalPages", tercerosPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscarconconctanormal")
    public ResponseEntity<Map<String, Object>> buscarconconactonormal(
            @RequestParam String termino,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "terceroId") String sortField,

            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int tipoprovedor) {

        Page<TerceroDTOImpl> tercerosPage = terceroService.buscarconconctanormal(termino, tipoprovedor, page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", tercerosPage.getContent());
        response.put("currentPage", tercerosPage.getNumber());
        response.put("totalItems", tercerosPage.getTotalElements());
        response.put("totalPages", tercerosPage.getTotalPages());

        return ResponseEntity.ok(response);
    }



    @PostMapping("/crear")
    public ResponseEntity<TerceroDTOImpl> crear(@RequestBody TerceroDTOImpl terceroDTO) {
        return ResponseEntity.ok(terceroService.guardar(terceroDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        terceroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
