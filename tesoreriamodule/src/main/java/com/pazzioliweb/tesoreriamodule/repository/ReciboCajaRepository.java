package com.pazzioliweb.tesoreriamodule.repository;

import com.pazzioliweb.tesoreriamodule.entity.ReciboCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReciboCajaRepository extends JpaRepository<ReciboCaja, Long> {

    @Query("SELECT COALESCE(MAX(r.consecutivo), 0) FROM ReciboCaja r")
    Integer findMaxConsecutivo();

    Optional<ReciboCaja> findTopByOrderByIdDesc();
}

