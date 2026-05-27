package com.pazzioliweb.facturacionmodule.service;

import com.pazzioliweb.commonbacken.events.VentaCompletadaEvent;
import com.pazzioliweb.facturacionmodule.dtos.FacturaElectronicaResponseDTO;
import com.pazzioliweb.facturacionmodule.dtos.GenerarFacturaRequestDTO;
import com.pazzioliweb.ventasmodule.entity.Venta;
import com.pazzioliweb.ventasmodule.repository.VentaRepository;
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
    private final VentaRepository ventaRepository;

    public VentaCompletadaListener(FacturacionElectronicaService facturacionService,
                                   VentaRepository ventaRepository) {
        this.facturacionService = facturacionService;
        this.ventaRepository = ventaRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onVentaCompletada(VentaCompletadaEvent event) {
        log.info("══════ Evento VentaCompletada recibido ══════");
        log.info("Venta ID: {}, Cajero ID: {}", event.getVentaId(), event.getCajeroId());

        try {
            // Buscamos el comprobante_id real de la venta para no depender de un id
            // hardcodeado (que puede no existir en la BD desplegada).
            Integer comprobanteIdReal = null;
            Venta venta = ventaRepository.findById(event.getVentaId()).orElse(null);
            if (venta != null && venta.getComprobante() != null && venta.getComprobante().getId() != null) {
                comprobanteIdReal = venta.getComprobante().getId().intValue();
            }
            if (comprobanteIdReal == null) {
                log.error("No se pudo determinar el comprobante de la venta {}. Abortando facturación electrónica.",
                        event.getVentaId());
                return;
            }

            GenerarFacturaRequestDTO request = new GenerarFacturaRequestDTO();
            request.setVentaId(event.getVentaId());
            request.setComprobanteId(comprobanteIdReal);
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
