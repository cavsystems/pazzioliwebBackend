package com.pazzioliweb.tesoreriamodule.scheduler;

import com.pazzioliweb.commonbacken.conexiondb.MultiTenantDataSource;
import com.pazzioliweb.commonbacken.conexiondb.TenantContext;
import com.pazzioliweb.tesoreriamodule.service.CarteraVencimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Tarea programada que marca la cartera VENCIDA todos los días.
 *
 * Recorre cada tenant activo (excluye 'administrador') y, dentro de cada uno,
 * pone en estado VENCIDA las CxC/CxP cuya fecha de vencimiento ya pasó y aún
 * tienen saldo. Se apoya en el mismo patrón multi-tenant que CajaMidnightScheduler.
 */
@Component
public class CarteraVencimientoScheduler {

    @Autowired
    private CarteraVencimientoService carteraVencimientoService;

    @Autowired
    private MultiTenantDataSource multiTenantDataSource;

    /** Todos los días a las 00:15 hora Colombia (después del cierre de caja de medianoche). */
    @Scheduled(cron = "0 15 0 * * *", zone = "America/Bogota")
    public void marcarCarteraVencida() {
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Bogota"));
        System.out.println("⏰ [CARTERA] Marcando cuentas vencidas al " + hoy + " ...");
        for (String tenant : multiTenantDataSource.getActiveTenantIds()) {
            try {
                TenantContext.setCurrentTenant(tenant);
                int[] r = carteraVencimientoService.marcarVencidas(hoy);
                if (r[0] > 0 || r[1] > 0) {
                    System.out.println("✅ [CARTERA][" + tenant + "] CxC vencidas: " + r[0] + " | CxP vencidas: " + r[1]);
                }
            } catch (Exception e) {
                System.out.println("❌ [CARTERA][" + tenant + "] Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                TenantContext.clear();
            }
        }
        System.out.println("⏰ [CARTERA] Marcado de vencidas finalizado.");
    }
}
