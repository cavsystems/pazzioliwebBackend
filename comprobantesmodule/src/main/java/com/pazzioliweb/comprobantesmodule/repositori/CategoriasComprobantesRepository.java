package com.pazzioliweb.comprobantesmodule.repositori;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioli.comprobantesmodule.dtos.CategoriaComprobanteDTO;
import com.pazzioliweb.comprobantesmodule.entity.CategoriasComprobantes;

public interface CategoriasComprobantesRepository extends JpaRepository<CategoriasComprobantes, Integer>{
	@Query("""
	        SELECT cat.categoria_comprobante_id AS categoria_comprobante_id,
	               cat.nombre AS nombre
	        FROM CategoriasComprobantes cat
	    """)
	    Page<CategoriaComprobanteDTO> listarCategorias(Pageable pageable);
}
