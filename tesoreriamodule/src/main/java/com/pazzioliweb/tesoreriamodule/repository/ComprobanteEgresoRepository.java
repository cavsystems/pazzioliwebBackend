package com.pazzioliweb.tesoreriamodule.repository;

import com.pazzioliweb.tesoreriamodule.entity.ComprobanteEgreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComprobanteEgresoRepository extends JpaRepository<ComprobanteEgreso, Long> {

    @Query("SELECT COALESCE(MAX(c.consecutivo), 0) FROM ComprobanteEgreso c")
    Integer findMaxConsecutivo();

    Optional<ComprobanteEgreso> findTopByOrderByIdDesc();
}

