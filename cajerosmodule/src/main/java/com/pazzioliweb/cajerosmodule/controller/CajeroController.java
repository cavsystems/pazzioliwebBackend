package com.pazzioliweb.cajerosmodule.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.cajerosmodule.dtos.CajeroDTO;
import com.pazzioliweb.cajerosmodule.dtos.CajeroRequestDTO;
import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.cajerosmodule.service.CajeroService;

/**
 * Controlador para la gestión de cajeros.
 */
@RestController
@RequestMapping("/api/cajeros")
public class CajeroController {

    private final CajeroService cajeroService;

    @Autowired
    public CajeroController(CajeroService cajeroService) {
        this.cajeroService = cajeroService;
    }

    // ─────────────────────────────────────────────
    // LISTADO
    // ─────────────────────────────────────────────

    /**
     * GET /api/cajeros/listar
     * Lista todos los cajeros paginados.
     */
    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(defaultValue = "0")        int    page,
            @RequestParam(defaultValue = "10")       int    size,
            @RequestParam(defaultValue = "cajeroId") String sortField,
            @RequestParam(defaultValue = "asc")      String sortDirection) {

        Page<CajeroDTO> resultado = cajeroService.listar(page, size, sortField, sortDirection);
        Map<String, Object> response = new HashMap<>();
        response.put("content",      resultado.getContent());
        response.put("currentPage",  resultado.getNumber());
        response.put("totalItems",   resultado.getTotalElements());
        response.put("totalPages",   resultado.getTotalPages());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/cajeros/{id}
     * Obtiene un cajero por su cajeroId.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cajero> obtener(@PathVariable Integer id) {
        return cajeroService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/cajeros/usuario/{usuarioId}
     * Obtiene el cajero asociado a un usuario específico.
     * Útil para verificar si un usuario ya es cajero antes de asociarlo.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Map<String, Object>> obtenerPorUsuario(@PathVariable int usuarioId) {
        Map<String, Object> response = new HashMap<>();
        return cajeroService.buscarPorUsuario(usuarioId)
                .map(cajero -> {
                    response.put("tieneCajero", true);
                    response.put("cajero", cajero);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("tieneCajero", false);
                    response.put("mensaje", "El usuario no tiene cajero asignado");
                    return ResponseEntity.ok(response);
                });
    }

    // ─────────────────────────────────────────────
    // ASOCIACIÓN
    // ─────────────────────────────────────────────

    /**
     * POST /api/cajeros/asociar
     * Asocia un usuario como cajero (crea la relación one-to-one).
     *
     * Body esperado:
     * {
     *   "usuarioId": 3,
     *   "nombre": "Caja Principal",
     *   "estado": "ACTIVO",         (opcional, default ACTIVO)
     *   "codigoUsuarioCreo": 1      (opcional)
     * }
     *
     * Errores:
     *  - 400 si el usuario no existe
     *  - 400 si el usuario ya tiene cajero asignado
     */
    @PostMapping("/asociar")
    public ResponseEntity<Map<String, Object>> asociar(@RequestBody CajeroRequestDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Cajero cajero = cajeroService.asociar(dto);
            response.put("success", true);
            response.put("cajeroId", cajero.getCajeroId());
            response.put("usuarioId", cajero.getUsuario().getCodigo());
            response.put("nombre", cajero.getNombre());
            response.put("estado", cajero.getEstado());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ─────────────────────────────────────────────
    // CAMBIO DE ESTADO
    // ─────────────────────────────────────────────

    /**
     * PATCH /api/cajeros/{id}/estado?valor=INACTIVO
     * Activa o desactiva un cajero sin modificar el resto de sus datos.
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Map<String, Object>> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam String valor) {

        Map<String, Object> response = new HashMap<>();
        try {
            Cajero cajero = cajeroService.cambiarEstado(id, valor);
            response.put("success", true);
            response.put("cajeroId", cajero.getCajeroId());
            response.put("estado", cajero.getEstado());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ─────────────────────────────────────────────
    // ACTUALIZAR NOMBRE
    // ─────────────────────────────────────────────

    /**
     * PUT /api/cajeros/{id}
     * Actualiza el nombre de un cajero.
     * NO permite cambiar el usuario asociado (es one-to-one permanente).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Integer id,
            @RequestBody CajeroRequestDTO dto) {

        Map<String, Object> response = new HashMap<>();
        return cajeroService.buscarPorId(id)
                .map(cajero -> {
                    if (dto.getNombre() != null) cajero.setNombre(dto.getNombre());
                    cajero.setEstado(dto.getEstado().equalsIgnoreCase("INACTIVO")
                            ? Cajero.EstadoCajero.INACTIVO
                            : Cajero.EstadoCajero.ACTIVO);
                    cajeroService.guardar(cajero);
                    response.put("success", true);
                    response.put("cajeroId", cajero.getCajeroId());
                    response.put("nombre", cajero.getNombre());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("mensaje", "Cajero no encontrado: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    // ─────────────────────────────────────────────
    // ELIMINAR
    // ─────────────────────────────────────────────

    /**
     * DELETE /api/cajeros/{id}
     * Elimina un cajero. Solo si no tiene sesiones de caja registradas.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (cajeroService.buscarPorId(id).isPresent()) {
            cajeroService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

