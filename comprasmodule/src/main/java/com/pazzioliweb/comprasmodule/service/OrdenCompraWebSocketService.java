package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.comprasmodule.dtos.LegalizacionProgresoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrdenCompraWebSocketService {

    private static final Logger log = LoggerFactory.getLogger(OrdenCompraWebSocketService.class);
    
    private static final String TOPIC = "/topic/orden-compra-progreso";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Envía actualización de progreso de orden de compra a un usuario específico
     * @param usuario Nombre de usuario autenticado
     * @param paso Número del paso actual
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
            
            String destination = TOPIC + "/" + usuario;
            messagingTemplate.convertAndSend(destination, dto);
            
            log.info("[WS-OrdenCompra] Usuario: {}, Paso: {}, Porcentaje: {}, Mensaje: {}", 
                usuario, paso, porcentaje, mensaje);
        } catch (Exception ex) {
            log.warn("[WS-OrdenCompra] Error al enviar progreso para usuario {}: {}", usuario, ex.getMessage());
        }
    }

    /**
     * Envía notificación de completado exitoso
     */
    public void enviarCompletado(String usuario, Long ordenCompraId) {
        try {
            LegalizacionProgresoDTO dto = new LegalizacionProgresoDTO(
                usuario,
                null,
                "Orden de compra creada exitosamente",
                100,
                ordenCompraId,
                "COMPLETADO"
            );
            
            String destination = TOPIC + "/" + usuario;
            messagingTemplate.convertAndSend(destination, dto);
            
            log.info("[WS-OrdenCompra] Completado para usuario: {}, Orden: {}", usuario, ordenCompraId);
        } catch (Exception ex) {
            log.warn("[WS-OrdenCompra] Error al enviar completado para usuario {}: {}", usuario, ex.getMessage());
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
            
            log.warn("[WS-OrdenCompra] Error para usuario: {}, Mensaje: {}", usuario, mensajeError);
        } catch (Exception ex) {
            log.warn("[WS-OrdenCompra] Error al enviar error para usuario {}: {}", usuario, ex.getMessage());
        }
    }
}
