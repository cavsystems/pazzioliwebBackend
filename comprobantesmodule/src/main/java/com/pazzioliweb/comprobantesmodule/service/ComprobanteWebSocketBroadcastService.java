package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.commonbacken.events.MovimientoRegistradoEvent;
import com.pazzioliweb.comprobantesmodule.dtos.ComprobanteContableDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Escucha {@link MovimientoRegistradoEvent} (publicado tras cualquier movimiento)
 * y hace broadcast WebSocket a todos los clientes suscritos en
 * /topic/comprobante-actualizado con el DTO actualizado del comprobante.
 *
 * Se ejecuta AFTER_COMMIT para garantizar que el consecutivo ya quedó guardado
 * en base de datos antes de notificar al frontend.
 */
@Component
public class ComprobanteWebSocketBroadcastService {

    private static final Logger log = LoggerFactory.getLogger(ComprobanteWebSocketBroadcastService.class);

    private static final String TOPIC = "/topic/comprobante-actualizado";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ComprobanteContableService comprobanteService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMovimientoRegistrado(MovimientoRegistradoEvent event) {
        if (event.getComprobanteId() == null) return;
        try {
            ComprobanteContableDTO dto = comprobanteService.obtener(event.getComprobanteId());
            messagingTemplate.convertAndSend(TOPIC, dto);
            log.info("[WS-Broadcast] Comprobante {} actualizado -> {}", event.getComprobanteId(), TOPIC);
        } catch (Exception ex) {
            log.warn("[WS-Broadcast] Error al enviar comprobante {}: {}", event.getComprobanteId(), ex.getMessage());
        }
    }
}
