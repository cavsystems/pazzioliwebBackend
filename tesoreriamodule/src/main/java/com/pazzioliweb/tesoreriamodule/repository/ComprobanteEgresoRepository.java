package com.pazzioliweb.tesoreriamodule.repository;

import com.pazzioliweb.tesoreriamodule.entity.ComprobanteEgreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComprobanteEgresoRepository extends JpaRepository<ComprobanteEgreso, Long> {

    @Query("SELECT COALESCE(MAX(c.consecutivo), 0) FROM ComprobanteEgreso c")
    Integer findMaxConsecutivo();

    Optional<ComprobanteEgreso> findTopByOrderByIdDesc();

    List<ComprobanteEgreso> findAllByOrderByFechaCreacionDesc();

    @Query("SELECT c FROM ComprobanteEgreso c WHERE " +
           "(:desde IS NULL OR c.fechaEgreso >= :desde) AND " +
           "(:hasta IS NULL OR c.fechaEgreso <= :hasta) " +
           "ORDER BY c.fechaCreacion DESC")
    List<ComprobanteEgreso> findByFechaEgresoRango(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);
}

