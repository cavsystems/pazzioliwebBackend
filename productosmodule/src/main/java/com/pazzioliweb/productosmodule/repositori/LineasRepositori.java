package com.pazzioliweb.productosmodule.repositori;


import org.springframework.data.domain.Pageable;


import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.productosmodule.entity.Lineas;
import java.util.Optional;

public interface LineasRepositori extends JpaRepository<Lineas,Integer>  {
Page<Lineas>  findByDescripcionContainingIgnoreCase(String descripcion,Pageable pageable);

Optional<Lineas> findByDescripcion(String descripcion);

/** Primer hueco disponible para reusar códigos eliminados.
 *  Detecta huecos al inicio (id=1 borrado), en medio, y si no hay → MAX+1. */
@Query(value =
	"SELECT COALESCE( " +
	"  (SELECT 1 WHERE NOT EXISTS (SELECT 1 FROM lineas WHERE linea_id = 1)), " +
	"  (SELECT l1.linea_id + 1 FROM lineas l1 " +
	"   WHERE NOT EXISTS (SELECT 1 FROM lineas l2 WHERE l2.linea_id = l1.linea_id + 1) " +
	"     AND l1.linea_id < (SELECT MAX(linea_id) FROM lineas) " +
	"   ORDER BY l1.linea_id ASC LIMIT 1), " +
	"  (SELECT COALESCE(MAX(linea_id), 0) + 1 FROM lineas) " +
	")",
	nativeQuery = true)
Integer findPrimerHueco();
}
