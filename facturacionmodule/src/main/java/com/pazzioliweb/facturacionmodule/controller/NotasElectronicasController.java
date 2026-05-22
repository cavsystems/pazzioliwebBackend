package com.pazzioliweb.facturacionmodule.controller;

import com.pazzioliweb.commonbacken.events.DevolucionRegistradaEvent;
import com.pazzioliweb.facturacionmodule.entity.DocumentoElectronico;
import com.pazzioliweb.facturacionmodule.entity.Facturas;
import com.pazzioliweb.facturacionmodule.repositori.DocumentoElectronicoRepository;
import com.pazzioliweb.facturacionmodule.repositori.FacturasRepository;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import com.pazzioliweb.ventasmodule.entity.Devolucion;
import com.pazzioliweb.ventasmodule.entity.Venta;
import com.pazzioliweb.ventasmodule.repository.DevolucionRepository;
import com.pazzioliweb.ventasmodule.repository.VentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoints manuales para gestión de Notas Crédito y Notas Débito Electrónicas.
 * El flujo automático ocurre vía eventos (ver DevolucionRegistradaListener),
 * pero estos endpoints permiten reenviar/regenerar manualmente si falla.
 */
@RestController
@RequestMapping("/api/notas-electronicas")
public class NotasElectronicasController {

    private static final Logger log = LoggerFactory.getLogger(NotasElectronicasController.class);

    private final DevolucionRepository devolucionRepository;
    private final DocumentoElectronicoRepository docElectRepository;
    private final FacturasRepository facturasRepository;
    private final TercerosRepository tercerosRepository;
    private final VentaRepository ventaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public NotasElectronicasController(DevolucionRepository devolucionRepository,
                                        DocumentoElectronicoRepository docElectRepository,
                                        FacturasRepository facturasRepository,
                                        TercerosRepository tercerosRepository,
                                        VentaRepository ventaRepository,
                                        ApplicationEventPublisher eventPublisher) {
        this.devolucionRepository = devolucionRepository;
        this.docElectRepository = docElectRepository;
        this.facturasRepository = facturasRepository;
        this.tercerosRepository = tercerosRepository;
        this.ventaRepository = ventaRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Lista todas las Notas Crédito generadas (a partir de devoluciones)
     * con filtros opcionales por fechas y estado DIAN.
     * GET /api/notas-electronicas/listar
     */
    @GetMapping("/listar")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> listarNotas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String estadoDian,
            @RequestParam(required = false) String tipo) {     // NC / ND

        List<Map<String, Object>> out = new ArrayList<>();
        // ── Notas Crédito desde devoluciones ──
        if (tipo == null || "NC".equalsIgnoreCase(tipo)) {
            List<Devolucion> devs = devolucionRepository.findAll();
            for (Devolucion d : devs) {
                if (d.getNumeroNc() == null) continue;
                if (desde != null && d.getFechaCreacion() != null && d.getFechaCreacion().isBefore(desde)) continue;
                if (hasta != null && d.getFechaCreacion() != null && d.getFechaCreacion().isAfter(hasta)) continue;
                if (estadoDian != null && !estadoDian.equalsIgnoreCase(d.getEstadoDianNc())) continue;

                Map<String, Object> row = new HashMap<>();
                row.put("tipo", "NC");
                row.put("numero", d.getNumeroNc());
                row.put("cufe", d.getCufeNc());
                row.put("estadoDian", d.getEstadoDianNc());
                row.put("mensajeDian", d.getMensajeDianNc());
                row.put("qrData", d.getQrDataNc());
                row.put("fecha", d.getFechaCreacion());
                row.put("totalNeto", d.getTotalNeto());
                row.put("motivo", d.getMotivo());
                row.put("devolucionId", d.getId());
                row.put("numeroDevolucion", d.getNumeroDevolucion());
                try {
                    if (d.getVenta() != null) {
                        row.put("numeroVentaOrigen", d.getVenta().getNumeroVenta());
                        if (d.getVenta().getCliente() != null) {
                            String razon = d.getVenta().getCliente().getRazonSocial();
                            row.put("clienteNombre",
                                    (razon != null && !razon.isBlank())
                                        ? razon
                                        : d.getVenta().getCliente().getNombre1());
                            row.put("clienteId", d.getVenta().getCliente().getIdentificacion());
                        }
                    }
                } catch (Exception ex) {
                    // Si la venta/cliente no está accesible (lazy init), dejamos los campos vacíos
                    // pero no rompemos todo el listado.
                }
                out.add(row);
            }
        }
        // ── Notas Débito, Tiquetes POS y Documentos Soporte desde documentos_electronicos ──
        String tipoLower = tipo == null ? null : tipo.toUpperCase();
        if (tipoLower == null || java.util.Arrays.asList("ND", "TPOS", "DS").contains(tipoLower)) {
            List<DocumentoElectronico> docs = docElectRepository.findFiltrado(
                    tipoLower != null && !"NC".equals(tipoLower) ? tipoLower : null,
                    estadoDian, desde, hasta);
            for (DocumentoElectronico d : docs) {
                Map<String, Object> row = new HashMap<>();
                row.put("tipo", d.getTipo());
                row.put("numero", d.getNumero());
                row.put("cufe", d.getCufe());
                row.put("estadoDian", d.getEstadoDian());
                row.put("mensajeDian", d.getMensajeDian());
                row.put("qrData", d.getQrData());
                row.put("fecha", d.getFechaEmision());
                row.put("totalNeto", d.getTotal());
                row.put("motivo", d.getConcepto());
                row.put("clienteNombre", d.getTerceroNombre());
                row.put("clienteId", d.getTerceroIdentificacion());
                // Referencia al origen — útil para ver desde qué venta/factura salió
                if (d.getDocumentoReferenciaTipo() != null && d.getDocumentoReferenciaId() != null) {
                    row.put("documentoReferenciaTipo", d.getDocumentoReferenciaTipo());
                    row.put("documentoReferenciaId", d.getDocumentoReferenciaId());
                }
                row.put("documentoElectronicoId", d.getId());  // para reenvío en el futuro
                out.add(row);
            }
        }

        // ── Facturas electrónicas FC/VC desde tabla facturas ──
        String tipoUpper = tipo == null ? null : tipo.toUpperCase();
        if (tipoUpper == null || java.util.Arrays.asList("FC", "VC", "FE").contains(tipoUpper)) {
            try {
                List<Facturas> facturas = facturasRepository.findAll();
                for (Facturas f : facturas) {
                    if (f.getCufe() == null) continue; // solo facturas electrónicas con CUFE
                    if (desde != null && f.getFechaEmision() != null && f.getFechaEmision().isBefore(desde)) continue;
                    if (hasta != null && f.getFechaEmision() != null && f.getFechaEmision().isAfter(hasta)) continue;
                    String estadoStr = f.getEstadoDian() != null ? f.getEstadoDian().name() : null;
                    if (estadoDian != null && !estadoDian.equalsIgnoreCase(estadoStr)) continue;

                    // ── Determinar FC (contado) vs VC (crédito) cruzando con la venta origen ──
                    // El comprobante_id de la factura siempre apunta al legacy/electrónico,
                    // así que tomamos el comprobante real de la venta vinculada.
                    String tipoReal = "FC"; // default
                    try {
                        if (f.getVentaId() != null) {
                            Venta venta = ventaRepository.findById(f.getVentaId()).orElse(null);
                            if (venta != null && venta.getComprobante() != null
                                    && venta.getComprobante().getTipoMovimiento() != null) {
                                String tm = venta.getComprobante().getTipoMovimiento().name();
                                if ("VC".equals(tm) || "FC".equals(tm)) {
                                    tipoReal = tm;
                                }
                            }
                        }
                    } catch (Exception ignore) { }

                    // Aplicar filtro por tipo si el usuario lo solicitó (FC o VC explícito)
                    if (("FC".equals(tipoUpper) || "VC".equals(tipoUpper)) && !tipoUpper.equals(tipoReal)) {
                        continue;
                    }

                    Map<String, Object> row = new HashMap<>();
                    row.put("tipo", tipoReal);
                    row.put("numero", f.getNumeroFactura());
                    row.put("cufe", f.getCufe());
                    row.put("estadoDian", estadoStr);
                    row.put("mensajeDian", f.getMensajeDian());
                    row.put("qrData", f.getQrData());
                    row.put("fecha", f.getFechaEmision());
                    row.put("totalNeto", f.getTotalFactura());
                    row.put("motivo", "VC".equals(tipoReal)
                            ? "Factura electrónica de venta a crédito"
                            : "Factura electrónica de venta de contado");
                    if (f.getVentaId() != null) {
                        row.put("documentoReferenciaTipo", "VENTA");
                        row.put("documentoReferenciaId", f.getVentaId());
                    }
                    // Lookup del cliente por terceroId
                    if (f.getTerceroId() != null) {
                        try {
                            Terceros t = tercerosRepository.findById(f.getTerceroId()).orElse(null);
                            if (t != null) {
                                String razon = t.getRazonSocial();
                                String nombreCompleto;
                                if (razon != null && !razon.isBlank()) {
                                    nombreCompleto = razon;
                                } else {
                                    StringBuilder sb = new StringBuilder();
                                    if (t.getNombre1() != null) sb.append(t.getNombre1()).append(" ");
                                    if (t.getApellido1() != null) sb.append(t.getApellido1());
                                    nombreCompleto = sb.toString().trim();
                                }
                                row.put("clienteNombre", nombreCompleto);
                                row.put("clienteId", t.getIdentificacion());
                            }
                        } catch (Exception ex) {
                            // No rompemos el listado si falla un cliente individual
                        }
                    }
                    row.put("facturaId", f.getFacturaId());
                    out.add(row);
                }
            } catch (Exception e) {
                log.warn("Error cargando facturas: {}", e.getMessage());
            }
        }

        // Ordenar por fecha desc
        out.sort((a, b) -> {
            Object fa = a.get("fecha"), fb = b.get("fecha");
            if (fa == null) return 1;
            if (fb == null) return -1;
            return ((LocalDate) fb).compareTo((LocalDate) fa);
        });
        return ResponseEntity.ok(out);
    }

    /**
     * Consulta el estado DIAN de la NC asociada a una devolución.
     * GET /api/notas-electronicas/devolucion/{devolucionId}
     */
    @GetMapping("/devolucion/{devolucionId}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> obtenerNCDeDevolucion(@PathVariable Long devolucionId) {
        Devolucion d = devolucionRepository.findById(devolucionId).orElse(null);
        if (d == null) return ResponseEntity.notFound().build();
        Map<String, Object> resp = new HashMap<>();
        resp.put("devolucionId", d.getId());
        resp.put("numeroDevolucion", d.getNumeroDevolucion());
        resp.put("numeroNc", d.getNumeroNc());
        resp.put("cufeNc", d.getCufeNc());
        resp.put("estadoDianNc", d.getEstadoDianNc());
        resp.put("mensajeDianNc", d.getMensajeDianNc());
        resp.put("qrDataNc", d.getQrDataNc());
        return ResponseEntity.ok(resp);
    }

    /**
     * Reenvía/regenera la NC para una devolución (útil si el envío inicial falló).
     * POST /api/notas-electronicas/devolucion/{devolucionId}/reenviar
     * Optional ?codigoConcepto=1   (1=Devolución, 2=Anulación, 3=Rebaja, 4=Descuento, 5=Otro)
     */
    @PostMapping("/devolucion/{devolucionId}/reenviar")
    public ResponseEntity<Map<String, Object>> reenviarNC(@PathVariable Long devolucionId,
                                                           @RequestParam(required = false, defaultValue = "1") Integer codigoConcepto) {
        Devolucion d = devolucionRepository.findById(devolucionId).orElse(null);
        if (d == null) return ResponseEntity.notFound().build();
        try {
            Integer cajeroId = d.getCajero() != null ? d.getCajero().getCajeroId() : null;
            Long ventaId = d.getVenta() != null ? d.getVenta().getId() : null;
            // Publica el mismo evento — el listener lo procesará igual que el automático
            eventPublisher.publishEvent(new DevolucionRegistradaEvent(
                    this, d.getId(), ventaId, cajeroId, codigoConcepto));
            log.info("Reenvío de NC solicitado para devolución {}", devolucionId);
            return ResponseEntity.ok(Map.of(
                "ok", true,
                "mensaje", "Reenvío de NC encolado. Consulte estado en unos segundos."
            ));
        } catch (Exception e) {
            log.error("Error reenviando NC: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("ok", false, "mensaje", e.getMessage()));
        }
    }
}
