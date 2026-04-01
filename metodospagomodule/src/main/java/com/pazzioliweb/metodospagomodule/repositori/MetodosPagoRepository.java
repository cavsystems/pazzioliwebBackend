package com.pazzioliweb.metodospagomodule.repositori;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.metodospagomodule.dtos.MetodoPagoDTO;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;

public interface MetodosPagoRepository extends JpaRepository<MetodosPago, Integer>{
	@Query("""
			SELECT 
				mp.metodo_pago_id AS metodo_pago_id,
				mp.descripcion AS descripcion,
				mp.sigla AS sigla,
				mp.estado AS estado,
				mp.tipoNegociacion AS tipoNegociacion
			FROM MetodosPago mp
			""")
	Page<MetodoPagoDTO> listadoMetodosPagoDTO(Pageable pageable);
}
