package com.pazzioliweb.commonbacken.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import com.pazzioliweb.commonbacken.entity.Retenciones;
import com.pazzioliweb.commonbacken.repositorio.RetencionesRepositori;

@Component
@RestController
@RequestMapping("/api/retencion")
public class Retencionescontroller {

    @Autowired
    private RetencionesRepositori repore;

    /** Lista todas las retenciones. */
    @GetMapping("traerretenciones/retenciones")
    public ResponseEntity<List<Retenciones>> listar() {
        return ResponseEntity.ok(repore.findAll());
    }

    /** Obtiene una retención por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<Retenciones> obtenerPorId(@PathVariable("id") Long id) {
        return repore.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea una nueva retención.
     * Body: { "nombre": "...", "base": 1424763.00, "porcentaje": 3.5, "codigo": 1 }
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Retenciones retencion) {
        try {
            if (retencion.getNombre() == null || retencion.getNombre().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El nombre es requerido"));
            }
            if (retencion.getBase() == null || retencion.getBase() < 0) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "La base debe ser mayor o igual a 0"));
            }
            if (retencion.getPorcentaje() == null || retencion.getPorcentaje() < 0) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El porcentaje debe ser mayor o igual a 0"));
            }
            // No se permite crear retenciones con nombre duplicado
            boolean existeNombre = repore.findAll().stream()
                    .anyMatch(r -> r.getNombre() != null && r.getNombre().equalsIgnoreCase(retencion.getNombre().trim()));
            if (existeNombre) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "Ya existe una retención con ese nombre"));
            }
            retencion.setNombre(retencion.getNombre().trim());
            Retenciones guardada = repore.save(retencion);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error creando retención: " + e.getMessage()));
        }
    }

    /**
     * Actualiza base y porcentaje de una retención existente.
     * Body: { "nombre": "...", "base": 1424763.00, "porcentaje": 3.5 }
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable("id") Long id,
                                         @RequestBody Retenciones dto) {
        try {
            Optional<Retenciones> optional = repore.findById(id);
            if (optional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Retenciones retencion = optional.get();

            if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
                // Verificar nombre duplicado en otra retención
                boolean nombreDuplicado = repore.findAll().stream()
                        .anyMatch(r -> r.getRetencionId() != null && r.getRetencionId().longValue() != id
                                && r.getNombre() != null
                                && r.getNombre().equalsIgnoreCase(dto.getNombre().trim()));
                if (nombreDuplicado) {
                    return ResponseEntity.badRequest().body(Map.of("mensaje", "Ya existe otra retención con ese nombre"));
                }
                retencion.setNombre(dto.getNombre().trim());
            }
            if (dto.getBase() != null) {
                if (dto.getBase() < 0) return ResponseEntity.badRequest().body(Map.of("mensaje", "La base no puede ser negativa"));
                retencion.setBase(dto.getBase());
            }
            if (dto.getPorcentaje() != null) {
                if (dto.getPorcentaje() < 0) return ResponseEntity.badRequest().body(Map.of("mensaje", "El porcentaje no puede ser negativo"));
                retencion.setPorcentaje(dto.getPorcentaje());
            }
            if (dto.getCodigo() != null) {
                retencion.setCodigo(dto.getCodigo());
            }

            return ResponseEntity.ok(repore.save(retencion));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error actualizando retención: " + e.getMessage()));
        }
    }

    /**
     * Elimina una retención.
     * Solo se puede eliminar si no está asignada a ningún tercero.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable("id") Long id) {
        try {
            if (!repore.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            repore.deleteById(id);
            return ResponseEntity.ok(Map.of("mensaje", "Retención eliminada correctamente"));
        } catch (Exception e) {
            // Si hay FK constraint (está asignada a terceros) → error controlado
            String msg = e.getMessage() != null && e.getMessage().contains("foreign key")
                    ? "No se puede eliminar: esta retención está asignada a uno o más proveedores/clientes. Desasígnela primero."
                    : "Error eliminando retención: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", msg));
        }
    }
}
