package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.entity.PeriodoContable;
import com.pazzioliweb.comprobantesmodule.repositori.PeriodoContableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Gestión del cierre contable mensual. El comportamiento por defecto es:
 * todos los periodos están ABIERTOS hasta que el admin los cierre. Así
 * NO se rompen movimientos existentes — solo aplica a futuros intentos
 * de escribir asientos en periodos ya cerrados manualmente.
 */
@Service
public class PeriodoContableService {

    private final PeriodoContableRepository repo;

    public PeriodoContableService(PeriodoContableRepository repo) {
        this.repo = repo;
    }

    /**
     * Verifica si una fecha cae en un periodo cerrado. Si no existe el
     * periodo en BD, se considera ABIERTO (compatibilidad hacia atrás).
     */
    @Transactional(readOnly = true)
    public boolean estaCerrado(LocalDate fecha) {
        if (fecha == null) return false;
        Optional<PeriodoContable> p = repo.findByAnioAndMes(fecha.getYear(), fecha.getMonthValue());
        return p.isPresent() && "CERRADO".equals(p.get().getEstado());
    }

    @Transactional(readOnly = true)
    public Optional<PeriodoContable> obtener(int anio, int mes) {
        return repo.findByAnioAndMes(anio, mes);
    }

    @Transactional(readOnly = true)
    public List<PeriodoContable> listarTodos() {
        return repo.findAllByOrderByAnioDescMesDesc();
    }

    @Transactional(readOnly = true)
    public List<PeriodoContable> listarPorAnio(int anio) {
        return repo.findByAnioOrderByMesAsc(anio);
    }

    /**
     * Asegura que existan filas para los 12 meses de un año dado.
     * No falla si ya existen (idempotente).
     */
    @Transactional
    public List<PeriodoContable> asegurarPeriodosDelAnio(int anio) {
        for (int mes = 1; mes <= 12; mes++) {
            Optional<PeriodoContable> existente = repo.findByAnioAndMes(anio, mes);
            if (existente.isEmpty()) {
                PeriodoContable p = new PeriodoContable();
                p.setAnio(anio);
                p.setMes(mes);
                p.setEstado("ABIERTO");
                p.setFechaCreacion(LocalDateTime.now());
                repo.save(p);
            }
        }
        return repo.findByAnioOrderByMesAsc(anio);
    }

    @Transactional
    public PeriodoContable cerrar(int anio, int mes, String usuario, String observaciones) {
        PeriodoContable p = repo.findByAnioAndMes(anio, mes)
                .orElseGet(() -> {
                    PeriodoContable nuevo = new PeriodoContable();
                    nuevo.setAnio(anio);
                    nuevo.setMes(mes);
                    nuevo.setFechaCreacion(LocalDateTime.now());
                    return nuevo;
                });
        p.setEstado("CERRADO");
        p.setFechaCierre(LocalDateTime.now());
        p.setUsuarioCierre(usuario);
        p.setObservaciones(observaciones);
        return repo.save(p);
    }

    @Transactional
    public PeriodoContable reabrir(int anio, int mes, String usuario, String motivo) {
        PeriodoContable p = repo.findByAnioAndMes(anio, mes)
                .orElseThrow(() -> new IllegalArgumentException("No existe periodo " + anio + "-" + mes));
        p.setEstado("ABIERTO");
        String prev = p.getObservaciones() != null ? p.getObservaciones() + " | " : "";
        p.setObservaciones(prev + "Reapertura " + LocalDateTime.now() + " por " + usuario + ": " + motivo);
        // Mantenemos fechaCierre y usuarioCierre como historial de quién lo cerró antes
        return repo.save(p);
    }
}
