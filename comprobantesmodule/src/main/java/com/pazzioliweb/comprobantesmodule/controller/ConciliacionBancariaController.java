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

    /**
     * Conciliación automática: el frontend sube las filas parseadas del extracto bancario
     * (fecha + monto + descripción + referencia) y el backend intenta hacer match con los
     * movimientos pendientes de esa cuenta dentro de una tolerancia de ±2 días en fecha
     * y monto exacto. Devuelve resumen: conciliados, sin-match-extracto, sin-match-libro.
     */
    @PostMapping("/conciliar-extracto")
    @Transactional
    public ResponseEntity<Map<String, Object>> conciliarExtracto(@RequestBody Map<String, Object> body) {
        Long cuentaBancariaId = Long.valueOf(body.get("cuentaBancariaId").toString());
        Integer toleranciaDias = body.get("toleranciaDias") != null
                ? Integer.parseInt(body.get("toleranciaDias").toString()) : 2;
        String usuario = body.get("usuarioConcilio") != null ? body.get("usuarioConcilio").toString() : "extracto";
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> filas = (List<Map<String, Object>>) body.getOrDefault("filas", new ArrayList<>());

        // Resolver cuenta contable de la cuenta bancaria
        // getSingleResult() con UNA columna retorna el escalar directo, NO Object[]
        Object res = em.createNativeQuery(
                "SELECT cuenta_contable_id FROM cuentas_bancarias WHERE id = :id")
                .setParameter("id", cuentaBancariaId).getSingleResult();
        Integer cuentaContableId = ((Number) res).intValue();

        // Cargar movimientos NO conciliados de los últimos 90 días
        LocalDate desde = LocalDate.now().minusDays(90);
        LocalDate hasta = LocalDate.now().plusDays(1);
        List<AsientoContableLinea> candidatos = lineaRepo.librodeMayorPorCuenta(cuentaContableId, desde, hasta);
        candidatos.removeIf(l -> Boolean.TRUE.equals(l.getConciliado()));

        int conciliados = 0;
        int sinMatchExtracto = 0;
        List<Map<String, Object>> matchedDetail = new ArrayList<>();
        List<Map<String, Object>> noMatchDetail = new ArrayList<>();

        for (Map<String, Object> fila : filas) {
            LocalDate fechaExtracto = LocalDate.parse(fila.get("fecha").toString());
            BigDecimal montoExtracto = new BigDecimal(fila.get("monto").toString()).abs();
            // Valor CON signo del extracto: + = entrada (depósito) → débito al banco; − = salida → crédito.
            BigDecimal montoExtractoSigned = new BigDecimal(fila.get("monto").toString());
            String referencia = fila.get("referencia") != null ? fila.get("referencia").toString() : null;
            String descripcionExtracto = fila.get("descripcion") != null ? fila.get("descripcion").toString() : "";

            // Buscar candidato que coincida: monto exacto + fecha dentro de tolerancia
            AsientoContableLinea match = null;
            for (AsientoContableLinea l : candidatos) {
                // Comparar CON signo: un depósito no debe conciliar contra un retiro del mismo monto.
                BigDecimal mLinea = l.getDebito().subtract(l.getCredito());
                if (mLinea.compareTo(montoExtractoSigned) != 0) continue;
                LocalDate fLinea = l.getAsiento().getFecha();
                long diff = Math.abs(java.time.temporal.ChronoUnit.DAYS.between(fLinea, fechaExtracto));
                if (diff <= toleranciaDias) { match = l; break; }
            }

            if (match != null) {
                match.setConciliado(true);
                match.setFechaConciliacion(LocalDate.now());
                match.setReferenciaExtracto(referencia);
                match.setUsuarioConcilio(usuario);
                lineaRepo.save(match);
                candidatos.remove(match);  // no usar el mismo dos veces
                conciliados++;
                Map<String, Object> d = new HashMap<>();
                d.put("lineaId", match.getId());
                d.put("fechaLibro", match.getAsiento().getFecha());
                d.put("fechaExtracto", fechaExtracto);
                d.put("monto", montoExtracto);
                d.put("descripcionExtracto", descripcionExtracto);
                d.put("referencia", referencia);
                matchedDetail.add(d);
            } else {
                sinMatchExtracto++;
                Map<String, Object> d = new HashMap<>();
                d.put("fecha", fechaExtracto);
                d.put("monto", montoExtracto);
                d.put("descripcion", descripcionExtracto);
                d.put("referencia", referencia);
                noMatchDetail.add(d);
            }
        }

        // Movimientos en el libro que NO aparecieron en el extracto
        int sinMatchLibro = candidatos.size();

        Map<String, Object> resp = new HashMap<>();
        resp.put("totalExtracto", filas.size());
        resp.put("conciliados", conciliados);
        resp.put("sinMatchExtracto", sinMatchExtracto);
        resp.put("sinMatchLibro", sinMatchLibro);
        resp.put("matchedDetail", matchedDetail);
        resp.put("noMatchDetail", noMatchDetail);
        return ResponseEntity.ok(resp);
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
