package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.commonbacken.events.FacturaAutorizadaEvent;
import com.pazzioliweb.comprobantesmodule.entity.AsientoContable;
import com.pazzioliweb.comprobantesmodule.repositori.AsientoContableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

/**
 * Escucha el evento {@link FacturaAutorizadaEvent} disparado por
 * facturacionmodule cuando la DIAN responde a una factura. Actualiza
 * el asiento contable de la venta para reflejar el estado DIAN, CUFE,
 * mensaje y fecha de autorización.
 *
 * Usa @TransactionalEventListener AFTER_COMMIT para asegurar que el
 * asiento ya esté persistido cuando llegue el evento.
 */
@Component
public class FacturaAutorizadaListener {

    private static final Logger log = LoggerFactory.getLogger(FacturaAutorizadaListener.class);

    private final AsientoContableRepository asientoRepo;

    public FacturaAutorizadaListener(AsientoContableRepository asientoRepo) {
        this.asientoRepo = asientoRepo;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional
    public void onFacturaAutorizada(FacturaAutorizadaEvent event) {
        if (event.getVentaId() == null) {
            log.warn("[FacturaAutorizada] Evento sin ventaId, ignorando.");
            return;
        }

        // El asiento se identifica por (documento_origen_tipo, documento_origen_id).
        // Las ventas generan asientos con tipo FC o VC. Buscamos ambos.
        Optional<AsientoContable> asiento = asientoRepo.findByDocumentoOrigenTipoAndDocumentoOrigenId("FC", event.getVentaId());
        if (asiento.isEmpty()) {
            asiento = asientoRepo.findByDocumentoOrigenTipoAndDocumentoOrigenId("VC", event.getVentaId());
        }

        if (asiento.isEmpty()) {
            log.info("[FacturaAutorizada] No se encontró asiento contable para venta {} — quizá la venta no tiene asiento aún. CUFE no aplicado.",
                     event.getVentaId());
            return;
        }

        AsientoContable a = asiento.get();
        a.setEstadoDian(event.getEstadoDian());
        a.setCufe(event.getCufe());
        a.setMensajeDian(event.getMensajeDian());
        a.setFechaAutorizacionDian(event.getFechaAutorizacion());
        asientoRepo.save(a);

        log.info("[FacturaAutorizada] Asiento {} de venta {} actualizado → estado DIAN: {}, CUFE: {}",
                 a.getNumeroAsiento(), event.getVentaId(), event.getEstadoDian(),
                 event.getCufe() != null ? event.getCufe().substring(0, Math.min(20, event.getCufe().length())) + "..." : "null");
    }
}
