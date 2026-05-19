package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.AsientoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsientoContableRepository extends JpaRepository<AsientoContable, Long> {

    Optional<AsientoContable> findByDocumentoOrigenTipoAndDocumentoOrigenId(
            String documentoOrigenTipo, Long documentoOrigenId);

    List<AsientoContable> findByFechaBetweenOrderByFechaAscIdAsc(LocalDate desde, LocalDate hasta);

    @Query("SELECT a FROM AsientoContable a WHERE a.fecha BETWEEN :desde AND :hasta ORDER BY a.fecha, a.id")
    List<AsientoContable> rangoFechas(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
