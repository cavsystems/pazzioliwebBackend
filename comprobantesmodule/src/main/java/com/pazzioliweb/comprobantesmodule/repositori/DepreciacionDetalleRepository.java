package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.DepreciacionDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepreciacionDetalleRepository extends JpaRepository<DepreciacionDetalle, Long> {

    List<DepreciacionDetalle> findByAnioAndMes(Integer anio, Integer mes);

    void deleteByAnioAndMes(Integer anio, Integer mes);
}
