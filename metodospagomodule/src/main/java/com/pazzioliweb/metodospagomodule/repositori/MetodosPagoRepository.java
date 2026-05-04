package com.pazzioliweb.metodospagomodule.repositori;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.pazzioliweb.metodospagomodule.dtos.MetodoPagoDTO;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;

public interface MetodosPagoRepository extends JpaRepository<MetodosPago, Integer>{
	@Query("""
			SELECT
				mp.metodo_pago_id AS metodo_pago_id,
				mp.descripcion AS descripcion,
				mp.sigla AS sigla,
				mp.estado AS estado,
				mp.tipoNegociacion AS tipoNegociacion,
				mp.tipos AS tipos
			FROM MetodosPago mp
			""")
	Page<MetodoPagoDTO> listadoMetodosPagoDTO(Pageable pageable);

	/** Lista los métodos activos cuyo CSV "tipos" contenga el tipo solicitado. */
	default List<MetodosPago> listarPorTipo(String tipo) {
		return listarPorTipoConEstado(tipo, MetodosPago.Estado.ACTIVO);
	}

	@Query("""
			SELECT mp FROM MetodosPago mp
			 WHERE mp.estado = :estado
			   AND (LOCATE(:tipo, mp.tipos) > 0)
			 ORDER BY mp.descripcion ASC
			""")
	List<MetodosPago> listarPorTipoConEstado(@Param("tipo") String tipo,
											 @Param("estado") MetodosPago.Estado estado);
}
