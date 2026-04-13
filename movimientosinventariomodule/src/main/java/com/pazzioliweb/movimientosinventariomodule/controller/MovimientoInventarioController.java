package com.pazzioliweb.movimientosinventariomodule.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioCreateDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioResponseDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioUpdateDto;
import com.pazzioliweb.movimientosinventariomodule.service.MovimientoInventarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventario/movimientos")
public class MovimientoInventarioController {

    @Autowired
    private MovimientoInventarioService movimientoService;

    /**
     * Crear un nuevo movimiento de inventario (entrada, salida o traslado).
     * El body debe tener: comprobanteId, tipo (ENTRADA/SALIDA/TRASLADO),
     * fechaEmision, detalles (productoVarianteId, bodegaOrigenId, bodegaDestinoId,
     * cantidad, costoUnitario).
     */
    @PostMapping
    public ResponseEntity<?> crearMovimiento(@Valid @RequestBody MovimientoInventarioCreateDto createDto) {
        try {
            MovimientoInventarioResponseDto response = movimientoService.crearMovimiento(createDto, null, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Listar movimientos con filtros opcionales (paginado).
     */
    @GetMapping
    public ResponseEntity<Page<MovimientoInventarioResponseDto>> listar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<MovimientoInventarioResponseDto> result =
                movimientoService.listarMovimientos(pageable, tipo, desde, hasta);
        return ResponseEntity.ok(result);
    }

    /**
     * Obtener un movimiento por ID con todos sus detalles.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            MovimientoInventarioResponseDto response = movimientoService.obtenerMovimientoConDetalles(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualizar un movimiento en estado BORRADOR (cabecera, no detalles).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                         @Valid @RequestBody MovimientoInventarioUpdateDto updateDto) {
        try {
            MovimientoInventarioResponseDto response = movimientoService.actualizarMovimiento(id, updateDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Anular un movimiento (revierte kardex).
     */
    @PostMapping("/{id}/anular")
    public ResponseEntity<?> anular(@PathVariable Long id) {
        try {
            movimientoService.anularMovimiento(id);
            return ResponseEntity.ok(Map.of("mensaje", "Movimiento anulado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
