package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.entity.PeriodoContable;
import com.pazzioliweb.comprobantesmodule.repositori.PeriodoContableRepository;
import com.pazzioliweb.commonbacken.util.PasswordUtils;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;

    public PeriodoContableService(PeriodoContableRepository repo, UsuarioRepository usuarioRepository) {
        this.repo = repo;
        this.usuarioRepository = usuarioRepository;
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

    /**
     * Valida que el periodo de la fecha esté abierto. Si está cerrado lanza
     * IllegalStateException que sube como BadRequest al cliente. Usar al
     * inicio de cada operación de negocio (venta, compra, devolución, etc.)
     * para evitar estados inconsistentes donde la operación se persiste
     * pero su asiento contable es rechazado.
     */
    @Transactional(readOnly = true)
    public void validarPeriodoAbierto(LocalDate fecha) {
        if (fecha != null && estaCerrado(fecha)) {
            throw new IllegalStateException(
                "El periodo contable " + fecha.getMonthValue() + "/" + fecha.getYear()
                + " está CERRADO. No se pueden registrar nuevas operaciones en esa fecha. " +
                "Solicite al administrador reabrir el periodo o use una fecha en un periodo abierto."
            );
        }
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

    /**
     * Reapertura con clave especial: valida la contraseña del usuario que
     * autoriza (misma doble lógica del login: hash BCrypt o texto plano legado)
     * antes de reabrir. El cierre nunca es definitivo; se puede levantar con clave.
     */
    @Transactional
    public PeriodoContable reabrirValidado(int anio, int mes, String usuario, String motivo, String password) {
        if (usuario == null || usuario.isBlank() || password == null || password.isBlank())
            throw new IllegalStateException("Debe indicar usuario y contraseña para reabrir el periodo.");
        Usuario u = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + usuario));
        String guardada = u.getContrasena();
        boolean ok = guardada != null && (PasswordUtils.matches(password, guardada) || password.equals(guardada));
        if (!ok) throw new IllegalStateException("Contraseña incorrecta. No autorizado para reabrir el periodo.");
        return reabrir(anio, mes, usuario, motivo);
    }

    /**
     * Cierra un grupo consecutivo de meses (bimestre=2, trimestre=3,
     * cuatrimestre=4) empezando en mesInicio. Valida que los meses anteriores
     * del año estén cerrados, y cierra todo el grupo de forma atómica.
     */
    @Transactional
    public List<PeriodoContable> cerrarGrupo(int anio, int mesInicio, int tamano, String usuario, String observaciones) {
        if (tamano < 1) tamano = 1;
        int mesFin = mesInicio + tamano - 1;
        if (mesInicio < 1 || mesFin > 12)
            throw new IllegalStateException("El grupo de meses (" + mesInicio + "-" + mesFin + ") está fuera del año.");
        // Validar secuencia: todos los meses anteriores al grupo deben estar cerrados.
        for (int m = 1; m < mesInicio; m++) {
            if (!estaCerrado(LocalDate.of(anio, m, 1)))
                throw new IllegalStateException("No se puede cerrar desde el mes " + mesInicio +
                        ": el mes " + m + " aún está abierto. Cierre los periodos en orden.");
        }
        List<PeriodoContable> cerrados = new java.util.ArrayList<>();
        for (int m = mesInicio; m <= mesFin; m++) {
            cerrados.add(cerrar(anio, m, usuario, observaciones));
        }
        return cerrados;
    }
}
