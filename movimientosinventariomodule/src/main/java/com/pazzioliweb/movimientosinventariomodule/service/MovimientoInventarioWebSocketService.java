package com.pazzioliweb.movimientosinventariomodule.service;

import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioProgresoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MovimientoInventarioWebSocketService {

    private static final Logger log = LoggerFactory.getLogger(MovimientoInventarioWebSocketService.class);
    private static final String TOPIC = "/topic/movimiento-inventario-progreso";
    // Topic aparte para "ver detalle" (GET /{id}): así no se mezcla con el progreso de
    // "guardar movimiento", que puede estar corriendo al mismo tiempo en la otra pantalla.
    private static final String TOPIC_DETALLE = "/topic/movimiento-inventario-detalle-progreso";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void enviarProgreso(String usuario, Integer paso, String mensaje, Integer porcentaje, Long movimientoId) {
        if (usuario == null || usuario.isBlank()) return;
        try {
            MovimientoInventarioProgresoDTO dto = new MovimientoInventarioProgresoDTO(
                    usuario, paso, mensaje, porcentaje, movimientoId, "PROCESANDO");
            messagingTemplate.convertAndSend(TOPIC + "/" + usuario, dto);
            log.debug("[WS-MovInv] Usuario={} Paso={} {}% {}", usuario, paso, porcentaje, mensaje);
        } catch (Exception ex) {
            log.warn("[WS-MovInv] Error enviando progreso para {}: {}", usuario, ex.getMessage());
        }
    }

    public void enviarCompletado(String usuario, Long movimientoId) {
        if (usuario == null || usuario.isBlank()) return;
        try {
            MovimientoInventarioProgresoDTO dto = new MovimientoInventarioProgresoDTO(
                    usuario, 7, "Movimiento registrado correctamente", 100, movimientoId, "COMPLETADO");
            messagingTemplate.convertAndSend(TOPIC + "/" + usuario, dto);
            log.info("[WS-MovInv] Completado usuario={} movimientoId={}", usuario, movimientoId);
        } catch (Exception ex) {
            log.warn("[WS-MovInv] Error enviando completado para {}: {}", usuario, ex.getMessage());
        }
    }

    public void enviarError(String usuario, String mensajeError, Long movimientoId) {
        if (usuario == null || usuario.isBlank()) return;
        try {
            MovimientoInventarioProgresoDTO dto = new MovimientoInventarioProgresoDTO(
                    usuario, null, mensajeError, null, movimientoId, "ERROR");
            messagingTemplate.convertAndSend(TOPIC + "/" + usuario, dto);
            log.warn("[WS-MovInv] Error para usuario={}: {}", usuario, mensajeError);
        } catch (Exception ex) {
            log.warn("[WS-MovInv] Error enviando error WS para {}: {}", usuario, ex.getMessage());
        }
    }

    // ── Progreso de "ver detalle" de un movimiento (GET /{id}) ──
    public void enviarProgresoDetalle(String usuario, Integer paso, String mensaje, Integer porcentaje, Long movimientoId) {
        if (usuario == null || usuario.isBlank()) return;
        try {
            MovimientoInventarioProgresoDTO dto = new MovimientoInventarioProgresoDTO(
                    usuario, paso, mensaje, porcentaje, movimientoId, "PROCESANDO");
            messagingTemplate.convertAndSend(TOPIC_DETALLE + "/" + usuario, dto);
            log.debug("[WS-MovInv-Detalle] Usuario={} Paso={} {}% {}", usuario, paso, porcentaje, mensaje);
        } catch (Exception ex) {
            log.warn("[WS-MovInv-Detalle] Error enviando progreso para {}: {}", usuario, ex.getMessage());
        }
    }

    public void enviarCompletadoDetalle(String usuario, Long movimientoId) {
        if (usuario == null || usuario.isBlank()) return;
        try {
            MovimientoInventarioProgresoDTO dto = new MovimientoInventarioProgresoDTO(
                    usuario, 2, "Detalle cargado", 100, movimientoId, "COMPLETADO");
            messagingTemplate.convertAndSend(TOPIC_DETALLE + "/" + usuario, dto);
            log.info("[WS-MovInv-Detalle] Completado usuario={} movimientoId={}", usuario, movimientoId);
        } catch (Exception ex) {
            log.warn("[WS-MovInv-Detalle] Error enviando completado para {}: {}", usuario, ex.getMessage());
        }
    }

    public void enviarErrorDetalle(String usuario, String mensajeError, Long movimientoId) {
        if (usuario == null || usuario.isBlank()) return;
        try {
            MovimientoInventarioProgresoDTO dto = new MovimientoInventarioProgresoDTO(
                    usuario, null, mensajeError, null, movimientoId, "ERROR");
            messagingTemplate.convertAndSend(TOPIC_DETALLE + "/" + usuario, dto);
            log.warn("[WS-MovInv-Detalle] Error para usuario={}: {}", usuario, mensajeError);
        } catch (Exception ex) {
            log.warn("[WS-MovInv-Detalle] Error enviando error WS para {}: {}", usuario, ex.getMessage());
        }
    }
}
