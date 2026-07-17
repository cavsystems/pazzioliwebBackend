package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.commonbacken.events.AsientoManualRegistradoEvent;
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
    private final com.pazzioliweb.comprobantesmodule.service.TipoComprobanteManualService tipoComprobanteManualService;
    private final com.pazzioliweb.comprobantesmodule.service.PeriodoContableService periodoService;
    private final com.pazzioliweb.comprobantesmodule.repositori.AuditoriaMantenimientoRepository auditoriaRepo;

    @PersistenceContext
    private EntityManager em;

    // Publica AsientoManualRegistradoEvent para que ventas/compras sincronicen el subledger de cartera.
    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

    public AsientoContableController(AsientoContableRepository repo,
                                      TercerosRepository tercerosRepo,
                                      com.pazzioliweb.comprobantesmodule.service.AsientoContableService asientoService,
                                      com.pazzioliweb.comprobantesmodule.service.TipoComprobanteManualService tipoComprobanteManualService,
                                      com.pazzioliweb.comprobantesmodule.service.PeriodoContableService periodoService,
                                      com.pazzioliweb.comprobantesmodule.repositori.AuditoriaMantenimientoRepository auditoriaRepo) {
        this.repo = repo;
        this.tercerosRepo = tercerosRepo;
        this.asientoService = asientoService;
        this.tipoComprobanteManualService = tipoComprobanteManualService;
        this.periodoService = periodoService;
        this.auditoriaRepo = auditoriaRepo;
    }

    /** Usuario autenticado (para auditoría); "SYSTEM" si no se puede resolver.
     *  Usa la API de servlet (sin dependencia de Spring Security en este módulo). */
    private String usuarioActual(jakarta.servlet.http.HttpServletRequest request) {
        try {
            if (request != null) {
                java.security.Principal p = request.getUserPrincipal();
                if (p != null && p.getName() != null && !"anonymousUser".equals(p.getName())) {
                    String n = p.getName();
                    return n.length() <= 100 ? n : n.substring(0, 100);
                }
                String hdr = request.getHeader("X-Usuario");
                if (hdr != null && !hdr.isBlank()) return hdr.length() <= 100 ? hdr : hdr.substring(0, 100);
            }
        } catch (Exception ignored) {}
        return "SYSTEM";
    }

    /** Lanza excepción si algún mes del rango [ini,fin] está en un periodo contable cerrado. */
    private void validarRangoAbierto(LocalDate ini, LocalDate fin) {
        LocalDate cursor = ini.withDayOfMonth(1);
        LocalDate topeMes = fin.withDayOfMonth(1);
        while (!cursor.isAfter(topeMes)) {
            if (periodoService.estaCerrado(cursor)) {
                throw new IllegalStateException("El periodo " + cursor.getMonthValue() + "/" + cursor.getYear()
                        + " está CERRADO. Reábralo desde Contabilidad → Periodos contables para poder operar sobre él.");
            }
            cursor = cursor.plusMonths(1);
        }
    }

    /** Guarda un registro de auditoría (best-effort, no interrumpe la operación). */
    private void auditar(String operacion, String detalle, String usuario) {
        try {
            com.pazzioliweb.comprobantesmodule.entity.AuditoriaMantenimiento a =
                    new com.pazzioliweb.comprobantesmodule.entity.AuditoriaMantenimiento();
            a.setOperacion(operacion);
            a.setDetalle(detalle != null && detalle.length() > 500 ? detalle.substring(0, 500) : detalle);
            a.setUsuario(usuario);
            a.setFechaHora(java.time.LocalDateTime.now());
            auditoriaRepo.save(a);
        } catch (Exception ignored) {}
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
                if (li.get("documentoCruce") != null) {
                    l.documentoCruce = li.get("documentoCruce").toString();
                }
                if (li.get("centroCostoId") != null && !li.get("centroCostoId").toString().isBlank()) {
                    l.centroCostoId = Integer.parseInt(li.get("centroCostoId").toString());
                }
                lineas.add(l);
            }
            if (lineas.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ninguna línea tiene débito o crédito > 0"));
            }

            // Número del asiento: si se eligió un tipo de comprobante manual
            // (Nota de contabilidad, Depreciación...), se numera con su prefijo
            // y consecutivo (NC-1, DEP-1...). Si no, cae al genérico MAN-{timestamp}.
            Integer tipoId = null;
            String numero;
            if (body.get("tipoComprobanteManualId") != null
                    && !body.get("tipoComprobanteManualId").toString().isBlank()) {
                tipoId = Integer.parseInt(body.get("tipoComprobanteManualId").toString());
                // Peek: numera sin consumir el contador (se confirma tras el éxito).
                numero = tipoComprobanteManualService.peekSiguienteNumero(tipoId);
            } else {
                numero = "MAN-" + System.currentTimeMillis() / 1000;
            }

            AsientoContable asiento = asientoService.generarAsiento(
                    numero, fecha, descripcion, "MANUAL", null, null, lineas);

            // El asiento se creó bien: ahora sí consumimos el consecutivo del tipo
            // (evita huecos si generarAsiento hubiera fallado antes).
            if (tipoId != null) {
                tipoComprobanteManualService.siguienteNumero(tipoId);
            }

            // Sincronizar SUBLEDGER de cartera: si alguna línea toca CxC (1305) o CxP (2205) con
            // tercero, se publica un evento para que ventas/compras creen/abonen el auxiliar (el
            // dashboard lee el subledger, no el mayor). El listener corre AFTER_COMMIT en tx propia.
            List<AsientoManualRegistradoEvent.LineaCartera> lineasCartera = new ArrayList<>();
            if (asiento.getLineas() != null) {
                for (AsientoContableLinea l : asiento.getLineas()) {
                    String cod = l.getCuentaContable() != null ? l.getCuentaContable().getCodigo() : null;
                    if (cod == null) continue;
                    boolean esCartera = cod.startsWith("1305") || cod.startsWith("2205");
                    if (!esCartera || l.getTerceroId() == null) continue;
                    lineasCartera.add(new AsientoManualRegistradoEvent.LineaCartera(
                            cod, l.getTerceroId(), l.getTerceroNombre(),
                            l.getDebito(), l.getCredito(), l.getDocumentoCruce()));
                }
            }
            if (!lineasCartera.isEmpty()) {
                eventPublisher.publishEvent(new AsientoManualRegistradoEvent(
                        this, asiento.getId(), asiento.getNumeroAsiento(), fecha, lineasCartera));
            }

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

    /**
     * Saldos iniciales → asiento de apertura. Recibe una fecha y las líneas
     * (cuenta + débito/crédito ya ubicados en su naturaleza por el front).
     * Origen "APERTURA" con documentoOrigenId = año → idempotente: una sola
     * apertura por año (si ya existe una CONFIRMADA, la retorna sin duplicar).
     */
    @PostMapping("/apertura")
    @Transactional
    public ResponseEntity<Map<String, Object>> crearApertura(@RequestBody Map<String, Object> body) {
        try {
            LocalDate fecha = body.get("fecha") != null
                    ? LocalDate.parse(body.get("fecha").toString())
                    : LocalDate.now();
            long anio = fecha.getYear();
            String descripcion = body.get("descripcion") != null
                    ? body.get("descripcion").toString()
                    : "Saldos iniciales " + anio;

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> lineasIn = (List<Map<String, Object>>) body.get("lineas");
            if (lineasIn == null || lineasIn.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe registrar al menos una línea de saldo inicial"));
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
                if (li.get("terceroId") != null && !li.get("terceroId").toString().isBlank()) {
                    Integer tid = Integer.parseInt(li.get("terceroId").toString());
                    String tnombre = li.get("terceroNombre") != null ? li.get("terceroNombre").toString() : "";
                    l.conTercero(tid, tnombre);
                }
                if (li.get("documentoCruce") != null) {
                    l.documentoCruce = li.get("documentoCruce").toString();
                }
                if (li.get("centroCostoId") != null && !li.get("centroCostoId").toString().isBlank()) {
                    l.centroCostoId = Integer.parseInt(li.get("centroCostoId").toString());
                }
                lineas.add(l);
            }
            if (lineas.size() < 2) {
                return ResponseEntity.badRequest().body(Map.of("error", "El asiento de apertura debe tener al menos 2 líneas con valor"));
            }

            // Tipo propio SALDOS_INI (no APERTURA) para NO colisionar con la clave de
            // idempotencia del traslado de cierre anual, que usa ("APERTURA", año+1).
            String numero = "SALDOS-INI-" + anio;
            AsientoContable asiento = asientoService.generarAsiento(
                    numero, fecha, descripcion, "SALDOS_INI", anio, null, lineas);
            return ResponseEntity.ok(Map.of(
                    "id", asiento.getId(),
                    "numeroAsiento", asiento.getNumeroAsiento(),
                    "totalDebito", asiento.getTotalDebito(),
                    "totalCredito", asiento.getTotalCredito(),
                    "mensaje", "Asiento de apertura registrado correctamente"
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
            lm.put("documentoCruce", l.getDocumentoCruce());
            lm.put("centroCostoId", l.getCentroCostoId());
            lineas.add(lm);
        }
        m.put("lineas", lineas);
        return m;
    }

    // ══════════════════════════════════════════════════════════════
    // HERRAMIENTAS DE MANTENIMIENTO DE DOCUMENTOS
    // ══════════════════════════════════════════════════════════════

    /**
     * Duplica un asiento existente creando uno nuevo con las mismas líneas.
     * Body opcional: { fecha, descripcion }. Por defecto fecha = hoy.
     */
    @PostMapping("/{id}/duplicar")
    @Transactional
    public ResponseEntity<Map<String, Object>> duplicar(@PathVariable Long id,
                                                        @RequestBody(required = false) Map<String, Object> body,
                                                        jakarta.servlet.http.HttpServletRequest request) {
        try {
            AsientoContable orig = repo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Asiento no encontrado: " + id));
            LocalDate fecha = (body != null && body.get("fecha") != null)
                    ? LocalDate.parse(body.get("fecha").toString()) : LocalDate.now();
            String descripcion = (body != null && body.get("descripcion") != null)
                    ? body.get("descripcion").toString()
                    : ("Copia de " + orig.getNumeroAsiento()
                        + (orig.getDescripcion() != null ? " - " + orig.getDescripcion() : ""));

            List<com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO> lineas = new ArrayList<>();
            for (AsientoContableLinea l : orig.getLineas()) {
                Integer cuentaId = l.getCuentaContable() != null ? l.getCuentaContable().getId() : null;
                if (cuentaId == null) continue;
                com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO nl;
                if (l.getDebito() != null && l.getDebito().compareTo(java.math.BigDecimal.ZERO) > 0) {
                    nl = com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO
                            .debito(cuentaId, l.getDebito(), l.getDescripcion());
                } else if (l.getCredito() != null && l.getCredito().compareTo(java.math.BigDecimal.ZERO) > 0) {
                    nl = com.pazzioliweb.comprobantesmodule.service.AsientoContableService.LineaDTO
                            .credito(cuentaId, l.getCredito(), l.getDescripcion());
                } else {
                    continue;
                }
                if (l.getTerceroId() != null) nl.conTercero(l.getTerceroId(), l.getTerceroNombre());
                nl.documentoCruce = l.getDocumentoCruce();
                nl.centroCostoId = l.getCentroCostoId();
                lineas.add(nl);
            }
            if (lineas.size() < 2) {
                return ResponseEntity.badRequest().body(Map.of("error", "El asiento origen no tiene líneas válidas para duplicar."));
            }

            // Número único (evita colisión si se duplica dos veces en el mismo segundo).
            String numero = "MAN-" + System.currentTimeMillis() + "-" + orig.getId();
            AsientoContable nuevo = asientoService.generarAsiento(
                    numero, fecha, descripcion, "MANUAL", null, null, lineas);
            auditar("DUPLICAR", "Asiento " + orig.getNumeroAsiento() + " (id " + orig.getId()
                    + ") duplicado en " + nuevo.getNumeroAsiento() + " (id " + nuevo.getId() + ")", usuarioActual(request));
            return ResponseEntity.ok(Map.of(
                    "id", nuevo.getId(),
                    "numeroAsiento", nuevo.getNumeroAsiento(),
                    "mensaje", "Asiento duplicado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Anula un asiento MANUAL/contable interno (lo marca ANULADO). */
    @PostMapping("/{id}/anular")
    @Transactional
    public ResponseEntity<Map<String, Object>> anular(@PathVariable Long id,
                                                      jakarta.servlet.http.HttpServletRequest request) {
        try {
            asientoService.anularPorId(id);
            auditar("ANULAR_ASIENTO", "Asiento id " + id + " anulado", usuarioActual(request));
            return ResponseEntity.ok(Map.of("mensaje", "Asiento anulado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cambio masivo de código contable: reemplaza una cuenta por otra en las líneas
     * de los asientos CONFIRMADOS dentro de un rango de fechas.
     * Body: { cuentaOrigenId, cuentaDestinoId, fechaInicio, fechaFin }.
     */
    @PostMapping("/cambio-masivo-cuenta")
    @Transactional
    public ResponseEntity<Map<String, Object>> cambioMasivoCuenta(@RequestBody Map<String, Object> body,
                                                                  jakarta.servlet.http.HttpServletRequest request) {
        try {
            Integer origen = Integer.parseInt(body.get("cuentaOrigenId").toString());
            Integer destino = Integer.parseInt(body.get("cuentaDestinoId").toString());
            if (origen.equals(destino)) {
                return ResponseEntity.badRequest().body(Map.of("error", "La cuenta origen y destino no pueden ser iguales."));
            }
            LocalDate ini = LocalDate.parse(body.get("fechaInicio").toString());
            LocalDate fin = LocalDate.parse(body.get("fechaFin").toString());

            // No permitir tocar asientos de periodos contables cerrados.
            validarRangoAbierto(ini, fin);

            // La cuenta destino debe existir, ser de movimiento y estar ACTIVA; además, si exige
            // tercero / centro de costo / documento de cruce, las líneas reasignadas deben tenerlo
            // (mismas reglas que aplica AsientoContableService al postear). Sin esto, un cambio
            // masivo podía dejar líneas en cuentas inactivas o sin tercero → rompe informes por
            // tercero, exógena DIAN y centros de costo.
            List<?> chk = em.createNativeQuery(
                    "SELECT es_movimiento, estado, requiere_tercero, requiere_centro_costo, requiere_documento_cruce " +
                    "FROM cuentas_contables WHERE cuenta_id = ?1")
                    .setParameter(1, destino).getResultList();
            if (chk.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La cuenta destino no existe."));
            }
            Object[] row = (Object[]) chk.get(0);
            java.util.function.Predicate<Object> flag = o -> o != null
                    && (o instanceof Boolean b ? b : !"0".equals(o.toString()) && !"false".equalsIgnoreCase(o.toString()));
            boolean movimiento = row[0] == null || flag.test(row[0]);
            if (!movimiento) {
                return ResponseEntity.badRequest().body(Map.of("error", "La cuenta destino no es de movimiento (no acepta registros)."));
            }
            String estadoDestino = row[1] != null ? row[1].toString() : "ACTIVO";
            if (!"ACTIVO".equalsIgnoreCase(estadoDestino)) {
                return ResponseEntity.badRequest().body(Map.of("error", "La cuenta destino está inactiva; actívela antes de reasignarle movimientos."));
            }
            java.util.List<String> faltas = new java.util.ArrayList<>();
            if (flag.test(row[2]) && contarLineasReasignablesSin(origen, ini, fin, "l.tercero_id IS NULL") > 0)
                faltas.add("tercero");
            if (flag.test(row[3]) && contarLineasReasignablesSin(origen, ini, fin, "l.centro_costo_id IS NULL") > 0)
                faltas.add("centro de costo");
            if (flag.test(row[4]) && contarLineasReasignablesSin(origen, ini, fin, "(l.documento_cruce IS NULL OR l.documento_cruce = '')") > 0)
                faltas.add("documento de cruce");
            if (!faltas.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error",
                        "La cuenta destino exige " + String.join(", ", faltas)
                        + ", y algunas líneas a reasignar no lo tienen. Complételo antes del cambio masivo."));
            }

            int n = em.createNativeQuery(
                    "UPDATE asientos_contables_lineas l " +
                    "JOIN asientos_contables a ON a.id = l.asiento_id " +
                    "SET l.cuenta_contable_id = ?1 " +
                    "WHERE l.cuenta_contable_id = ?2 AND a.estado = 'CONFIRMADO' " +
                    "AND a.fecha BETWEEN ?3 AND ?4")
                    .setParameter(1, destino).setParameter(2, origen)
                    .setParameter(3, ini).setParameter(4, fin)
                    .executeUpdate();

            auditar("CAMBIO_MASIVO_CUENTA", "Cuenta " + origen + " → " + destino + " en [" + ini + " a " + fin
                    + "]: " + n + " línea(s) actualizada(s)", usuarioActual(request));
            return ResponseEntity.ok(Map.of(
                    "actualizados", n,
                    "mensaje", n + " línea(s) actualizada(s) de la cuenta " + origen + " a la " + destino + "."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cuenta las líneas que un cambio masivo reasignaría (cuenta origen, asientos CONFIRMADOS del
     * rango) que además cumplen la condición dada (p. ej. les falta tercero). `condicion` es un
     * literal controlado por el código (no entrada del usuario), por lo que es seguro concatenarlo.
     */
    private long contarLineasReasignablesSin(Integer origen, LocalDate ini, LocalDate fin, String condicion) {
        Object c = em.createNativeQuery(
                "SELECT COUNT(*) FROM asientos_contables_lineas l " +
                "JOIN asientos_contables a ON a.id = l.asiento_id " +
                "WHERE l.cuenta_contable_id = ?1 AND a.estado = 'CONFIRMADO' " +
                "AND a.fecha BETWEEN ?2 AND ?3 AND " + condicion)
                .setParameter(1, origen).setParameter(2, ini).setParameter(3, fin)
                .getSingleResult();
        return c == null ? 0L : Long.parseLong(c.toString());
    }

    /**
     * Renumera los asientos CONFIRMADOS de un rango de fechas de forma consecutiva.
     * Body: { fechaInicio, fechaFin, prefijo?, desde? }. Ordena por fecha e id.
     */
    @PostMapping("/renumerar")
    @Transactional
    public ResponseEntity<Map<String, Object>> renumerar(@RequestBody Map<String, Object> body,
                                                         jakarta.servlet.http.HttpServletRequest request) {
        try {
            LocalDate ini = LocalDate.parse(body.get("fechaInicio").toString());
            LocalDate fin = LocalDate.parse(body.get("fechaFin").toString());
            String prefijo = body.get("prefijo") != null ? body.get("prefijo").toString() : "";
            int desde = body.get("desde") != null ? Integer.parseInt(body.get("desde").toString()) : 1;
            // Segmentación por tipo de documento (FC, VC, CC, CR, RC, CE, DV, MANUAL, DEP...).
            // Evita mezclar consecutivos de distintos tipos / fiscales.
            String tipo = body.get("tipo") != null && !body.get("tipo").toString().isBlank()
                    ? body.get("tipo").toString().trim() : null;

            // No renumerar dentro de periodos cerrados.
            validarRangoAbierto(ini, fin);

            String jpql = "SELECT a FROM AsientoContable a WHERE a.estado = 'CONFIRMADO' "
                    + "AND a.fecha BETWEEN :ini AND :fin "
                    + (tipo != null ? "AND a.documentoOrigenTipo = :tipo " : "")
                    + "ORDER BY a.fecha ASC, a.id ASC";
            var query = em.createQuery(jpql, AsientoContable.class)
                    .setParameter("ini", ini).setParameter("fin", fin);
            if (tipo != null) query.setParameter("tipo", tipo);
            List<AsientoContable> asientos = query.getResultList();

            int consec = desde;
            for (AsientoContable a : asientos) {
                a.setNumeroAsiento(prefijo + consec);
                repo.save(a);
                consec++;
            }
            auditar("RENUMERAR", "Rango [" + ini + " a " + fin + "]"
                    + (tipo != null ? " tipo " + tipo : " (todos)")
                    + ": " + asientos.size() + " asiento(s) desde " + prefijo + desde, usuarioActual(request));
            return ResponseEntity.ok(Map.of(
                    "renumerados", asientos.size(),
                    "mensaje", asientos.size() + " asiento(s) renumerado(s) desde " + prefijo + desde + "."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
