package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.AsientoFallido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsientoFallidoRepository extends JpaRepository<AsientoFallido, Long> {
    List<AsientoFallido> findByResueltoFalseOrderByFechaIntentoDesc();
    List<AsientoFallido> findByModuloOrderByFechaIntentoDesc(String modulo);
}
