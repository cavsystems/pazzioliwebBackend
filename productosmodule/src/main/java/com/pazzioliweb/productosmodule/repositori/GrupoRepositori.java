package com.pazzioliweb.productosmodule.repositori;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.productosmodule.entity.Grupos;
import com.pazzioliweb.productosmodule.entity.Lineas;
import java.util.Optional;

public interface GrupoRepositori extends JpaRepository<Grupos,Integer>  {
	Page<Grupos>  findByDescripcionContainingIgnoreCase(String descripcion,Pageable pageable);

	Optional<Grupos> findByDescripcion(String descripcion);

	/**
	 * Devuelve el primer ID disponible (1 si no hay, o el menor hueco entre 1 y MAX).
	 * Permite que los códigos visibles al usuario sean secuenciales sin saltos
	 * cuando se eliminan y vuelven a crear registros.
	 */
	@Query(value =
		"SELECT COALESCE( " +
		"  (SELECT 1 WHERE NOT EXISTS (SELECT 1 FROM grupos WHERE grupo_id = 1)), " +
		"  (SELECT g1.grupo_id + 1 FROM grupos g1 " +
		"   WHERE NOT EXISTS (SELECT 1 FROM grupos g2 WHERE g2.grupo_id = g1.grupo_id + 1) " +
		"     AND g1.grupo_id < (SELECT MAX(grupo_id) FROM grupos) " +
		"   ORDER BY g1.grupo_id ASC LIMIT 1), " +
		"  (SELECT COALESCE(MAX(grupo_id), 0) + 1 FROM grupos) " +
		")",
		nativeQuery = true)
	Integer findPrimerHueco();
}
