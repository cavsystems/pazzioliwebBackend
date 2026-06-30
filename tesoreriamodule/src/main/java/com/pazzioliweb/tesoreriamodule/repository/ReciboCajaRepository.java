package com.pazzioliweb.tesoreriamodule.repository;

import com.pazzioliweb.tesoreriamodule.entity.ReciboCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReciboCajaRepository extends JpaRepository<ReciboCaja, Long> {

    @Query("SELECT COALESCE(MAX(r.consecutivo), 0) FROM ReciboCaja r")
    Integer findMaxConsecutivo();

    Optional<ReciboCaja> findTopByOrderByIdDesc();

    List<ReciboCaja> findAllByOrderByFechaCreacionDesc();

    @Query("SELECT r FROM ReciboCaja r WHERE " +
           "(:desde IS NULL OR r.fechaRecibo >= :desde) AND " +
           "(:hasta IS NULL OR r.fechaRecibo <= :hasta) " +
           "ORDER BY r.fechaCreacion DESC")
    List<ReciboCaja> findByFechaRango(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    @Query(value = "SELECT nombre FROM vendedores WHERE vendedor_id = :id", nativeQuery = true)
    Optional<String> findVendedorNombreById(@Param("id") Integer id);
}

