package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.entity.AsientoContable;
import com.pazzioliweb.comprobantesmodule.entity.AsientoContableLinea;
import com.pazzioliweb.comprobantesmodule.repositori.AsientoContableRepository;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private final TercerosRepository tercerosRepo;
    private final com.pazzioliweb.comprobantesmodule.service.AsientoContableService asientoService;

    @PersistenceContext
    private EntityManager em;

    public AsientoContableController(AsientoContableRepository repo,
                                      TercerosRepository tercerosRepo,
                                      com.pazzioliweb.comprobantesmodule.service.AsientoContableService asientoService) {
        this.repo = repo;
        this.tercerosRepo = tercerosRepo;
        this.asientoService = asientoService;
    }

    /**
     * Si el asiento no tiene tercero en sus líneas, lo resolvemos desde el
     * documento origen (venta/orden de compra/recibo/egreso/devolución).
     * Retorna [terceroId, terceroNombre, terceroNit] o null si no aplica.
     */
    private Object[] resolverTerceroDocOrigen(String tipo, Long docId) {
        if (tipo == null || docId == null) return null;
        try {
            String sql;
            switch (tipo.toUpperCase()) {
                case "FC": case "VC": case "DV":
                    // ventas / devoluciones → cliente_id
                    if ("DV".equals(tipo)) {
                        sql = "SELECT t.tercero_id, COALESCE(t.razon_social, CONCAT_WS(' ', t.nombre_1, t.apellido_1)), t.identificacion " +
                              "FROM devoluciones_venta d JOIN ventas v ON v.id = d.venta_id JOIN terceros t ON t.tercero_id = v.cliente_id WHERE d.id = ?1";
                    } else {
                        sql = "SELECT t.tercero_id, COALESCE(t.razon_social, CONCAT_WS(' ', t.nombre_1, t.apellido_1)), t.identificacion " +
                              "FROM ventas v JOIN terceros t ON t.tercero_id = v.cliente_id WHERE v.id = ?1";
                    }
                    break;
                case "CC": case "CR":
                    sql = "SELECT t.tercero_id, COALESCE(t.razon_social, CONCAT_WS(' ', t.nombre_1, t.apellido_1)), t.identificacion " +
                          "FROM ordenes_compra o JOIN terceros t ON t.tercero_id = o.proveedor_id WHERE o.id = ?1";
                    break;
                case "RC":
                    sql = "SELECT r.tercero_id, r.tercero_nombre, r.tercero_nit FROM recibos_caja r WHERE r.id = ?1";
                    break;
                case "CE":
                    sql = "SELECT e.tercero_id, e.tercero_nombre, e.tercero_nit FROM comprobantes_egreso e WHERE e.id = ?1";
                    break;
                default:
                    return null;
            }
            Object[] row = (Object[]) em.createNativeQuery(sql).setParameter(1, docId).getSingleResult();
            return row;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Limpia el sufijo " - {nombre cliente}" de la descripción ya que ahora se muestra
     * el cliente en columna separada. Solo elimina si coincide con el nombre del tercero.
     */
    private String limpiarDescripcion(String desc, String terceroNombre) {
        if (desc == null || terceroNombre == null || terceroNombre.isBlank()) return desc;
        String suffix = " - " + terceroNombre.trim();
        if (desc.endsWith(suffix)) {
            return desc.substring(0, desc.length() - suffix.length()).trim();
        }
        return desc;
    }

    /**
     * Crear asiento contable MANUAL desde el formulario UI.
     * Body: {
     *   fecha: "2026-05-22",
     *   descripcion: "Ajuste manual mensual",
     *   lineas: [
     *     { cuentaContableId, debito?, credito?, terceroId?, terceroNombre?, descripcion? },
     *     ...
     *   ]
     * }
     * El servicio valida que débitos = créditos.
     */
    @PostMapping("/manual")
    @Transactional
    public ResponseEntity<Map<String, Object>> crearManual(@RequestBody Map<String, Object> body) {
        try {
            LocalDate fecha = body.get("fecha") != null
                    ? LocalDate.parse(body.get("fecha").toString())
                    : LocalDate.now();
            String descripcion = body.get("descripcion") != null ? body.get("descripcion").toString() : "Asiento manual";

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> lineasIn = (List<Map<String, Object>>) body.get("lineas");
            if (lineasIn == null || lineasIn.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El asiento debe tener al menos 1 línea"));
            }

            List<com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO> lineas = new ArrayList<>();
            for (Map<String, Object> li : lineasIn) {
                Integer cuentaId = li.get("cuentaContableId") != null
                        ? Integer.parseInt(li.get("cuentaContableId").toString()) : null;
                if (cuentaId == null) continue;
                java.math.BigDecimal debito = new java.math.BigDecimal(li.getOrDefault("debito", "0").toString());
                java.math.BigDecimal credito = new java.math.BigDecimal(li.getOrDefault("credito", "0").toString());
                String desc = li.get("descripcion") != null ? li.get("descripcion").toString() : descripcion;

                com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO l;
                if (debito.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    l = com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO.debito(cuentaId, debito, desc);
                } else if (credito.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    l = com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO.credito(cuentaId, credito, desc);
                } else {
                    continue;
                }

                if (li.get("terceroId") != null) {
                    Integer tid = Integer.parseInt(li.get("terceroId").toString());
                    String tnombre = li.get("terceroNombre") != null ? li.get("terceroNombre").toString() : "";
                    l.conTercero(tid, tnombre);
                }
                lineas.add(l);
            }
            if (lineas.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ninguna línea tiene débito o crédito > 0"));
            }

            // Número del asiento: MAN-{timestamp}
            String numero = "MAN-" + System.currentTimeMillis() / 1000;

            AsientoContable asiento = asientoService.generarAsiento(
                    numero, fecha, descripcion, "MANUAL", null, null, lineas);
            return ResponseEntity.ok(Map.of(
                    "id", asiento.getId(),
                    "numeroAsiento", asiento.getNumeroAsiento(),
                    "totalDebito", asiento.getTotalDebito(),
                    "totalCredito", asiento.getTotalCredito(),
                    "mensaje", "Asiento manual creado correctamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
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

        // 1) Tercero principal del asiento — primero buscamos en las líneas (caso crédito: CxC/CxP)
        String terceroNombre = null;
        String terceroNit = null;
        Integer terceroId = null;
        try {
            if (a.getLineas() != null) {
                for (AsientoContableLinea li : a.getLineas()) {
                    if (li.getTerceroId() != null) {
                        terceroId = li.getTerceroId();
                        terceroNombre = li.getTerceroNombre();
                        try {
                            Terceros t = tercerosRepo.findByTerceroId(li.getTerceroId()).orElse(null);
                            if (t != null) terceroNit = t.getIdentificacion();
                        } catch (Exception ignored) {}
                        break;
                    }
                }
            }
        } catch (Exception ignored) {}

        // 2) Si ninguna línea tiene tercero (caso contado: FC/CC/RC/CE), lo buscamos en el documento origen
        if (terceroId == null) {
            Object[] info = resolverTerceroDocOrigen(a.getDocumentoOrigenTipo(), a.getDocumentoOrigenId());
            if (info != null && info.length >= 3) {
                try {
                    terceroId = info[0] != null ? Integer.valueOf(info[0].toString()) : null;
                    terceroNombre = info[1] != null ? info[1].toString() : null;
                    terceroNit = info[2] != null ? info[2].toString() : null;
                } catch (Exception ignored) {}
            }
        }

        m.put("terceroId", terceroId);
        m.put("terceroNombre", terceroNombre);
        m.put("terceroNit", terceroNit);

        // Limpiamos el sufijo "- {cliente}" de la descripción ya que ahora aparece en columna aparte
        m.put("descripcion", limpiarDescripcion(a.getDescripcion(), terceroNombre));

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
            // Lookup del NIT/identificación del tercero para mostrar en la UI
            String terceroNit = null;
            if (l.getTerceroId() != null) {
                try {
                    Terceros t = tercerosRepo.findByTerceroId(l.getTerceroId()).orElse(null);
                    if (t != null) terceroNit = t.getIdentificacion();
                } catch (Exception ignored) {}
            }
            lm.put("terceroNit", terceroNit);
            lm.put("descripcion", l.getDescripcion());
            lineas.add(lm);
        }
        m.put("lineas", lineas);
        return m;
    }
}
