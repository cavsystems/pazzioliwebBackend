package com.pazzioliweb.commonbacken.repositorio;

import com.pazzioliweb.commonbacken.entity.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface NotificacionRepositori extends JpaRepository<Notificacion, Long> {

    Page<Notificacion> findAllByOrderByFechaCreacionDesc(Pageable pageable);

    Page<Notificacion> findByLeidaFalseOrderByFechaCreacionDesc(Pageable pageable);

    long countByLeidaFalse();

    /** Idempotencia para jobs diarios (licencia, cartera): evita crear la misma
     *  notificación varias veces si el scheduler corre más de una vez el mismo día. */
    boolean existsByTipoAndFechaCreacionBetween(String tipo, LocalDateTime desde, LocalDateTime hasta);
}
