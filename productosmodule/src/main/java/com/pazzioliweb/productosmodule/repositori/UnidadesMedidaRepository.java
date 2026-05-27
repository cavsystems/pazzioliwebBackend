package com.pazzioliweb.productosmodule.repositori;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.productosmodule.entity.Lineas;
import com.pazzioliweb.productosmodule.entity.UnidadesMedida;
import java.util.Optional;

public interface UnidadesMedidaRepository extends JpaRepository<UnidadesMedida, Integer>{
	Optional<UnidadesMedida> findByDescripcion(String descripcion);
	Page<UnidadesMedida>  findByDescripcionContainingIgnoreCase(String descripcion,Pageable pageable);

	Optional<UnidadesMedida> findBySigla(String sigla);

	/** Primer hueco disponible para reusar códigos eliminados. */
	@Query(value =
		"SELECT COALESCE( " +
		"  (SELECT 1 WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE unidad_medida_id = 1)), " +
		"  (SELECT u1.unidad_medida_id + 1 FROM unidades_medida u1 " +
		"   WHERE NOT EXISTS (SELECT 1 FROM unidades_medida u2 WHERE u2.unidad_medida_id = u1.unidad_medida_id + 1) " +
		"     AND u1.unidad_medida_id < (SELECT MAX(unidad_medida_id) FROM unidades_medida) " +
		"   ORDER BY u1.unidad_medida_id ASC LIMIT 1), " +
		"  (SELECT COALESCE(MAX(unidad_medida_id), 0) + 1 FROM unidades_medida) " +
		")",
		nativeQuery = true)
	Integer findPrimerHueco();
}
