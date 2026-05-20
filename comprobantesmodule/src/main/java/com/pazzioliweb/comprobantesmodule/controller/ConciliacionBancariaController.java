package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.entity.AsientoContableLinea;
import com.pazzioliweb.comprobantesmodule.repositori.AsientoContableLineaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Conciliación bancaria estilo Alegra: para una cuenta bancaria, marca los
 * movimientos contables como "conciliados" (= ya aparecieron en el extracto
 * real del banco). Cada línea de asiento que afecte la cuenta contable de la
 * cuenta bancaria es un movimiento conciliable.
 */
@RestController
@RequestMapping("/api/conciliacion-bancaria")
@CrossOrigin(origins = "*")
public class ConciliacionBancariaController {

    private final AsientoContableLineaRepository lineaRepo;

    @PersistenceContext
    private EntityManager em;

    public ConciliacionBancariaController(AsientoContableLineaRepository lineaRepo) {
        this.lineaRepo = lineaRepo;
    }

    /**
     * Lista los movimientos de una cuenta bancaria en un rango de fechas
     * con su estado de conciliación. El frontend muestra checkboxes para
     * marcar/desmarcar conciliados.
     */
    @GetMapping("/movimientos")
    public ResponseEntity<Map<String, Object>> movimientos(
            @RequestParam Long cuentaBancariaId,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) String filtroEstado) {

        LocalDate d = desde != null ? LocalDate.parse(desde) : LocalDate.now().withDayOfMonth(1);
        LocalDate h = hasta != null ? LocalDate.parse(hasta) : LocalDate.now();

        // Resuelve la cuenta contable y el saldo inicial
        Object[] cb = (Object[]) em.createNativeQuery(
                "SELECT cb.id, cb.nombre, cb.banco, cb.numero_cuenta, cb.saldo_inicial, cb.cuenta_contable_id " +
                "FROM cuentas_bancarias cb WHERE cb.id = :id")
                .setParameter("id", cuentaBancariaId).getSingleResult();
        Integer cuentaContableId = ((Number) cb[5]).intValue();

        List<AsientoContableLinea> lineas = lineaRepo.librodeMayorPorCuenta(cuentaContableId, d, h);

        // Filtros
        if ("conciliados".equals(filtroEstado)) {
            lineas.removeIf(l -> !Boolean.TRUE.equals(l.getConciliado()));
        } else if ("pendientes".equals(filtroEstado)) {
            lineas.removeIf(l -> Boolean.TRUE.equals(l.getConciliado()));
        }

        BigDecimal totalConciliado = BigDecimal.ZERO;
        BigDecimal totalPendiente = BigDecimal.ZERO;
        List<Map<String, Object>> movs = new ArrayList<>();
        for (AsientoContableLinea l : lineas) {
            BigDecimal monto = l.getDebito().subtract(l.getCredito());
            Map<String, Object> m = new HashMap<>();
            m.put("lineaId", l.getId());
            m.put("fecha", l.getAsiento().getFecha());
            m.put("asiento", l.getAsiento().getNumeroAsiento());
            m.put("tipoDocumento", l.getAsiento().getDocumentoOrigenTipo());
            m.put("descripcion", l.getDescripcion() != null ? l.getDescripcion() : l.getAsiento().getDescripcion());
            m.put("tercero", l.getTerceroNombre());
            m.put("debito", l.getDebito());
            m.put("credito", l.getCredito());
            m.put("monto", monto);
            m.put("conciliado", Boolean.TRUE.equals(l.getConciliado()));
            m.put("fechaConciliacion", l.getFechaConciliacion());
            m.put("referenciaExtracto", l.getReferenciaExtracto());
            m.put("usuarioConcilio", l.getUsuarioConcilio());
            movs.add(m);

            if (Boolean.TRUE.equals(l.getConciliado())) totalConciliado = totalConciliado.add(monto);
            else totalPendiente = totalPendiente.add(monto);
        }

        Map<String, Object> resp = new HashMap<>();
        Map<String, Object> banco = new HashMap<>();
        banco.put("id", cb[0]);
        banco.put("nombre", cb[1]);
        banco.put("banco", cb[2]);
        banco.put("numeroCuenta", cb[3]);
        banco.put("saldoInicial", cb[4]);
        resp.put("cuentaBancaria", banco);
        resp.put("desde", d);
        resp.put("hasta", h);
        resp.put("totalConciliado", totalConciliado);
        resp.put("totalPendiente", totalPendiente);
        resp.put("movimientos", movs);
        return ResponseEntity.ok(resp);
    }

    /** Marca/desmarca un movimiento como conciliado. */
    @PutMapping("/{lineaId}/marcar")
    @Transactional
    public ResponseEntity<Map<String, Object>> marcar(@PathVariable Long lineaId,
                                                       @RequestBody Map<String, Object> body) {
        AsientoContableLinea l = lineaRepo.findById(lineaId)
                .orElseThrow(() -> new IllegalArgumentException("Línea no encontrada: " + lineaId));
        boolean conciliado = Boolean.TRUE.equals(body.get("conciliado"));
        l.setConciliado(conciliado);
        if (conciliado) {
            l.setFechaConciliacion(LocalDate.now());
            if (body.get("referenciaExtracto") != null) {
                l.setReferenciaExtracto(body.get("referenciaExtracto").toString());
            }
            if (body.get("usuarioConcilio") != null) {
                l.setUsuarioConcilio(body.get("usuarioConcilio").toString());
            }
        } else {
            l.setFechaConciliacion(null);
            l.setReferenciaExtracto(null);
            l.setUsuarioConcilio(null);
        }
        lineaRepo.save(l);
        Map<String, Object> r = new HashMap<>();
        r.put("lineaId", lineaId);
        r.put("conciliado", l.getConciliado());
        r.put("fechaConciliacion", l.getFechaConciliacion());
        return ResponseEntity.ok(r);
    }

    /** Marca varios movimientos como conciliados en una sola llamada. */
    @PutMapping("/marcar-lote")
    @Transactional
    public ResponseEntity<Map<String, Object>> marcarLote(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Number> ids = (List<Number>) body.getOrDefault("lineaIds", new ArrayList<>());
        boolean conciliado = Boolean.TRUE.equals(body.get("conciliado"));
        String usuario = body.get("usuarioConcilio") != null ? body.get("usuarioConcilio").toString() : null;
        int n = 0;
        for (Number id : ids) {
            Optional<AsientoContableLinea> opt = lineaRepo.findById(id.longValue());
            if (opt.isPresent()) {
                AsientoContableLinea l = opt.get();
                l.setConciliado(conciliado);
                l.setFechaConciliacion(conciliado ? LocalDate.now() : null);
                if (conciliado && usuario != null) l.setUsuarioConcilio(usuario);
                else if (!conciliado) { l.setUsuarioConcilio(null); l.setReferenciaExtracto(null); }
                lineaRepo.save(l);
                n++;
            }
        }
        Map<String, Object> r = new HashMap<>();
        r.put("actualizados", n);
        return ResponseEntity.ok(r);
    }
}
