package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.PeriodoContable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PeriodoContableRepository extends JpaRepository<PeriodoContable, Long> {

    Optional<PeriodoContable> findByAnioAndMes(Integer anio, Integer mes);

    List<PeriodoContable> findByAnioOrderByMesAsc(Integer anio);

    List<PeriodoContable> findAllByOrderByAnioDescMesDesc();
}
