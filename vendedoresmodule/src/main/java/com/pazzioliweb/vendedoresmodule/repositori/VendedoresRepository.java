package com.pazzioliweb.vendedoresmodule.repositori;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.vendedoresmodule.dtos.VendedorDTO;
import com.pazzioliweb.vendedoresmodule.entity.Vendedores;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VendedoresRepository extends JpaRepository<Vendedores, Integer> {

	@Query(value = """
	           SELECT (
	               SELECT COUNT(*) FROM pedidos     WHERE vendedor_id = :vendedorId
	           ) + (
	               SELECT COUNT(*) FROM ventas       WHERE vendedor_id = :vendedorId
	           ) + (
	               SELECT COUNT(*) FROM cotizaciones WHERE vendedor_id = :vendedorId
	           ) + (
	               SELECT COUNT(*) FROM facturas     WHERE vendedor_id = :vendedorId
	           ) AS total
	           """, nativeQuery = true)
	Long contarRegistrosAsociados(@Param("vendedorId") Integer vendedorId);
	@Query(value = """
	           SELECT DISTINCT 
	               v.vendedor_id,
	               v.nombre,
	               v.direccion,
	               v.telefono,
	               v.identificacion,
	               v.correo,
	               v.comision,
	               v.meta_ventas,
	               v.tipo_vendedor,
	               v.estado,
	               v.codigo_usuario_creo,
	               v.fechacreado,
	               u.codigo,
	               u.nombre,
	               r.nombre,
	               b.codigo,
	               b.nombre
	           FROM vendedores v
	           LEFT JOIN usuarios_vendedor uv ON v.vendedor_id = uv.vendedor_id
	           LEFT JOIN usuarios u ON uv.usuario_id = u.codigo
	           LEFT JOIN roles r ON u.codigorol = r.codigo
	           LEFT JOIN bodegas b ON v.bodega_id = b.codigo
	           """,
	           nativeQuery = true)
	Page<Object[]> listarVendedoresDTO(Pageable pageable);

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
