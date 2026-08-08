package com.pazzioliweb.empresaauth.scheduler;

import com.pazzioliweb.commonbacken.conexiondb.MultiTenantDataSource;
import com.pazzioliweb.commonbacken.conexiondb.TenantContext;
import com.pazzioliweb.commonbacken.services.NotificacionService;
import com.pazzioliweb.empresasback.entity.Empresa;
import com.pazzioliweb.empresasback.entity.Estado;
import com.pazzioliweb.empresasback.repositori.EmpresaRepositori;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Tarea programada que avisa cuando la licencia de una empresa está por vencer
 * (o ya venció). Antes no existía ningún job para esto — el vencimiento solo se
 * descubría si alguien entraba a revisar manualmente la ficha de la empresa.
 *
 * Recorre cada tenant activo (mismo patrón que CarteraVencimientoScheduler /
 * CajaMidnightScheduler / DepreciacionScheduler) y, dentro de cada uno, revisa
 * la única fila de `empresa` de ese schema.
 */
@Component
public class LicenciaVencimientoScheduler {

    /** Ventana de aviso: se notifica todos los días mientras falten <= 7 días
     *  para el vencimiento, y también los días posteriores a que ya venció
     *  (crearSiNoExisteHoy evita duplicar el aviso el mismo día). */
    private static final long DIAS_ANTICIPACION = 7;

    @Autowired
    private EmpresaRepositori empresaRepositori;

    @Autowired
    private MultiTenantDataSource multiTenantDataSource;

    @Autowired
    private NotificacionService notificacionService;

    /** Todos los días a la 1:00am hora Colombia. */
    @Scheduled(cron = "0 0 1 * * *", zone = "America/Bogota")
    public void revisarVencimientos() {
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Bogota"));
        System.out.println("⏰ [LICENCIA] Revisando vencimientos de licencia al " + hoy + " ...");
        for (String tenant : multiTenantDataSource.getActiveTenantIds()) {
            try {
                TenantContext.setCurrentTenant(tenant);
                revisarTenant(tenant, hoy);
            } catch (Exception e) {
                System.out.println("❌ [LICENCIA][" + tenant + "] Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                TenantContext.clear();
            }
        }
        System.out.println("⏰ [LICENCIA] Revisión de vencimientos finalizada.");
    }

    private void revisarTenant(String tenant, LocalDate hoy) {
        List<Empresa> empresas = empresaRepositori.findAll();
        for (Empresa empresa : empresas) {
            if (empresa.getFechafinallicencia() == null) continue;
            if (empresa.getEstado() != null && empresa.getEstado() != Estado.ACTIVA) continue;

            long diasParaVencer = ChronoUnit.DAYS.between(hoy, empresa.getFechafinallicencia());
            if (diasParaVencer > DIAS_ANTICIPACION) continue; // aún falta mucho, no avisar todavía

            String mensaje;
            if (diasParaVencer < 0) {
                mensaje = "La licencia venció hace " + (-diasParaVencer) + " día(s) (" + empresa.getFechafinallicencia() + ").";
            } else if (diasParaVencer == 0) {
                mensaje = "La licencia vence HOY (" + empresa.getFechafinallicencia() + ").";
            } else {
                mensaje = "La licencia vence en " + diasParaVencer + " día(s) (" + empresa.getFechafinallicencia() + ").";
            }
            System.out.println("⚠️ [LICENCIA][" + tenant + "] " + mensaje);
            notificacionService.crearSiNoExisteHoy(
                    "LICENCIA_POR_VENCER",
                    diasParaVencer < 0 ? "Licencia vencida" : "Licencia por vencer",
                    mensaje,
                    "EMPRESA",
                    (long) empresa.getCodigo()
            );
        }
    }
}
