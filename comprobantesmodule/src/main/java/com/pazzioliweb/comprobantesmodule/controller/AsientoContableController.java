package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.entity.AsientoContable;
import com.pazzioliweb.comprobantesmodule.entity.AsientoContableLinea;
import com.pazzioliweb.comprobantesmodule.repositori.AsientoContableRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/asientos-contables")
@CrossOrigin(origins = "*")
public class AsientoContableController {

    private final AsientoContableRepository repo;

    public AsientoContableController(AsientoContableRepository repo) {
        this.repo = repo;
    }

    /** Lista asientos filtrando por rango de fechas. Default = mes actual. */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> listar(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta) {
        LocalDate d = desde != null ? LocalDate.parse(desde) : LocalDate.now().withDayOfMonth(1);
        LocalDate h = hasta != null ? LocalDate.parse(hasta) : LocalDate.now();
        List<AsientoContable> asientos = repo.rangoFechas(d, h);
        List<Map<String, Object>> res = new ArrayList<>();
        for (AsientoContable a : asientos) {
            res.add(toResumen(a));
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
        return repo.findByIdConDetalle(id)
                .map(a -> ResponseEntity.ok(toDetalle(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** Buscar asiento por documento origen (RC + 15, etc.). */
    @GetMapping("/por-documento")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> porDocumento(
            @RequestParam String tipo, @RequestParam Long id) {
        return repo.findByDocumentoOrigenTipoAndDocumentoOrigenId(tipo, id)
                .map(a -> ResponseEntity.ok(toDetalle(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    private Map<String, Object> toResumen(AsientoContable a) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", a.getId());
        m.put("numeroAsiento", a.getNumeroAsiento());
        m.put("fecha", a.getFecha());
        m.put("descripcion", a.getDescripcion());
        m.put("totalDebito", a.getTotalDebito());
        m.put("totalCredito", a.getTotalCredito());
        m.put("documentoOrigenTipo", a.getDocumentoOrigenTipo());
        m.put("documentoOrigenId", a.getDocumentoOrigenId());
        m.put("estado", a.getEstado());
        if (a.getComprobante() != null) {
            m.put("comprobanteId", a.getComprobante().getId());
            m.put("comprobantePrefijo", a.getComprobante().getPrefijo());
        }
        // Estado DIAN (solo aplica a ventas)
        m.put("estadoDian", a.getEstadoDian());
        m.put("cufe", a.getCufe());
        m.put("fechaAutorizacionDian", a.getFechaAutorizacionDian());
        m.put("mensajeDian", a.getMensajeDian());
        return m;
    }

    private Map<String, Object> toDetalle(AsientoContable a) {
        Map<String, Object> m = toResumen(a);
        List<Map<String, Object>> lineas = new ArrayList<>();
        for (AsientoContableLinea l : a.getLineas()) {
            Map<String, Object> lm = new HashMap<>();
            lm.put("id", l.getId());
            lm.put("orden", l.getOrden());
            lm.put("cuentaContableId", l.getCuentaContable() != null ? l.getCuentaContable().getId() : null);
            lm.put("cuentaContableCodigo", l.getCuentaContable() != null ? l.getCuentaContable().getCodigo() : null);
            lm.put("cuentaContableNombre", l.getCuentaContable() != null ? l.getCuentaContable().getNombre() : null);
            lm.put("debito", l.getDebito());
            lm.put("credito", l.getCredito());
            lm.put("terceroId", l.getTerceroId());
            lm.put("terceroNombre", l.getTerceroNombre());
            lm.put("descripcion", l.getDescripcion());
            lineas.add(lm);
        }
        m.put("lineas", lineas);
        return m;
    }
}
