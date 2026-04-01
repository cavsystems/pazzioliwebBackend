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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.commonbacken.dtos.response.ApiResponse;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaProductoCreateDTO;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaProductoResponseDTO;
import com.pazzioliweb.productosmodule.entity.UnidadesMedidaProducto;
import com.pazzioliweb.productosmodule.service.UnidadesMedidaProductoService;

@RestController
@RequestMapping("/api/unidadesMedidaProducto")
public class UnidadesMedidaProductoController {

    private final UnidadesMedidaProductoService service;

    public UnidadesMedidaProductoController(UnidadesMedidaProductoService service) {
        this.service = service;
    }

    @GetMapping("/listar")
    public ResponseEntity<ApiResponse<Page<UnidadMedidaProductoResponseDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "productoId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UnidadMedidaProductoResponseDTO> listado = service.listar(pageable);

        return ResponseEntity.ok(ApiResponse.success("Listado obtenido", listado));
    }

    @GetMapping("/listar-por-producto/{productoId}")
    public ResponseEntity<ApiResponse<Page<UnidadMedidaProductoResponseDTO>>> listarPorProducto(
            @PathVariable Integer productoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UnidadMedidaProductoResponseDTO> listado = service.listarPorProducto(productoId, pageable);

        return ResponseEntity.ok(ApiResponse.success("Listado para producto", listado));
    }

    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<UnidadMedidaProductoResponseDTO>> crear(
            @RequestBody UnidadMedidaProductoCreateDTO dto) {

        var resp = service.crear(dto);
        return ResponseEntity.ok(ApiResponse.success("Registro creado", resp));
    }

    @GetMapping("/{productoId}/{unidadMedidaId}")
    public ResponseEntity<ApiResponse<UnidadMedidaProductoResponseDTO>> obtenerId(
            @PathVariable Integer productoId,
            @PathVariable Integer unidadMedidaId) {

        var dto = service.obtenerPorId(productoId, unidadMedidaId);
        return ResponseEntity.ok(ApiResponse.success("Registro encontrado", dto));
    }

    @DeleteMapping("/{productoId}/{unidadMedidaId}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Integer productoId,
            @PathVariable Integer unidadMedidaId) {

        service.eliminar(productoId, unidadMedidaId);
        return ResponseEntity.ok(ApiResponse.success("Eliminado correctamente", null));
    }
}
