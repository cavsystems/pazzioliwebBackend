package com.pazzioliweb.ventasmodule.service;

import com.pazzioliweb.commonbacken.events.AsientoManualRegistradoEvent;
import com.pazzioliweb.commonbacken.events.AsientoManualRegistradoEvent.LineaCartera;
import com.pazzioliweb.ventasmodule.entity.CuentaPorCobrar;
import com.pazzioliweb.ventasmodule.repository.CuentaPorCobrarRepository;
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
 * Sincroniza el subledger de CUENTAS POR COBRAR (1305) cuando se registra un asiento MANUAL que
 * toca esa cuenta con un tercero. El dashboard/reportes de cartera leen el subledger, no el mayor,
 * así que sin esto un asiento manual a 1305 no se vería reflejado.
 *
 * Reglas (conservadoras, falla segura):
 *  - DÉBITO a 1305  → NUEVA cuenta por cobrar (numeroVenta = documento de cruce, o el Nº del asiento).
 *                     Si ya existe una CxC con ese número (no anulada), se omite (no duplica).
 *  - CRÉDITO a 1305 → ABONO: baja el saldo de la(s) CxC cuyo numeroVenta = documento de cruce.
 *                     Si no hay cruce o no se encuentra, se omite (nunca crea saldos negativos).
 *
 * Corre AFTER_COMMIT en transacción propia: si algo falla, no afecta el asiento ya creado.
 */
@Component
public class AsientoManualCxCListener {

    private static final Logger log = LoggerFactory.getLogger(AsientoManualCxCListener.class);

    private final CuentaPorCobrarRepository cxcRepo;
    private final TercerosRepository tercerosRepo;

    public AsientoManualCxCListener(CuentaPorCobrarRepository cxcRepo, TercerosRepository tercerosRepo) {
        this.cxcRepo = cxcRepo;
        this.tercerosRepo = tercerosRepo;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAsientoManual(AsientoManualRegistradoEvent ev) {
        if (ev.getLineas() == null) return;
        for (LineaCartera l : ev.getLineas()) {
            String cod = l.getCuentaCodigo();
            if (cod == null || !cod.startsWith("1305") || l.getTerceroId() == null) continue;
            try {
                BigDecimal deb = l.getDebito() != null ? l.getDebito() : BigDecimal.ZERO;
                BigDecimal cre = l.getCredito() != null ? l.getCredito() : BigDecimal.ZERO;
                if (deb.compareTo(BigDecimal.ZERO) > 0) {
                    crearCxC(ev, l, deb);
                } else if (cre.compareTo(BigDecimal.ZERO) > 0) {
                    abonarCxC(l, cre);
                }
            } catch (Exception e) {
                log.warn("[AsientoManualCxC] No se pudo sincronizar la línea (cuenta {}, tercero {}): {}",
                        cod, l.getTerceroId(), e.getMessage());
            }
        }
    }

    private void crearCxC(AsientoManualRegistradoEvent ev, LineaCartera l, BigDecimal monto) {
        String numero = (l.getDocumentoCruce() != null && !l.getDocumentoCruce().isBlank())
                ? l.getDocumentoCruce().trim() : ev.getNumeroAsiento();
        boolean yaExiste = cxcRepo.findByNumeroVenta(numero).stream()
                .anyMatch(c -> !"ANULADA".equalsIgnoreCase(c.getEstado()));
        if (yaExiste) {
            log.info("[AsientoManualCxC] Ya existe una CxC con número {} — no se duplica.", numero);
            return;
        }
        Terceros t = tercerosRepo.findByTerceroId(l.getTerceroId()).orElse(null);
        if (t == null) { log.warn("[AsientoManualCxC] Tercero {} no encontrado.", l.getTerceroId()); return; }

        int plazo = (t.getPlazo() != null && t.getPlazo() > 0) ? t.getPlazo() : 30;
        LocalDate fecha = ev.getFecha() != null ? ev.getFecha() : LocalDate.now();
        String nombre = (t.getRazonSocial() != null && !t.getRazonSocial().isBlank())
                ? t.getRazonSocial()
                : (t.getNombre1() != null ? t.getNombre1() : (l.getTerceroNombre() != null ? l.getTerceroNombre() : ""));

        CuentaPorCobrar cxc = new CuentaPorCobrar();
        cxc.setCliente(t);
        cxc.setNit(t.getIdentificacion());
        cxc.setNombre(nombre);
        cxc.setNumeroVenta(numero);
        cxc.setValorNeto(monto);
        cxc.setSaldo(monto);
        cxc.setFechaEmision(fecha);
        cxc.setFechaVencimiento(fecha.plusDays(plazo));
        cxc.setPlazoDias(plazo);
        cxc.setEstado("PENDIENTE");
        cxcRepo.save(cxc);
        log.info("[AsientoManualCxC] CxC creada {} por {} (tercero {}).", numero, monto, l.getTerceroId());
    }

    private void abonarCxC(LineaCartera l, BigDecimal abono) {
        if (l.getDocumentoCruce() == null || l.getDocumentoCruce().isBlank()) {
            log.info("[AsientoManualCxC] Crédito a 1305 sin documento de cruce — no se puede aplicar abono; se omite.");
            return;
        }
        List<CuentaPorCobrar> cxcs = cxcRepo.findByNumeroVenta(l.getDocumentoCruce().trim());
        BigDecimal restante = abono;
        boolean aplicado = false;
        for (CuentaPorCobrar cxc : cxcs) {
            if (restante.compareTo(BigDecimal.ZERO) <= 0) break;
            if ("ANULADA".equalsIgnoreCase(cxc.getEstado())) continue;
            BigDecimal saldo = cxc.getSaldo() != null ? cxc.getSaldo() : BigDecimal.ZERO;
            if (saldo.compareTo(BigDecimal.ZERO) <= 0) continue;
            BigDecimal aplicar = saldo.min(restante);
            BigDecimal nuevo = saldo.subtract(aplicar);
            cxc.setSaldo(nuevo);
            cxc.setEstado(nuevo.compareTo(BigDecimal.ZERO) <= 0 ? "PAGADA" : "PARCIAL");
            cxcRepo.save(cxc);
            restante = restante.subtract(aplicar);
            aplicado = true;
        }
        if (!aplicado) {
            log.info("[AsientoManualCxC] No se encontró CxC abierta con número {} para abonar.", l.getDocumentoCruce());
        }
    }
}
