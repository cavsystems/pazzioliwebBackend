package com.pazzioliweb.cajerosmodule.scheduler;

import com.pazzioliweb.cajerosmodule.service.DetalleCajeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tarea programada que se ejecuta a medianoche (00:00:00) en zona horaria America/Bogota.
 *
 * Cierra TODAS las sesiones de caja que sigan abiertas del día anterior
 * y crea automáticamente una nueva sesión para el nuevo día.
 *
 * Esto garantiza que cada sesión de caja (detalle_cajero) abarque como máximo 24 horas,
 * facilitando el informe diario de ventas (Reporte Z / Cuadre de Caja).
 */
@Component
public class CajaMidnightScheduler {

    @Autowired
    private DetalleCajeroService detalleCajeroService;

    /**
     * Se ejecuta todos los días a las 00:00:00 hora Colombia (America/Bogota).
     * Cierra sesiones del día anterior y reabre nuevas para el nuevo día.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "America/Bogota")
    public void cerrarYReabrirSesionesAlMedianoche() {
        System.out.println("⏰ [MEDIANOCHE] Iniciando cierre automático de sesiones de caja...");
        try {
            int sesionesReabiertas = detalleCajeroService.cerrarYReabrirSesionesMedianoche();
            System.out.println("✅ [MEDIANOCHE] " + sesionesReabiertas + " sesión(es) cerrada(s) y reabierta(s).");
        } catch (Exception e) {
            System.out.println("❌ [MEDIANOCHE] Error al cerrar sesiones: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

