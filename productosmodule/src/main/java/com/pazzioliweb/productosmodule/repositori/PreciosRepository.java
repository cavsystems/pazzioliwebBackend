package com.pazzioliweb.productosmodule.repositori;

import com.pazzioliweb.productosmodule.dtos.OtroprecioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteDTO;
import com.pazzioliweb.productosmodule.entity.Precios;
import com.pazzioliweb.productosmodule.entity.UnidadesMedida;

import java.util.List;
import java.util.Optional;

public interface PreciosRepository extends JpaRepository<Precios, Integer> {
	Optional<Precios> findByDescripcion(String descripcion);
	Page<Precios>  findByDescripcionContainingIgnoreCase(String descripcion,Pageable pageable);
	@Query("""
        SELECT 
            p.precioId AS precioId,
            p.descripcion AS descripcion
        FROM Precios p
        WHERE p.descripcion=:descripciones
    """)
	List<OtroprecioDTO> obtenerPorDescripciones(
			@Param("descripciones") String descripciones
	);
}
