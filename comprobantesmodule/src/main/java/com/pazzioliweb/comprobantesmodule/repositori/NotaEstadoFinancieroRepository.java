package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.NotaEstadoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotaEstadoFinancieroRepository extends JpaRepository<NotaEstadoFinanciero, Integer> {
    List<NotaEstadoFinanciero> findByAnioOrderByNumeroAsc(Integer anio);
}
