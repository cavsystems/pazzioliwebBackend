package com.pazzioliweb.tercerosmodule.repositori;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.tercerosmodule.entity.SedeTercero;

public interface SedeTerceroRepository extends JpaRepository<SedeTercero, Integer> {
	@Query("""
		    SELECT s FROM SedeTercero s
		    LEFT JOIN FETCH s.departamento
		    LEFT JOIN FETCH s.municipio
		    WHERE s.tercero.terceroId = :terceroId
		""")
		Page<SedeTercero> findByTercero_TerceroIdConRelaciones(@Param("terceroId") Integer terceroId, Pageable pageable);
	
	
	
	@Query("""
		    SELECT s FROM SedeTercero s
		    WHERE s.tercero.terceroId = :terceroId
		""")
		List<SedeTercero> findBysedetercero(@Param("terceroId") Integer terceroId);
	
	
	
}
