package com.pazzioliweb.vendedoresmodule.repositori;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.vendedoresmodule.dtos.VendedorDTO;
import com.pazzioliweb.vendedoresmodule.entity.Vendedores;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VendedoresRepository extends JpaRepository<Vendedores, Integer>{
	@Query(value = """
	           SELECT DISTINCT v.* 
	           FROM vendedores v
	           INNER JOIN usuarios_vendedor uv ON v.vendedor_id = uv.vendedor_id
	           """,
	           nativeQuery = true,
	           countQuery = """
	           SELECT COUNT(DISTINCT v.vendedor_id) 
	           FROM vendedores v 
	           INNER JOIN usuarios_vendedor uv ON v.vendedor_id = uv.vendedor_id
	           """)
	    Page<Vendedores> listarVendedoresDTO(Pageable pageable);

	/*
	query para buscar todos los vendedores pertenecientes a una sede

	 */
	@Query(value = """
	           SELECT DISTINCT v.* 
	           FROM vendedores v
	           INNER JOIN usuarios_vendedor uv ON v.vendedor_id = uv.vendedor_id
	           INNER JOIN usuarios u ON uv.usuario_id = u.codigo
	           INNER JOIN usuariobodega ub ON u.codigo = ub.usuarioid
	           INNER JOIN bodegas b ON ub.bodegaid = b.codigo
	           WHERE b.codigo = :bodegaId
	           """,
	           nativeQuery = true)
	List<Vendedores> findByBodegaId(@Param("bodegaId") Integer bodegaId);
}
