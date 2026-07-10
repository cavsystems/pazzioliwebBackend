package com.pazzioliweb.cajerosmodule.scheduler;

import com.pazzioliweb.cajerosmodule.service.DetalleCajeroService;
import com.pazzioliweb.commonbacken.conexiondb.MultiTenantDataSource;
import com.pazzioliweb.commonbacken.conexiondb.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tarea programada que se ejecuta a medianoche (00:00:00) en zona horaria America/Bogota.
 *
 * Cierra TODAS las sesiones de caja que sigan abiertas del día anterior
 * y crea automáticamente una nueva sesión para el nuevo día, para TODOS los tenants.
 */
@Component
public class CajaMidnightScheduler {

    @Autowired
    private DetalleCajeroService detalleCajeroService;

    @Autowired
    private MultiTenantDataSource multiTenantDataSource;

    /**
     * Se ejecuta todos los días a las 00:00:00 hora Colombia (America/Bogota).
     * Itera sobre cada tenant activo (excluye 'administrador') y cierra las sesiones abiertas.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "America/Bogota")
    public void cerrarYReabrirSesionesAlMedianoche() {
        System.out.println("⏰ [MEDIANOCHE] Iniciando cierre automático de sesiones de caja...");
        for (String tenant : multiTenantDataSource.getActiveTenantIds()) {
            try {
                TenantContext.setCurrentTenant(tenant);
                int cerradas = detalleCajeroService.cerrarYReabrirSesionesMedianoche();
                System.out.println("✅ [MEDIANOCHE][" + tenant + "] " + cerradas + " sesión(es) cerrada(s) y reabierta(s).");
            } catch (Exception e) {
                System.out.println("❌ [MEDIANOCHE][" + tenant + "] Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                TenantContext.clear();
            }
        }
    }
}

