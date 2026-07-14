package com.pazzioliweb.tercerosmodule.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SaldoWebSocketBroadcastService {

    private static final Logger log = LoggerFactory.getLogger(SaldoWebSocketBroadcastService.class);

    private static final String TOPIC = "/topic/saldo-actualizado";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notificarSaldoActualizado(Integer terceroId, Double nuevoSaldo) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("terceroId", terceroId);
            payload.put("saldo", nuevoSaldo);
            payload.put("timestamp", System.currentTimeMillis());
            // Incluir el tenant/schema: el terceroId es por schema, así que el front debe filtrar
            // por tenant además del terceroId (evita que el saldo del tercero #N del tenant A
            // actualice la pantalla del tenant B que ve su propio tercero #N).
            payload.put("tenant", com.pazzioliweb.commonbacken.conexiondb.TenantContext.getCurrentTenant());

            messagingTemplate.convertAndSend(TOPIC, payload);
            log.info("[WS-Broadcast] Saldo actualizado para tercero {} -> {}", terceroId, nuevoSaldo);
        } catch (Exception ex) {
            log.warn("[WS-Broadcast] Error al enviar saldo actualizado para tercero {}: {}", terceroId, ex.getMessage());
        }
    }
}
