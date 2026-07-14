package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.ActivoFijo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivoFijoRepository extends JpaRepository<ActivoFijo, Long> {

    List<ActivoFijo> findByEstadoOrderByNombreAsc(String estado);

    List<ActivoFijo> findAllByOrderByFechaAdquisicionDesc();
}
