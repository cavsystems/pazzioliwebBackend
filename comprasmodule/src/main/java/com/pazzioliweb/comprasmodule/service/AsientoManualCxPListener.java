package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.commonbacken.events.AsientoManualRegistradoEvent;
import com.pazzioliweb.commonbacken.events.AsientoManualRegistradoEvent.LineaCartera;
import com.pazzioliweb.comprasmodule.entity.CuentaPorPagar;
import com.pazzioliweb.comprasmodule.repository.CuentaPorPagarRepository;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Sincroniza el subledger de CUENTAS POR PAGAR (2205) cuando se registra un asiento MANUAL que
 * toca esa cuenta con un tercero. El dashboard lee el subledger, no el mayor.
 *
 * Reglas (conservadoras, falla segura) — simétricas a CxC:
 *  - CRÉDITO a 2205 → NUEVA cuenta por pagar (numeroFactura = documento de cruce, o el Nº del asiento).
 *                     Si ya existe una CxP con ese número (no anulada), se omite (no duplica).
 *  - DÉBITO a 2205  → ABONO/pago: baja el saldo de la(s) CxP cuyo numeroFactura = documento de cruce.
 *                     Si no hay cruce o no se encuentra, se omite (nunca crea saldos negativos).
 *
 * Corre AFTER_COMMIT en transacción propia.
 */
@Component
public class AsientoManualCxPListener {

    private static final Logger log = LoggerFactory.getLogger(AsientoManualCxPListener.class);

    private final CuentaPorPagarRepository cxpRepo;
    private final TercerosRepository tercerosRepo;

    public AsientoManualCxPListener(CuentaPorPagarRepository cxpRepo, TercerosRepository tercerosRepo) {
        this.cxpRepo = cxpRepo;
        this.tercerosRepo = tercerosRepo;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAsientoManual(AsientoManualRegistradoEvent ev) {
        if (ev.getLineas() == null) return;
        for (LineaCartera l : ev.getLineas()) {
            String cod = l.getCuentaCodigo();
            if (cod == null || !cod.startsWith("2205") || l.getTerceroId() == null) continue;
            try {
                BigDecimal deb = l.getDebito() != null ? l.getDebito() : BigDecimal.ZERO;
                BigDecimal cre = l.getCredito() != null ? l.getCredito() : BigDecimal.ZERO;
                if (cre.compareTo(BigDecimal.ZERO) > 0) {
                    crearCxP(ev, l, cre);
                } else if (deb.compareTo(BigDecimal.ZERO) > 0) {
                    abonarCxP(l, deb);
                }
            } catch (Exception e) {
                log.warn("[AsientoManualCxP] No se pudo sincronizar la línea (cuenta {}, tercero {}): {}",
                        cod, l.getTerceroId(), e.getMessage());
            }
        }
    }

    private void crearCxP(AsientoManualRegistradoEvent ev, LineaCartera l, BigDecimal monto) {
        String numero = (l.getDocumentoCruce() != null && !l.getDocumentoCruce().isBlank())
                ? l.getDocumentoCruce().trim() : ev.getNumeroAsiento();
        boolean yaExiste = cxpRepo.findByNumeroFactura(numero).stream()
                .anyMatch(c -> !"ANULADA".equalsIgnoreCase(c.getEstado()));
        if (yaExiste) {
            log.info("[AsientoManualCxP] Ya existe una CxP con número {} — no se duplica.", numero);
            return;
        }
        Terceros t = tercerosRepo.findByTerceroId(l.getTerceroId()).orElse(null);
        if (t == null) { log.warn("[AsientoManualCxP] Tercero {} no encontrado.", l.getTerceroId()); return; }

        int plazo = (t.getPlazo() != null && t.getPlazo() > 0) ? t.getPlazo() : 30;
        LocalDate fecha = ev.getFecha() != null ? ev.getFecha() : LocalDate.now();
        String nombre = (t.getRazonSocial() != null && !t.getRazonSocial().isBlank())
                ? t.getRazonSocial()
                : (t.getNombre1() != null ? t.getNombre1() : (l.getTerceroNombre() != null ? l.getTerceroNombre() : ""));

        CuentaPorPagar cxp = new CuentaPorPagar();
        cxp.setNit(t.getIdentificacion());
        cxp.setNombre(nombre);
        cxp.setNumeroFactura(numero);
        cxp.setValorNeto(monto);
        cxp.setSaldo(monto);
        cxp.setFechaVencimiento(fecha.plusDays(plazo));
        cxp.setEstado("PENDIENTE");
        cxp.setProveedor(t);
        cxpRepo.save(cxp);
        log.info("[AsientoManualCxP] CxP creada {} por {} (tercero {}).", numero, monto, l.getTerceroId());
    }

    private void abonarCxP(LineaCartera l, BigDecimal abono) {
        if (l.getDocumentoCruce() == null || l.getDocumentoCruce().isBlank()) {
            log.info("[AsientoManualCxP] Débito a 2205 sin documento de cruce — no se puede aplicar; se omite.");
            return;
        }
        List<CuentaPorPagar> cxps = cxpRepo.findByNumeroFactura(l.getDocumentoCruce().trim());
        BigDecimal restante = abono;
        boolean aplicado = false;
        for (CuentaPorPagar cxp : cxps) {
            if (restante.compareTo(BigDecimal.ZERO) <= 0) break;
            if ("ANULADA".equalsIgnoreCase(cxp.getEstado())) continue;
            BigDecimal saldo = cxp.getSaldo() != null ? cxp.getSaldo() : BigDecimal.ZERO;
            if (saldo.compareTo(BigDecimal.ZERO) <= 0) continue;
            BigDecimal aplicar = saldo.min(restante);
            BigDecimal nuevo = saldo.subtract(aplicar);
            cxp.setSaldo(nuevo);
            cxp.setEstado(nuevo.compareTo(BigDecimal.ZERO) <= 0 ? "PAGADA" : "PARCIAL");
            cxpRepo.save(cxp);
            restante = restante.subtract(aplicar);
            aplicado = true;
        }
        if (!aplicado) {
            log.info("[AsientoManualCxP] No se encontró CxP abierta con número {} para abonar.", l.getDocumentoCruce());
        }
    }
}
