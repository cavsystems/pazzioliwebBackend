package com.pazzioliweb.facturacionmodule.service;

import com.pazzioliweb.commonbacken.events.VentaCompletadaEvent;
import com.pazzioliweb.facturacionmodule.dtos.FacturaElectronicaResponseDTO;
import com.pazzioliweb.facturacionmodule.dtos.GenerarFacturaRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Escucha el evento VentaCompletadaEvent y genera automáticamente
 * la factura electrónica enviándola a la DIAN.
 */
@Component
public class VentaCompletadaListener {

    private static final Logger log = LoggerFactory.getLogger(VentaCompletadaListener.class);

    private final FacturacionElectronicaService facturacionService;

    public VentaCompletadaListener(FacturacionElectronicaService facturacionService) {
        this.facturacionService = facturacionService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onVentaCompletada(VentaCompletadaEvent event) {
        log.info("══════ Evento VentaCompletada recibido ══════");
        log.info("Venta ID: {}, Cajero ID: {}", event.getVentaId(), event.getCajeroId());

        try {
            GenerarFacturaRequestDTO request = new GenerarFacturaRequestDTO();
            request.setVentaId(event.getVentaId());
            request.setComprobanteId(1); // Comprobante tipo Factura de Venta
            request.setCajaId(event.getCajeroId());

            FacturaElectronicaResponseDTO response = facturacionService.generarDesdeVenta(request);

            log.info("Factura electrónica generada: {} - Estado DIAN: {} - CUFE: {}",
                    response.getNumeroFactura(),
                    response.getEstadoDian(),
                    response.getCufe());

        } catch (Exception e) {
            // No lanzar excepción para no afectar la transacción de la venta
            log.error("Error generando factura electrónica para venta {}: {}",
                    event.getVentaId(), e.getMessage());
            if (e.getCause() != null) {
                log.error("Causa raíz: {}", e.getCause().getMessage());
            }
        }
    }
}
