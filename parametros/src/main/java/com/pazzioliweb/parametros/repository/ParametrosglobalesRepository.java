package com.pazzioliweb.parametros.repository;

import com.pazzioliweb.parametros.entity.Parametrosglobales;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParametrosglobalesRepository extends JpaRepository<Parametrosglobales, Integer> {
    Optional<Parametrosglobales> findByParametrosId(Long parametroId);
}
