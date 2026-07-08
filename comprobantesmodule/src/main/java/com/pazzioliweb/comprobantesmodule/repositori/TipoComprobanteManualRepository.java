package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.TipoComprobanteManual;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoComprobanteManualRepository extends JpaRepository<TipoComprobanteManual, Integer> {
    List<TipoComprobanteManual> findAllByOrderByNombreAsc();
    Optional<TipoComprobanteManual> findByCodigo(String codigo);
}
