package com.pazzioliweb.parametros.repository;

import com.pazzioliweb.parametros.entity.Parametroscomprobantes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParametroscomprobantesRepository extends JpaRepository<Parametroscomprobantes, Integer> {
    List<Parametroscomprobantes> findByParametrosId(Long parametroId);
    Optional<Parametroscomprobantes> findByParametrosIdAndComprobanteContableId(Long parametroId, Integer comprobanteContableId);
}
