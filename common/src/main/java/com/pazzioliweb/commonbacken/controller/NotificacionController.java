package com.pazzioliweb.commonbacken.controller;

import com.pazzioliweb.commonbacken.dtos.response.PaginationResponse;
import com.pazzioliweb.commonbacken.entity.Notificacion;
import com.pazzioliweb.commonbacken.services.NotificacionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Notificaciones de la campana del navbar (ver Notificacion). Sin distinción por
 *  usuario: visibles para todos los usuarios de la empresa/tenant actual. */
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<Notificacion>> listar(
            @RequestParam(defaultValue = "false") boolean soloNoLeidas,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // El orden lo define el nombre del método del repositorio (...OrderByFechaCreacionDesc).
        PageRequest pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(PaginationResponse.of(service.listar(soloNoLeidas, pageable)));
    }

    @GetMapping("/no-leidas/conteo")
    public ResponseEntity<Map<String, Long>> contarNoLeidas() {
        return ResponseEntity.ok(Map.of("conteo", service.contarNoLeidas()));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> marcarLeida(@PathVariable Long id) {
        service.marcarLeida(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodasLeidas() {
        service.marcarTodasLeidas();
        return ResponseEntity.noContent().build();
    }
}
