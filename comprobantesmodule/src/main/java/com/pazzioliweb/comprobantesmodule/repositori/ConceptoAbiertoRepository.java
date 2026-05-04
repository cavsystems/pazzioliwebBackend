package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.ConceptoAbierto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConceptoAbiertoRepository extends JpaRepository<ConceptoAbierto, Long> {

    /** Devuelve todos los activos cuyo tipo coincida con el solicitado o sea AMBOS. */
    @Query("""
            SELECT c FROM ConceptoAbierto c
             WHERE c.estado = 'ACTIVO'
               AND (c.tipo = :tipo OR c.tipo = 'AMBOS')
             ORDER BY c.descripcion ASC
            """)
    List<ConceptoAbierto> listarPorTipo(@Param("tipo") String tipo);

    List<ConceptoAbierto> findByEstadoOrderByDescripcionAsc(String estado);
}
