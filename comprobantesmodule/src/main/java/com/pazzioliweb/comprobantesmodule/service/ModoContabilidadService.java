package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.entity.ConfiguracionContabilidad;
import com.pazzioliweb.comprobantesmodule.repositori.ConfiguracionContabilidadRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * modoPOS — indica si la empresa (tenant) lleva contabilidad y desde qué fecha.
 *
 * Fuente de verdad: la fila única (id=1) de {@code configuracion_contabilidad}. La usan el gate
 * central de asientos ({@code AsientoContableService.generarAsiento}) y el de periodos
 * ({@code PeriodoContableService.estaCerrado}).
 *
 * FAIL-SAFE TOTAL: la LECTURA se hace con JDBC directo (NO JPA) envuelto en try/catch. Ante
 * CUALQUIER error —fila ausente, o incluso la TABLA ausente en un schema al que aún no se le
 * aplicó la migración— se asume contabilidad ACTIVA (comportamiento histórico). Esto es crítico
 * porque el gate se invoca en toda venta/compra/recibo/egreso/movimiento: si la lectura lanzara,
 * el tenant quedaría inoperable. Usar JDBC + catch evita además envenenar (rollback-only) la
 * transacción del caller, cosa que sí ocurriría con una excepción de JPA por tabla inexistente.
 */
@Service
public class ModoContabilidadService {

    private final ConfiguracionContabilidadRepository repo; // solo para la ESCRITURA (guardar)
    private final JdbcTemplate jdbc;                          // para la LECTURA resiliente

    public ModoContabilidadService(ConfiguracionContabilidadRepository repo, JdbcTemplate jdbc) {
        this.repo = repo;
        this.jdbc = jdbc;
    }

    /**
     * Lee [contabilidad_activa, contabilidad_desde] del schema actual de forma resiliente.
     * Retorna null ante cualquier problema (tabla/fila ausente, error de conexión, etc.) → los
     * métodos degradan a contabilidad ACTIVA, sin envenenar la transacción en curso.
     */
    private Object[] leerConfig() {
        try {
            return jdbc.queryForObject(
                "SELECT contabilidad_activa, contabilidad_desde FROM configuracion_contabilidad WHERE id = 1",
                (rs, n) -> new Object[]{ rs.getObject("contabilidad_activa"), rs.getDate("contabilidad_desde") });
        } catch (Exception e) {
            return null; // fail-safe → contabilidad activa (comportamiento histórico)
        }
    }

    /**
     * Interpreta contabilidad_activa de forma resiliente: MySQL devuelve tinyint(1) como
     * Boolean (driver con tinyInt1isBit=true, el default) o como Number según configuración.
     * Antes se casteaba directo a Number → ClassCastException "Boolean cannot be cast to Number".
     */
    private static boolean toBool(Object v) {
        if (v == null) return true;                         // fail-safe ON
        if (v instanceof Boolean) return (Boolean) v;
        if (v instanceof Number) return ((Number) v).intValue() != 0;
        String s = String.valueOf(v).trim();
        return !s.equals("0") && !s.equalsIgnoreCase("false");
    }

    /** true si la empresa lleva contabilidad (default/fail-safe = true). */
    public boolean contabilidadActiva() {
        Object[] r = leerConfig();
        if (r == null || r[0] == null) return true; // fail-safe ON
        return toBool(r[0]);
    }

    /** Fecha de corte de contabilidad, o null si no hay corte. */
    public LocalDate contabilidadDesde() {
        Object[] r = leerConfig();
        if (r == null || r[1] == null) return null;
        return ((java.sql.Date) r[1]).toLocalDate();
    }

    /**
     * ¿Debe generarse contabilidad para un documento con esta fecha?
     * true si contabilidad activa Y (no hay fecha de corte, o la fecha del documento es
     * igual/posterior al corte). Si la fecha del documento es null, no se bloquea (comportamiento actual).
     */
    public boolean esContable(LocalDate fecha) {
        Object[] r = leerConfig();
        if (r == null || r[0] == null) return true; // fail-safe ON
        boolean activa = toBool(r[0]);
        if (!activa) return false;
        LocalDate desde = r[1] != null ? ((java.sql.Date) r[1]).toLocalDate() : null;
        if (desde == null || fecha == null) return true; // sin corte, o sin fecha → contable
        return !fecha.isBefore(desde); // fecha >= desde
    }

    /** Actualiza el flag (usado por la pantalla de administración / módulo de empresas). */
    @Transactional
    public ConfiguracionContabilidad guardar(Boolean activa, LocalDate desde) {
        ConfiguracionContabilidad cfg = repo.findById(1).orElseGet(() -> {
            ConfiguracionContabilidad nuevo = new ConfiguracionContabilidad();
            nuevo.setId(1);
            return nuevo;
        });
        if (activa != null) cfg.setContabilidadActiva(activa);
        cfg.setContabilidadDesde(desde);
        cfg.setFechaActualizacion(LocalDateTime.now());
        return repo.save(cfg);
    }
}
