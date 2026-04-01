package com.pazzioliweb.metodospagomodule.repositori;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.metodospagomodule.dtos.TipoTotalDTO;
import com.pazzioliweb.metodospagomodule.entity.TipoTotales;

public interface TipoTotalesRepository extends JpaRepository<TipoTotales, Integer>{
	@Query("""
			SELECT 
				t.tipo_total_id AS tipo_total_id,
				t.descripcion AS descripcion,
				t.sigla AS sigla,
				t.estado AS estado,
				t.tipo AS tipo
			FROM TipoTotales AS t
			""")
	Page<TipoTotalDTO> listadoTipoTotalesDTO(Pageable pageable);
}
