package com.pazzioliweb.commonbacken.services;

import com.pazzioliweb.commonbacken.entity.Notificacion;
import com.pazzioliweb.commonbacken.repositorio.NotificacionRepositori;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class NotificacionService {

    private final NotificacionRepositori repo;

    public NotificacionService(NotificacionRepositori repo) {
        this.repo = repo;
    }

    @Transactional
    public Notificacion crear(String tipo, String titulo, String mensaje, String entidadTipo, Long entidadId) {
        Notificacion n = new Notificacion();
        n.setTipo(tipo);
        n.setTitulo(titulo);
        n.setMensaje(mensaje);
        n.setEntidadTipo(entidadTipo);
        n.setEntidadId(entidadId);
        n.setLeida(false);
        n.setFechaCreacion(LocalDateTime.now());
        return repo.save(n);
    }

    /**
     * Igual que {@link #crear}, pero no duplica si ya se creó una notificación del
     * mismo tipo HOY. Pensado para jobs programados (licencia por vencer, cartera
     * vencida) que corren una vez al día — evita spam si el scheduler se reintenta
     * o si el backend se reinicia varias veces el mismo día.
     */
    @Transactional
    public void crearSiNoExisteHoy(String tipo, String titulo, String mensaje, String entidadTipo, Long entidadId) {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = inicioHoy.plusDays(1);
        if (repo.existsByTipoAndFechaCreacionBetween(tipo, inicioHoy, finHoy)) return;
        crear(tipo, titulo, mensaje, entidadTipo, entidadId);
    }

    @Transactional(readOnly = true)
    public Page<Notificacion> listar(boolean soloNoLeidas, Pageable pageable) {
        return soloNoLeidas
                ? repo.findByLeidaFalseOrderByFechaCreacionDesc(pageable)
                : repo.findAllByOrderByFechaCreacionDesc(pageable);
    }

    @Transactional(readOnly = true)
    public long contarNoLeidas() {
        return repo.countByLeidaFalse();
    }

    @Transactional
    public void marcarLeida(Long id) {
        repo.findById(id).ifPresent(n -> {
            if (Boolean.TRUE.equals(n.getLeida())) return;
            n.setLeida(true);
            n.setFechaLeida(LocalDateTime.now());
            repo.save(n);
        });
    }

    @Transactional
    public void marcarTodasLeidas() {
        LocalDateTime ahora = LocalDateTime.now();
        repo.findByLeidaFalseOrderByFechaCreacionDesc(Pageable.unpaged()).forEach(n -> {
            n.setLeida(true);
            n.setFechaLeida(ahora);
            repo.save(n);
        });
    }
}
