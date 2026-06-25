package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.commonbacken.events.ConsecutivoIncrementadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Escucha {@link ConsecutivoIncrementadoEvent} (publicado tras incrementar consecutivo)
 * y hace broadcast WebSocket a todos los clientes suscritos en
 * /topic/consecutivo-incrementado con un booleano indicando que el consecutivo aumentó.
 *
 * Se ejecuta AFTER_COMMIT para garantizar que el consecutivo ya quedó guardado
 * en base de datos antes de notificar al frontend.
 */
@Component
public class ConsecutivoIncrementadoWebSocketListener {

    private static final Logger log = LoggerFactory.getLogger(ConsecutivoIncrementadoWebSocketListener.class);

    private static final String TOPIC = "/topic/consecutivo-incrementado";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onConsecutivoIncrementado(ConsecutivoIncrementadoEvent event) {
        try {
            // Crear payload con el booleano y datos adicionales
            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("consecutivoAumentado", event.getConsecutivoAumentado());
            payload.put("comprobanteId", event.getComprobanteId());
            payload.put("tipoMovimiento", event.getTipoMovimiento());
            payload.put("nuevoConsecutivo", event.getNuevoConsecutivo());
            
            messagingTemplate.convertAndSend(TOPIC, payload);
            log.info("[WS-Consecutivo] Consecutivo incrementado para comprobante {} -> {} (tipo: {})",
                    event.getComprobanteId(), event.getNuevoConsecutivo(), event.getTipoMovimiento());
        } catch (Exception ex) {
            log.warn("[WS-Consecutivo] Error al enviar notificación de consecutivo incrementado: {}", ex.getMessage());
        }
    }
}
