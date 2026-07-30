package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.comprasmodule.dtos.LegalizacionProgresoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class LegalizacionWebSocketService {

    private static final Logger log = LoggerFactory.getLogger(LegalizacionWebSocketService.class);
    
    private static final String TOPIC = "/topic/legalizacion-progreso";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Envía actualización de progreso de legalización a un usuario específico
     * @param usuario Nombre de usuario autenticado
     * @param paso Número del paso actual (1-6)
     * @param mensaje Descripción del paso
     * @param porcentaje Porcentaje de completitud (0-100)
     * @param ordenCompraId ID de la orden siendo procesada
     */
    public void enviarProgreso(String usuario, Integer paso, String mensaje, Integer porcentaje, Long ordenCompraId) {
        try {
            LegalizacionProgresoDTO dto = new LegalizacionProgresoDTO(
                usuario,
                paso,
                mensaje,
                porcentaje,
                ordenCompraId,
                "PROCESANDO"
            );
            
            // Envía al topic específico para este usuario
            String destination = TOPIC + "/" + usuario;
            messagingTemplate.convertAndSend(destination, dto);
            
            log.info("[WS-Legalizacion] Usuario: {}, Paso: {}, Porcentaje: {}, Mensaje: {}", 
                usuario, paso, porcentaje, mensaje);
        } catch (Exception ex) {
            log.warn("[WS-Legalizacion] Error al enviar progreso para usuario {}: {}", usuario, ex.getMessage());
        }
    }

    /**
     * Envía notificación de completado exitoso
     */
    public void enviarCompletado(String usuario, Long ordenCompraId) {
        try {
            LegalizacionProgresoDTO dto = new LegalizacionProgresoDTO(
                usuario,
                6,
                "Legalización completada exitosamente",
                100,
                ordenCompraId,
                "COMPLETADO"
            );
            
            String destination = TOPIC + "/" + usuario;
            messagingTemplate.convertAndSend(destination, dto);
            
            log.info("[WS-Legalizacion] Completado para usuario: {}, Orden: {}", usuario, ordenCompraId);
        } catch (Exception ex) {
            log.warn("[WS-Legalizacion] Error al enviar completado para usuario {}: {}", usuario, ex.getMessage());
        }
    }

    /**
     * Envía notificación de error
     */
    public void enviarError(String usuario, String mensajeError, Long ordenCompraId) {
        try {
            LegalizacionProgresoDTO dto = new LegalizacionProgresoDTO(
                usuario,
                null,
                mensajeError,
                null,
                ordenCompraId,
                "ERROR"
            );
            
            String destination = TOPIC + "/" + usuario;
            messagingTemplate.convertAndSend(destination, dto);
            
            log.warn("[WS-Legalizacion] Error para usuario: {}, Mensaje: {}", usuario, mensajeError);
        } catch (Exception ex) {
            log.warn("[WS-Legalizacion] Error al enviar error para usuario {}: {}", usuario, ex.getMessage());
        }
    }
}
