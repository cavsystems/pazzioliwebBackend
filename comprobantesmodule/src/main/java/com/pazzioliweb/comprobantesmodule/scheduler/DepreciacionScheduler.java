package com.pazzioliweb.comprobantesmodule.scheduler;

import com.pazzioliweb.commonbacken.conexiondb.MultiTenantDataSource;
import com.pazzioliweb.commonbacken.conexiondb.TenantContext;
import com.pazzioliweb.comprobantesmodule.service.ActivoFijoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Corre automáticamente la depreciación del MES ANTERIOR el día 2 de cada mes,
 * para todos los tenants. Es idempotente: si el mes ya se corrió manualmente,
 * la operación se salta (no duplica). Si un tenant no tiene activos por depreciar,
 * simplemente se ignora sin marcar error.
 */
@Component
public class DepreciacionScheduler {

    @Autowired
    private ActivoFijoService activoFijoService;

    @Autowired
    private MultiTenantDataSource multiTenantDataSource;

    /** Día 2 de cada mes a las 01:00 hora Colombia. */
    @Scheduled(cron = "0 0 1 2 * *", zone = "America/Bogota")
    public void depreciarMesAnterior() {
        LocalDate mesAnterior = LocalDate.now(ZoneId.of("America/Bogota")).minusMonths(1);
        int anio = mesAnterior.getYear();
        int mes = mesAnterior.getMonthValue();
        System.out.println("⏰ [DEPRECIACIÓN] Corriendo depreciación de " + mes + "/" + anio + " para todos los tenants...");
        for (String tenant : multiTenantDataSource.getActiveTenantIds()) {
            try {
                TenantContext.setCurrentTenant(tenant);
                ActivoFijoService.ResultadoDepreciacion r = activoFijoService.correrDepreciacion(anio, mes, false);
                System.out.println("✅ [DEPRECIACIÓN][" + tenant + "] asiento " + r.numeroAsiento
                        + " · " + r.activosDepreciados + " activo(s) · total " + r.totalDepreciado);
            } catch (Exception e) {
                // "ya registrada" / "no hay activos por depreciar" son casos normales, no fallas.
                System.out.println("ℹ️ [DEPRECIACIÓN][" + tenant + "] " + e.getMessage());
            } finally {
                TenantContext.clear();
            }
        }
        System.out.println("⏰ [DEPRECIACIÓN] Fin de la corrida mensual.");
    }
}
