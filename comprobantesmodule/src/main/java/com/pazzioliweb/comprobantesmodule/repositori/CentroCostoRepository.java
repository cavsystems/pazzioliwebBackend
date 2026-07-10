package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.CentroCosto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CentroCostoRepository extends JpaRepository<CentroCosto, Integer> {
    List<CentroCosto> findAllByOrderByNombreAsc();
}
