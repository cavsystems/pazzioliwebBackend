package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.dtos.ModoContabilidadDTO;
import com.pazzioliweb.comprobantesmodule.entity.ConfiguracionContabilidad;
import com.pazzioliweb.comprobantesmodule.service.ModoContabilidadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * modoPOS — lectura/escritura del flag de contabilidad de la empresa (tenant actual).
 * Se consume desde el módulo de Empresas del front (toggle "Lleva contabilidad" + fecha de corte).
 * Opera SIEMPRE sobre el tenant de la petición (X-TenantID); no acopla el backend de empresas.
 */
@RestController
@RequestMapping("/api/contabilidad")
public class ModoContabilidadController {

    private final ModoContabilidadService modoContabilidad;

    public ModoContabilidadController(ModoContabilidadService modoContabilidad) {
        this.modoContabilidad = modoContabilidad;
    }

    /** Estado actual del flag para el tenant en contexto. */
    @GetMapping("/modo")
    public ResponseEntity<ModoContabilidadDTO> obtener() {
        ModoContabilidadDTO dto = new ModoContabilidadDTO();
        dto.setContabilidadActiva(modoContabilidad.contabilidadActiva());
        dto.setContabilidadDesde(modoContabilidad.contabilidadDesde());
        return ResponseEntity.ok(dto);
    }

    /** Actualiza el flag para el tenant en contexto. */
    @PutMapping("/modo")
    public ResponseEntity<ModoContabilidadDTO> actualizar(@RequestBody ModoContabilidadDTO body) {
        ConfiguracionContabilidad cfg = modoContabilidad.guardar(
                body.getContabilidadActiva(), body.getContabilidadDesde());
        ModoContabilidadDTO dto = new ModoContabilidadDTO();
        dto.setContabilidadActiva(cfg.getContabilidadActiva());
        dto.setContabilidadDesde(cfg.getContabilidadDesde());
        return ResponseEntity.ok(dto);
    }
}
